(ns msf.questions
  (:require [clojure.edn :as edn]
           [msf.util :refer (field-hash)]
           [hiccup.core :as hic]))

(defn read-questions
  "Temp function for testing"
  [filename]
  (-> filename slurp edn/read-string))

(defn render-question-label [q]
  [:span {:class "question-label"} (:question q)])

;;(render-question) has no opinion about layout, it just returns 2
;;hiccup fragments, one for the label and one for the input field.

(defmulti render-question (fn [hash-prefix q] (:answer-type q)))

(defmethod render-question :text [hash-prefix q]
  {:label (render-question-label q)
   :field [:input {:name (field-hash (str hash-prefix ":" (:question q)))
                   :class "question-text-field"}]})

;;TODO change this to include integer validation
(defmethod render-question :integer [hash-prefix q]
  {:label (render-question-label q)
   :field [:input {:name (field-hash (str hash-prefix ":" (:question q)))
                   :class "question-integer-field"}]})

(defmethod render-question :choice [hash-prefix {:keys [choices] :as q}]
  {:label (render-question-label q)
   :field [:select
           {:name (field-hash (str hash-prefix ":" (:question q)))}
           (map (fn [{c :choice}]
                  [:option {:value (field-hash (str hash-prefix ":" c))} c]) choices)]})

(defn render-question-row [hash-prefix question]
  (let [{:keys [label field]} (render-question hash-prefix question)]
    [:tr
     [:td label]
     [:td field]]))

(defn render-questions-table [hash-prefix questions]
  [:table.questionnaire
   [:tbody
    (map render-question-row (repeat hash-prefix) questions)]])

(defn render-module [{:keys [title questions]}]
  (list
   [:h1 title]
   (render-questions-table title questions)))

(comment
  (->> "resources/questionnaire.edn"
       read-questions
       :modules
       (map render-module)
       hic/html
       (spit "resources/q.html")))
