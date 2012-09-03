(ns my-website.views.common
  (:use [noir.core :only [defpartial]]
        hiccup.element 
        hiccup.form
        [hiccup.page :only [include-css include-js html5]])
  (:require [noir.session :as session]))

(defmacro local-redirect [url]
  `(noir.response/redirect 
     (if-let [context# (:context (noir.request/ring-request))]
       (str context# ~url) ~url)))

(defmacro private-page [path params & content]
  `(noir.core/defpage 
     ~path 
     ~params 
     (if (session/get :user) (do ~@content) (local-redirect "/"))))

(defn login-form []
  (form-to [:post "/login"]           
           (text-field {:placeholder "user id"} "handle")                        
           (password-field {:placeholder "password"} "pass")                        
           (submit-button "login")))

(defn menu []
  [:div.menu
   [:ul
    [:li (form-to [:post "/logout"] (submit-button "logout"))]    
    [:li (link-to "/upload" "my files")]
    [:li (link-to "/" "home")]]])

(defpartial basic-layout [& content]
  (html5
    [:head
     [:title "my-website"]
     (include-css "/css/reset.css")
     (include-js "http://code.jquery.com/jquery-1.7.2.min.js"
                 "/js/jquery.flot.min.js")]
    [:body content]))

(defpartial layout [& content]  
  (basic-layout 
    [:div
     (if-let [user (session/get :user)]      
       [:div
        (menu)
        [:h2 "welcome " user]]
       [:div.login
        (login-form) [:p "or"] (link-to "/signup" "sign up")])
     content]))
