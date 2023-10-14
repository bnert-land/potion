(ns potion.build
  (:require
    clojure.pprint
    [clojure.edn :as edn]
    [clojure.tools.deps :as t.d]
    [clojure.tools.build.api :as t.b]))

(defn project-aliases []
  (-> (t.d/find-edn-maps) :project-edn :aliases))

(defn clean
  ([]
   (clean nil))
  ([_]
   (t.b/delete {:path "target"})))

;; TODO: make more customizeable
(defn uber [_]
  (let [potion-alias ((project-aliases) :potion {})
        config (-> (select-keys potion-alias [:out-dir :out-name])
                   (update :out-dir  (fnil identity "target"))
                   (update :out-name (fnil identity "potion.jar")))
        basis (t.b/create-basis
                {:project "deps.edn"
                 :extra   (potion-alias :deps {})})]
    (t.b/compile-clj
      {:basis      basis
       :class-dir  (str (config :out-dir) "/classes")
       :ns-compile ['potion.shim]})
    (t.b/uber
      {:basis     basis
       :class-dir (str (config :out-dir) "/classes")
       :uber-file (str (config :out-dir) "/" (config :out-name) ".jar")
       :main      'potion.shim})))

