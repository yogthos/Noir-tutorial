(ns my-website.models.db
  (:require [clojure.java.jdbc :as sql]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(def db 
  {:subprotocol "postgresql"
   :subname "//localhost/my_website"
   :user "admin"
   :password "admin"})

(defn init-db []
  (try
  (sql/with-connection
    db
    (sql/create-table
      :users
      [:id "SERIAL"]
      [:handle "varchar(100)"]
      [:pass   "varchar(100)"]))
  (catch Exception ex
    (.getMessage (.getNextException ex)))))

(defn create-file-table []
  (sql/with-connection 
    db
    (sql/create-table
      :file
      [:type "varchar(50)"]
      [:name "varchar(50)"]
      [:data "bytea"])))

(defn db-read
  "returns the result of running the supplied SQL query"
  [query & args]
  (sql/with-connection 
    db
    (sql/with-query-results res (vec (cons query args)) (doall res))))

(defn add-user [user]
  (sql/with-connection 
    db
    (sql/insert-record :users user)))

(defn get-user [handle]
  (first (db-read "select * from users where handle=?" handle)))

(defn to-byte-array [f]  
  (with-open [input  (new java.io.FileInputStream f) 
              buffer (new java.io.ByteArrayOutputStream)]
    (clojure.java.io/copy input buffer)
    (.toByteArray buffer)))

(defn store-file [{:keys [tempfile filename content-type]}]
  (sql/with-connection 
    db
    (sql/update-or-insert-values
      :file
      ["name=?" filename]
      {:type content-type 
       :name filename ;;needs to be cleaned up when served as url 
       :data (to-byte-array tempfile)})))

(defn list-files []
  (map :name (db-read "select name from file")))

(defn get-file [name]
  (first (db-read "select * from file where name=?" name)))
