# fastlane

Transit transports for nREPL.

## Motivation

A Transit transport simplifies the lives of people using
Clojure/ClojureScript/Java clients for nREPL. We use
[transit-clj][tclj] to implement transports to be used with nREPL, it
supports transit with message pack, json and json verbose.

[tclj]: https://github.com/cognitect/transit-clj/blob/master/src/cognitect/transit.clj

## Usage

Just add fastlane as a dependency to your project and you can use it as a transport for nREPL.

```clojure
[nrepl/fastlane "0.1.0"]
```

The transports are at fastlane.core:

* `transit+msgpack`
* `transit+json`
* `transit+json-verbose`

Afterwards you can start the server like this:

``` clojure
(require
 '[nrepl.server :as server]
 '[fastlane.core :as fastlane])

(server/start-server :port 12345 :transport-fn fastlane/transit+json)
```

You can also start it via the built-in nREPL CLI with the following options:

### Using deps inline with `clj`

``` shell
$ clj -Sdeps '{:deps {nrepl {:mvn/version "0.5.3"} nrepl/fastlane {:mvn/version "0.2.0-SNAPSHOT"}}}' -m nrepl.cmdline --transport fastlane.core/transit+json
```

### Creating a `nrepl` profile at `deps.edn` with `clj`

``` shell
$ cat deps.edn
{:aliases
 {:nrepl {:extra-deps {nrepl {:mvn/version "0.5.3"}
                       nrepl/fastlane {:mvn/version "0.2.0-SNAPSHOT"}}}}}
$ clj -R:nrepl -m nrepl.cmdline --transport fastlane.core/transit+json
```

### Creating a `nrepl` profile at `deps.edn` with `clj` using `nrepl` configuration file (`.nrepl.edn`)

``` shell
$ cat deps.edn
{:aliases
 {:nrepl {:extra-deps {nrepl {:mvn/version "0.5.3"}
                       nrepl/fastlane {:mvn/version "0.2.0-SNAPSHOT"}}}}}
$ cat .nrepl.edn
{:transport fastlane.core/transit+json}
$ clj -R:nrepl -m nrepl.cmdline
```

### Using `lein` for headless REPL with a `nrepl` configuration file

``` shell
# With `[nrepl/fastlane "0.2.0-SNAPSHOT"]` at your `project.clj`
# and configuration at .nrepl.edn
$ cat .nrepl.edn
{:transport fastlane.core/transit+json}
$ lein repl :headless
# It should start with transit+json transport
```

### Using `lein` for interactive REPL (in construction...)

``` shell
# TODO
```


## License

Copyright © 2018 Bozhidar Batsov and nREPL contributors

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
