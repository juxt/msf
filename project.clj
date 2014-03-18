(defproject msf "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]

                 [hiccup "1.0.5"]

                 [ch.qos.logback/logback-classic "1.0.7" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.2"]
                 [org.slf4j/jcl-over-slf4j "1.7.2"]
                 [org.slf4j/log4j-over-slf4j "1.7.2"]

                 ;; Modules
                 [com.stuartsierra/component "0.2.1"]
                 [juxt.modular/http-kit "0.1.0-SNAPSHOT"]

                 [hiccup "1.0.5"]
                 [liberator "0.11.0"]
                 [ring/ring-codec "1.0.0"]

                 [juxt/datomic-extras "1.0.3"
                  :exclusions [org.slf4j/slf4j-nop
                               org.slf4j/jul-to-slf4j
                               org.slf4j/jcl-over-slf4j
                               org.slf4j/log4j-over-slf4j]]]

  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]]
                   :source-paths ["dev"]}})
