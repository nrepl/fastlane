(ns fastlane.core-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [fastlane.core :as fastlane]
            [nrepl.core :as nrepl]
            [nrepl.server :as server]))

(def transport-fn->protocol
  "Add your transport-fn var here so it can be tested"
  {#'fastlane/transit+msgpack "transit+msgpack"
   #'fastlane/transit+json "transit+json"
   #'fastlane/transit+json-verbose "transit+json-verbose"})

(def transport-fns
  (keys transport-fn->protocol))

(def ^{:dynamic true} *server* nil)
(def ^{:dynamic true} *transport-fn* nil)

(defn repl-server-fixture
  [f]
  (doseq [transport-fn transport-fns]
    (binding [*transport-fn* transport-fn]
      (testing (str (-> transport-fn meta :name) " transport\n")
        (f)))))

(use-fixtures :each repl-server-fixture)

(deftest transit-transport-communication
  (is (= (with-open [server (server/start-server :transport-fn *transport-fn* :port 7889)]
           (with-open [conn (nrepl/url-connect (str (transport-fn->protocol *transport-fn*)
                                                    "://127.0.0.1:"
                                                    (:port server)))]
             (-> (nrepl/client conn 1000)
                 (nrepl/message {:op "eval" :code "(+ 2 3)"})
                 nrepl/response-values)))
         [5])))
