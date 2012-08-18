(ns my-website.views.common
  (:use [noir.core :only [defpartial]]
        hiccup.element 
        hiccup.form
        [hiccup.page :only [include-css html5]])
  (:require [noir.session :as session]))

(defn login-form []
  (form-to [:post "/login"]           
           (text-field {:placeholder "user id"} "handle")                        
           (password-field {:placeholder "password"} "pass")                        
           (submit-button "login")))

(defpartial layout [& content]
            (html5
              [:head
               [:title "my-website"]
               (include-css "/css/reset.css")]
              [:body               
               (if-let [user (session/get :user)]
                  [:h2 "welcome " user 
                    (form-to [:post "/logout"] (submit-button "logout"))]
                  [:div.login
                   (login-form) [:p "or"] (link-to "/signup" "sign up")])
               
               content]))
