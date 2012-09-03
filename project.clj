(defproject my-website "0.1.0-SNAPSHOT"
            :description "my Noir website"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.3.0-beta3"]
                           [org.clojure/java.jdbc "0.2.3"]
                           [postgresql/postgresql "9.1-901.jdbc4"]
                           [joda-time "2.0"]]
            :dev-dependencies [[lein-ring "0.7.3"]]
            :ring {:handler my-website.server/handler
                   :init my-website.config/init-config}
            :main my-website.server)

