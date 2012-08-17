(defproject my-website "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir "1.3.0-beta3"]]
            :aot [my-website.server]
            :main my-website.server)

