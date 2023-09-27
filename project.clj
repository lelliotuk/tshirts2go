(defproject ttg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.xerial/sqlite-jdbc "3.23.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/math.combinatorics "0.1.6"]
                 [ring/ring-jetty-adapter "1.7.1"]]
                 
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler ttg.handler/app}
  :main ttg.handler
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
