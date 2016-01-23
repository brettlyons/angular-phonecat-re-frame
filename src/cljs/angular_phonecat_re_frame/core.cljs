(ns angular-phonecat-re-frame.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views

(re-frame/register-handler
  :initialise-db
  (fn
    [db v]
    {:phones [{:name "Nexus S" :snippet "You'll go faster with a Nexus S"}
              {:name "Motorola Xoom with WIFI" :snippet "The Previous Generatoin of Tablet"}
              {:name "Motorola Xoom 2" :snippet "The Next Prevoius Generation"}]}))

(re-frame/dispatch  [:initialise-db])

(re-frame/register-sub
  :phones
  (fn [db]
    (reaction (:phones @db))))

(defn phone-component
  [phone]
  [:li
   [:span (:name @phone)]
   [:p (:snippet @phone)]])

(defn phones-component
  []
  (let [phones (re-frame/subscribe [:phones])]
    (fn []
      [:ul (for [phone in @phones]
             ^{:key (:name phone)} [phone-component phone])])))

(defn home-page []
  [:div [:h2 "Welcome to angular-phonecat-re-frame"]
   [phones-component]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About angular-phonecat-re-frame"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
