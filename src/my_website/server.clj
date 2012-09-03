(ns my-website.server
  (:use my-website.config)
  (:require [noir.server :as server]
            [my-website.views 
             common
             files
             log-stats
             users
             welcome])     
  (:gen-class))

(server/load-views-ns 'my-website.views)

(def handler 
  (server/gen-handler 
    {:mode :prod, 
     :ns 'my-website 
     :session-cookie-attrs {:max-age 1800000}}))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (init-config)
    (server/start port {:mode mode
                        :ns 'my-website})))

