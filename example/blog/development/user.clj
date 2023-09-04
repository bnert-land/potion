(ns user)

(require '[land.bnert.shtatic :as shtatic] :reload)

(comment

  (shtatic/pattern->prelude "^path/hello/index.md")

  (def site (shtatic/site!))

  (shtatic/start site)
  (shtatic/refresh site)
  (shtatic/stop site)

  (shtatic/paths site)
)
