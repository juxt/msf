(ns msf.api
  (:require
   [clojure.pprint :refer (pprint)]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [msf.util :refer (field-hash)]
   [clojure.walk :refer (postwalk)]
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

(defn walker
  ([tree answers]
     (walker tree answers ""))
  ([tree answers hash-prefix]
     (postwalk
      (fn [x]
        (if-let [title (:title x)]
          (assoc x :questions (walker (:questions x) answers title))
          (if-let [q (:question x)]
            (assoc x :answer (answers (field-hash (str hash-prefix ":" q))))
            x)))
      tree
      )))

(defresource questionnaire
  :available-media-types #{"text/html"}
  :allowed-methods #{:get :post}
  :handle-ok (form-body)
  :post! (fn [{{body :body} :request :as req}]
           (let [form (codec/form-decode (slurp body :encoding (:character-encoding req)))]
             (prn (walker (edn/read-string (slurp (io/resource "questionnaire.edn"))) form))
             ))

  :handle-created "Thanks!")
