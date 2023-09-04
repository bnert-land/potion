# shtatic

Yet another static site generator.

## Why?

I wanted to make one. The exercise, as well as the option to explore what
may work in this space from a configurability and compilation standpoint
as well a what it may look like to have a repl driven static site generation/
creation tool.

## Quick Start
### Install
via `deps.edn`:
```
{:deps {bnert.land/shtatic {:git/sha "..."}}}
```

### Add Site Metadata
```shell
$ touch shtatic.edn
```

In `shtatic.edn` (showing defaults):
```
{:css  [["https://cdn.jsdelivr.net/npm/sakura.css/css/sakura.css"]]
 :site {""         "pages/index.md"
        ; If a key ends in ".md", it won't be expanded or put into a path
        "/about.md" "pages/about.md"
        ; If a key doesn't end in md, the path will be prepended as a
        ; prefix to the file. The value is a regex matcher.
        ;
        ; I.e. pages/posts/thing.md => https://some.url/posts/thing.md
        "/posts"    "pages/posts/[0-9A-Za-z]+.md"}}
```

### Create a pages
```shell
$ echo '# Welcome to shtatic 👋' > pages/index.md
$ echo '# About' > pages/about.md
$ echo '# Posts' > pages/index.md
$ echo '- [Hello World](pages/001-hello-world.md)' >> pages/index.md
$ echo '# Post' > pages/posts/001-hello-world.md
```


### Preview/Write
Start a REPL:
```shell
$ clj
Clojure 1.11.1
user=> (require '[land.bnert.shtatic :as shtatic])
user=> (def site (shtatic/site!))
user=> (shtatic/start site)   ; preview the site
user=> (shtatic/refresh site) ; refreshes content
user=> (shtatic/stop site)    ; when ready to stop site
user=> (shtatic/paths site)   ; 
```

### Package (not implemented)
```shell
$ clj
Clojure 1.11.1
user=> (require '[land.bnert.shtatic :as shtatic])
user=> (shtatic/package!) ; => out: target/shtatic.jar
```

### Deploy
Pick your poison on how to deploy your jar file.


## Couple Of Quips
1. In order to keep things simple, use [classless css](https://github.com/dbohdan/classless-css)
files.

## Roadmap
- [x] Config file format
- [x] REPL functions for content
- [ ] REPL functions for building jar's

