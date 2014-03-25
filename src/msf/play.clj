(ns msf.play
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [msf.util :refer (field-hash)]
   [clojure.walk :refer (postwalk)])

)

(def answers {"submit" "Submit", "1272541496" "2012", "-1684303240" "London", "-811683248" "142198544", "-1737995881" "Title"})

(postwalk
 (fn [x] (if-let [q (:question x)]
           (assoc x :answer (answers (str (field-hash q))))
           x))


 {:modules [{:id :influences, :title "Influence on Policy, Practice, Patients & the Public", :questions [{:question "Provide a short title or name for this influence on policy or practice", :answer-type :text} {:question "Please select an influence", :answer-type :choice, :choices [{:choice "Influenced training of practitioners or researchers"} {:choice "Citation in clinical guidelines"} {:choice "Citation in clinical reviews"} {:choice "Citation in other policy documents"} {:choice "Citation in systematic reviews"}]} {:question "Which location", :answer-type :text} {:question "Select the year in which the influence was first realised.", :answer-type :integer}]}]})

(edn/read-string (slurp (io/resource "questionnaire.edn")))
