(ns my-website.views.log-stats
  (:require [my-website.views.common :as common]
            [noir.request :as request]
            [noir.response :as response])
  (:use clojure.java.io hiccup.page hiccup.form noir.core)
  (:import java.text.SimpleDateFormat java.io.File))


(defn round-ms-down-to-nearest-sec [date]
  (let [date (.parse 
               (new SimpleDateFormat 
                    "dd/MMM/yyyy:HH:mm:ss zzzzz") 
               date)] 
    ( * 1000 (quot (.getTime date) 1000))))

(defn parse-line [line]
  {:ip (re-find #"\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b" line) 
   :access-time (round-ms-down-to-nearest-sec 
                  (second (re-find #"\[(.*?)\]" line))) })

(defn read-logs [file] 
  (with-open [rdr (reader file)] 
    (doall (map parse-line (line-seq rdr)))))

(defn hits-per-second [logs]
  (->> logs 
    (group-by :ip)
    (mapcat second)    
    (group-by :access-time)    
    (map (fn [[t hits]] [t (count hits)]))
    (sort-by first)))

(defn last-log [path] 
  (->> path
    (new File)
    (.listFiles)    
    (filter #(.startsWith (.getName %) "localhost_access_log") )
    (sort-by (memfn lastModified))
    (map (memfn getName))
    (last)))

(defpage [:post "/get-logs"] params  
  (response/json (hits-per-second (read-logs (str "logs/" (last-log "logs/"))))))

(defpage "/access-chart" []
  (common/basic-layout
    (include-js "/js/site.js")
    (hidden-field "context" (:context (request/ring-request)))
    [:div#hits-by-time "loading..."]))


