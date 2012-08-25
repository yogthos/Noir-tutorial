(ns my-website.views.files
  (:use hiccup.util
        noir.core
        hiccup.core
        hiccup.page
        hiccup.form
        hiccup.element)
  (:require [my-website.views.common :as common]
            [my-website.models.db :as db]
            [noir.response :as resp]))

(defn list-files [& [types]]  
  (into [:ul]
        (for [name (db/list-files types)]             
          [:li.file-link (link-to (str "/files/" name) name) 
           [:span "  "] 
           [:div.file]])))

(defn select-files-by-type []  
  (let [file-types (db/file-types)] 
    (form-to [:post "/show-files"]
             "select file types to show"
             (into 
               (with-group "file-types")
               (for [type file-types]
                 [:div 
                  type
                 (check-box type)]))
             (submit-button "show files"))))

(common/private-page [:post "/show-files"] params                     
  (let [file-types (keys params)] 
    (common/layout 
      [:h2 "showing files types " 
       (apply str (interpose ", " file-types))]
      (list-files file-types)
      (link-to "/upload" "back"))))

(common/private-page "/upload" {:keys [info]}
  (common/layout       
    [:h2.info info]
    (select-files-by-type)
    (list-files)
    (form-to {:enctype "multipart/form-data"}
             [:post "/upload"]
             (label :file "File to upload")
             (file-upload :file)
             [:br]
             (submit-button "upload"))))

(common/private-page [:post "/upload"] {:keys [file]}
  (render "/upload"
          {:info 
           (try
             (db/store-file file) 
             "file uploaded successfully"
             (catch Exception ex
               (do
                 (.printStackTrace ex)
                 (str "An error has occured while uploading the file: "
                      (.getMessage ex)))))}))

(common/private-page "/files/:name" {:keys [name]}
  (let [{:keys [name type data]} (db/get-file name)]
    (resp/content-type type (new java.io.ByteArrayInputStream data))))
