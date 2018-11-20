(defproject nrepl/fastlane "0.1.0-SNAPSHOT"
  :description "Transit transports for nREPL."
  :url "https://github.com/nrepl/fastlane"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git" :url "https://github.com/nrepl/fastlane"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [nrepl "0.4.5"]]

  :aliases {"test-all" ["with-profile" "+1.8:+1.9" "test"]}

  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases false}]]

  :profiles {:1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}

             :cljfmt {:plugins [[lein-cljfmt "0.6.1"]]}})
