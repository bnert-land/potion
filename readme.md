# potion

Yet another static site thingy. Right now, markdown files are put onto
the class path and rendered ad hoc.

While slower, this has allowed for a pretty quick development time, and
has kept development/repl and prod homogenous. It is on the roadmap to
perform true static site generation, but for now, not a priority.

## Why?

I wanted to make one. The exercise, as well as the option to explore what
may work in this space from a configurability and compilation standpoint
as well a what it may look like to have a repl driven static site generation/
creation tool.

## Quick Start
### Install
via `deps.edn`
```
{:deps {bnert.land/potion {:git/sha "0739ed49c05c740a1385bbc4768a68161b8583a8"
                           :git/url "https://github.com/bnert-land/potion.git"}}}
```

### Add Site Metadata
```shell
$ touch potion.edn
```

In `potion.edn` (showing defaults)
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
$ echo '# Welcome to potion 👋' > pages/index.md
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
user=> (require '[potion.core :as potion])
user=> (def site (potion/site!))
user=> (potion/start site)   ; preview the site
user=> (potion/refresh site) ; refreshes content
user=> (potion/stop site)    ; when ready to stop site
```

### Package (not implemented)
```shell
$ clj
Clojure 1.11.1
user=> (require '[potion.build :as build])
user=> (build/uber!) ; => out: target/potion.jar
```

### Deploy
Pick your poison on how to deploy your jar file.

## Markdown

Uses [MultiMarkdown](https://github.com/fletcher/MultiMarkdown/wiki/MultiMarkdown-Syntax-Guide) variant,
because I didn't want to write a markdown interpreter.

## Couple Of Quips
1. In order to keep things simple, use [classless css](https://github.com/dbohdan/classless-css)
files.

## Roadmap (In No Particular Order)

### Seems important and useful
- [x] Config file format
- [x] REPL functions for content
- [x] REPL functions for building jar's
- [ ] "Zero config" tree walking for sourcing files (i.e. directory based routing)

### Not as important but seem useful

- [ ] Custom html/css "components" (injected via parse/read time)
- [ ] File caching
- [ ] Remote server provision via REPL
- [ ] Remote server deploy via REPL/git

