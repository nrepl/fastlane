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

You can also start it via the built-in nREPL CLI:

``` shell
# Change the nREPL and fastlane versions to your preferences
$ clj -Sdeps '{:deps {nrepl {:mvn/version "0.6.0-SNAPSHOT"} nrepl/fastlane {:mvn/version "0.2.0-SNAPSHOT"}}}' -m nrepl.cmdline --transport fastlane.core/transit+json
```

## License

Copyright © 2018 Bozhidar Batsov and nREPL contributors

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
