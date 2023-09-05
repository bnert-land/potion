(ns land.bnert.shtatic.build
  (:require
    [clojure.edn :as edn]
    [clojure.tools.build.api :as build]))

(defn clean
  ([]
   (clean nil))
  ([_]
   (build/delete {:path "target"})))

;; TODO: make more customizeable
(defn uber!
  ([]
   (uber! {}))
  ([{:keys [config]
     :or   {config "shtatic.edn"}}]
   (let [config' (try (edn/read-string (slurp config))
                      (catch Exception _e
                        {}))
         config'   (-> config'
                       (update :out/dir (fnil identity "target"))
                       (update :out/name (fnil identity "shtatic.jar")))
         clsdir    (str (:out/dir config') "/classes")
         uber-file (str (:out/dir config') "/" (:out/name config'))
         basis     (build/create-basis {:project "deps.edn"})]
     (build/copy-dir {:src-dirs   ["pages"]
                      :target-dir clsdir})
     (build/compile-clj {:basis     basis
                         :ns-compile ['land.bnert.shtatic]
                         :class-dir clsdir})
     (build/uber {:class-dir clsdir
                  :uber-file uber-file
                  :basis     basis
                  :main      'land.bnert.shtatic}))))

