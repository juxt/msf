(ns msf.api
  (:require
   [clojure.pprint :refer (pprint)]
   [ring.util.codec :as codec]
   [liberator.core :refer (defresource)]
   [hiccup.core :refer (html)]
   [msf.questions :refer (read-questions render-module)]))

(defn form-body []
  (html
   [:html
    [:body

     [:form {:method :post}
      (concat
       (->> "resources/questionnaire.edn"
            read-questions
            :modules
            first
            render-module)
       [[:input {:name "submit" :type "submit" :value "Submit"}]])]]]))

(defresource questionnaire
  :available-media-types #{"text/html"}
  :allowed-methods #{:get :post}
  :handle-ok (form-body)
  :post! (fn [{{body :body} :request :as req}]
           (let [form (codec/form-decode (slurp body :encoding (:character-encoding req)))]
             (prn form)))

  :handle-created "Thanks!")
