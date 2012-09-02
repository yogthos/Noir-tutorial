(ns my-website.server
  (:require [noir.server :as server]
            [my-website.views 
             common
             files
             log-stats
             users
             welcome])     
  (:gen-class))

(server/load-views-ns 'my-website.views)

(defn fix-base-url [handler]
  (fn [request]    
    (with-redefs [noir.options/resolve-url 
                  (fn [url]                    
                    ;prepend context to the relative URLs
                    (if (.contains url "://") 
                      url (str (:context request) url)))]
      (handler request))))

(def base-handler 
  (server/gen-handler 
    {:mode :prod, 
     :ns 'my-website 
     :session-cookie-attrs {:max-age 1800000}}))

(def handler (-> base-handler fix-base-url))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'my-website})))

