(ns my-website.views.users
  (:use [noir.core]
        hiccup.core hiccup.form)
  (:require [my-website.views.common :as common]
            [my-website.models.db :as db]
            [noir.util.crypt :as crypt]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]))

(defpartial error-item [[first-error]]
  [:p.error first-error])

(defn valid? [{:keys [handle pass pass1]}]
  (vali/rule (vali/has-value? handle)
             [:handle "user ID is required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])  
  (vali/rule (= pass pass1)
             [:pass "entered passwords do not match"])
  (not (vali/errors? :handle :pass :pass1)))

(defpage "/signup" {:keys [handle error]}
  (common/basic-layout
    [:div.error error]
    (form-to [:post "/signup"]
             (vali/on-error :handle error-item)
             (label "user-id" "user id")
             (text-field "handle" handle)
             [:br]
             (vali/on-error :pass error-item)
             (label "pass" "password")
             (password-field "pass")             
             [:br]
             (vali/on-error :pass1 error-item)
             (label "pass1" "retype password")
             (password-field "pass1")             
             [:br]
             (submit-button "create account"))))

(defpage [:post "/signup"] user
  (if (valid? user)
    (try 
      (db/add-user (update-in (dissoc user :pass1) [:pass] crypt/encrypt))
      (common/local-redirect "/")
      (catch Exception ex
        (render "/signup" (assoc user :error (.getMessage ex)))))
    (render "/signup" user)))

(defpage [:post "/login"] {:keys [handle pass]}
  (let [user (db/get-user handle)] 
    (if (and user (crypt/compare pass (:pass user)))           
      (do (session/put! :user handle)
        (common/local-redirect "/"))
      (render "/" {:handle handle :error "login failed"}))))

(defpage [:post "/logout"] []
  (session/clear!)
  (common/local-redirect "/"))