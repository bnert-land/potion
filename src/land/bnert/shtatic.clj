(ns land.bnert.shtatic
  (:gen-class)
  (:require
    [clojure.java.io :as io]
    [land.bnert.shtatic.core :as core]))

(defn -main [& _args]
  (core/start (core/site! {:fn/reader io/resource})))

