(ns user)

(require '[potion.core :as potion] :reload)
(require '[potion.build :as build] :reload)

(comment

  (def site (potion/site!))

  (potion/start site)
  (potion/refresh site)
  (potion/stop site)

  (build/clean)
  (build/uber nil)

  (potion/paths site)
)
