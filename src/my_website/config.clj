(ns my-website.config
  (:use clojure.java.io))

(def config-file "config")
(def app-config (atom nil))

(defn load-config-file []
  (let [url (.. 
              (Thread/currentThread) 
              getContextClassLoader 
              (findResource config-file))] 
    (if (or (nil? url) 
            (.. url 
              getPath 
              (endsWith (str "jar!/" config-file))))
      (doto (new java.io.File "config") 
        (.createNewFile))
      url)))

(defn init-config []
  (with-open
    [r (java.io.PushbackReader. (reader (load-config-file)))]
    (if-let [config (read r nil nil)]
      (reset! app-config config)))
  (println "servlet has been initialized"))