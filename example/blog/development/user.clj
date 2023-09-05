(ns user)

(require '[land.bnert.shtatic.core :as shtatic] :reload)
(require '[land.bnert.shtatic.build :as build] :reload)

(comment

  (def site (shtatic/site!))

  (shtatic/start site)
  (shtatic/refresh site)
  (shtatic/stop site)

  (build/clean)
  (build/uber!)

  (shtatic/paths site)
)
