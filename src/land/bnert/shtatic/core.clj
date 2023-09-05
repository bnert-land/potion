(ns land.bnert.shtatic.core
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [hiccup2.core :as h]
    [org.httpkit.server :as server]
    [markdown.core :as md]))

(def default-shtatic-css
  [["https://cdn.jsdelivr.net/npm/sakura.css/css/sakura.css"]])

(def default-shtatic-pages
  {""          "^pages/index.md$"
   "/about.md" "^pages/about.md$"
   "/posts"    "^pages/posts/[-_A-Za-z0-9]+.md$"})

(defn pattern->prelude [p]
  (reduce #(str %1 "/" %2)
          (drop-last
            (-> p
                (str/replace-first #"\^" "")
                (str/split #"/")))))

(defn expand-pattern-path [reader base pattern]
  (let [pattern'   (re-pattern pattern)
        prelude    (pattern->prelude pattern)
        dir?       (reader prelude)
        file-base? (str/ends-with? base ".md")]
    (when (and (.exists dir?) (.isDirectory dir?))
      (reduce
        (fn [m path]
          (let [file (reader path)]
            (if-not (re-matches pattern' (str file))
              m
              (cond-> m
                (not file-base?)
                  (assoc (str base "/" (.getName file)) file)
                file-base?
                  (assoc base file)
                (= "" base)
                  (-> (assoc "" file)
                      (assoc "/" file))
                (and (not= "" base)
                     (= "index.md" (.getName file)))
                  (-> (assoc base file)
                      (assoc (str base "/") file))))))
        {}
        (file-seq dir?)))))

(defn expand-config [reader {:keys [site]}]
  (reduce-kv
    (fn [m base-path splat-path]
      (into m (or (expand-pattern-path reader base-path splat-path) {})))
    {}
    site))

; Markdown files are automatically wrapped in
; <article></article> tags
(defn render-md [markdown-content {:keys [css] :as _config}]
  (let [{:keys [metadata] :as md} (md/md-to-html-string-with-meta
                                    markdown-content
                                    :heading-anchors true
                                    :refrence-links? true
                                    :footnotes?      true)]
    (str
      (h/html
        [:html {:lang "en-US"}
         (cond-> [:head
                  [:meta {:charset "utf-8"}]]
           (seq css)
             (into (mapv #(vec [:link
                                (into {:rel "stylesheet"
                                       :type "text/css"
                                       :href (first %)}
                                      (or (second %) {}))])
                         css))
           (:title metadata)
             (conj [:title (first (:title metadata))]))
         [:body
          [:article
            (h/raw (:html md))]]]))))


(defn handler [content config]
  (fn [req]
    (if-let [p? (get @content (:uri req))]
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body    (render-md (slurp p?) config)}
      {:status 404
       :headers {"Content-Type" "tekt/html"}
       :body    (render-md "# Not found" config)})))

(defprotocol StartStop
  (start [this] "start")
  (stop [this] "stop"))

(defprotocol Refresh
  (refresh [this] "refresh"))

(defrecord ContentServer [content reader svr port config]
  StartStop
  (start [this]
    (refresh this)
    (when-let [s @svr]
      (s)) ; shutdown
    (reset! svr (server/run-server (handler content config) {:port port}))
    nil)

  (stop [_this]
    (when-let [s @svr]
      (s)) ; shutdown
    (reset! svr nil)
    nil)

  Refresh
  (refresh [_this]
    (swap! content merge (expand-config reader config))
    nil))

(defn paths [site*]
  (-> site* :content deref keys))

(defn site!
  ([] (site! {}))
  ([{:keys      [port config]
     :file/keys [reader]
     :or        {reader io/file
                 port   4076
                 config "shtatic.edn"}}]
   (let [slurped? (try (slurp config) (catch Exception _e "{}"))]
     (ContentServer. (atom {})
                     reader
                     (atom nil)
                     port
                     (-> (edn/read-string slurped?)
                         (update :css  (fnil identity default-shtatic-css))
                         (update :site (fnil identity default-shtatic-pages)))))))

