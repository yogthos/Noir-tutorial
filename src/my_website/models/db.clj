(ns my-website.models.db
  (:require [clojure.java.jdbc :as sql]))

(def db 
  {:subprotocol "postgresql"
   :subname "//localhost/my_website"
   :user "admin"
   :password "admin"})

#_ (try
  (sql/with-connection
    db
    (sql/create-table
      :users
      [:id "SERIAL"]
      [:handle "varchar(100)"]
      [:pass   "varchar(100)"]))
  (catch Exception ex
    (.getMessage (.getNextException ex))))

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


