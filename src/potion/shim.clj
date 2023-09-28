(ns potion.shim
  (:gen-class)
  (:require
    [clojure.java.io :as io]
    [potion.core :as core]))

(defn -main [& _args]
  (core/start (core/site! {:fn/reader io/resource})))

