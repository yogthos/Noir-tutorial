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

(defn list-files []
  (into [:ul]
        (for [name (db/list-files)]             
          [:li.file-link (link-to (str "/files/" name) name) 
           [:span "  "] 
           [:div.file]])))

(defpage "/upload" {:keys [info]}
  (common/layout    
    [:h2.info info]
    (list-files)
    (form-to {:enctype "multipart/form-data"}
             [:post "/upload"]
             (label :file "File to upload")
             (file-upload :file)
             [:br]
             (submit-button "upload"))))

(defpage [:post "/upload"] {:keys [file]}
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

(defpage "/files/:name" {:keys [name]}
  (let [{:keys [name type data]} (db/get-file name)]
    (resp/content-type type (new java.io.ByteArrayInputStream data))))
