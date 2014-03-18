(ns msf.api
  (:require
   [liberator.core :refer (defresource)]
   [hiccup.core :refer (html)]
   [msf.questions :refer (read-questions render-module)]))

(defn form-body []
  (html
   [:html
    [:body
     (->> "resources/questionnaire.edn"
          read-questions
          :modules
          first
          render-module

          )
     [:form {:method :post} [:input {:type "submit" :value "Submit"}]]]]))

(defresource questionnaire
  :available-media-types #{"text/html"}
  :allowed-methods #{:get :post}
  :handle-ok (form-body)
  :handle-created "Thanks!")
