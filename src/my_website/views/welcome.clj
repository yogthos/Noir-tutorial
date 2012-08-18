(ns my-website.views.welcome
  (:require [my-website.views.common :as common])
  (:use [noir.core :only [defpage]]
        hiccup.core hiccup.form))

(defpage "/" []
  (common/layout 
    ))

(defpage "/welcome" {:keys [greeting]}
  (common/layout
    (if greeting [:h2 greeting]) 
    (form-to [:post "/welcome"]
      (label "name" "name")
      (text-field "name")
      (submit-button "submit"))))

(defpage [:post "/welcome"] {:keys [name]}
  (noir.core/render "/welcome" {:greeting (str "Welcome " name)}))

