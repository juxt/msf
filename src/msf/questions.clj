(ns msf.questions
  (require [clojure.edn :as edn]
           [hiccup.core :as hic]))

(defn read-questions
  "Temp function for testing"
  [filename]
  (-> filename slurp edn/read-string))

(defn render-question-label [q]
  [:span {:class "question-label"} (:question q)])

;;(render-question) has no opinion about layout, it just returns 2
;;hiccup fragments, one for the label and one for the input field.

(defmulti render-question :answer-type)

(defmethod render-question :text [q]
  {:label (render-question-label q)
   :field [:input {:class "question-text-field"}]})

;;TODO change this to include integer validation 
(defmethod render-question :integer [q]
  {:label (render-question-label q)
   :field [:input {:class "question-integer-field"}]})

(defmethod render-question :choice [{:keys [choices] :as q}]
  {:label (render-question-label q)
   :field [:select
           (map (fn [c] [:option (:choice c)]) choices)]})

(defn render-question-row [question]
  (let [{:keys [label field]} (render-question question)]
    [:tr
     [:td label]
     [:td field]]))

(defn render-questions-table [questions]
  [:table
   [:tbody
    (map render-question-row questions)]])

(defn render-module [{:keys [title questions]}]
  [:html
   [:body
    [:h1 title]
    (render-questions-table questions)]])

(comment
  (->> "resources/questionnaire.edn"
       read-questions
       :modules
       first
       render-module
       hic/html
       (spit "resources/q.html")))
