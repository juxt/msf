(ns msf.api
  (:require
   [clojure.pprint :refer (pprint)]
   [liberator.representation :refer (ring-response)]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [msf.util :refer (field-hash)]
   [clojure.walk :refer (postwalk)]
   [ring.util.codec :as codec]
   [liberator.core :refer (defresource resource)]
   [hiccup.core :refer (html)]
   [msf.questions :refer (read-questions render-module)]
   [msf.view-util :refer (to-table explicit-column-order)]))

(def questionnaire-file "resources/questionnaire.edn")

(defn form-body []
  (html
   [:html
    [:body

     [:form {:method :post}
      (concat
       (->> questionnaire-file
            read-questions
            :modules
            (map render-module))
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

(defn questionnaire [dir]
  (fn [cw]
    (resource
     :available-media-types #{"text/html" "application/edn"}
     :allowed-methods #{:get :post}
     :handle-ok (fn [{rep :representation request :request}]
                  (case (:media-type rep)
                    "application/edn" (slurp questionnaire-file)
                    "text/html"
                    (cw request (form-body))))
     :post! (fn [{{body :body} :request :as req}]
              (let [form (codec/form-decode (slurp body :encoding (:character-encoding req)))]
                (spit (io/file dir (str (.getTime (java.util.Date.)) ".edn"))
                      (with-out-str (pprint (walker (edn/read-string (slurp (io/resource "questionnaire.edn"))) form))))
                ))
     :post-redirect? true
     :handle-see-other (ring-response {:headers {"Location" "/"}}))))

(defn submissions [dir]
  (fn [cw]
    (resource
     :available-media-types #{"text/html" "application/edn"}
     :allowed-methods #{:get}
     :handle-ok (fn [{rep :representation request :request}]
                  (case (:media-type rep)
                    "application/edn" (.list dir)
                    "text/html" (cw request
                                    (html
                                     [:body
                                      (to-table {:column-order (explicit-column-order :file :responder :location)
                                                 :formatters {:file (fn [row] [:a {:href (str "/submissions/" (.getName (:file row)))} (.getName (:file row))])
                                                              :responder (fn [row] (str (->> (read-string (slurp (:file row)))
                                                                                             :modules
                                                                                             (filter #(= :personal (:id %)))
                                                                                             first
                                                                                             :questions
                                                                                             (filter #(= (:question %) "Your name"))
                                                                                             first
                                                                                             :answer)))
                                                              :location (fn [row] (str (->> (read-string (slurp (:file row)))
                                                                                             :modules
                                                                                             (filter #(= :personal (:id %)))
                                                                                             first
                                                                                             :questions
                                                                                             (filter #(= (:question %) "Your location"))
                                                                                             first
                                                                                             :answer)))}}
                                                (for [i (.listFiles dir)] {:file i}))]))))
     :post! (fn [{{body :body} :request :as req}]
              (let [form (codec/form-decode (slurp body :encoding (:character-encoding req)))]
                (spit (io/file dir (str (.getTime (java.util.Date.)) ".edn"))
                      (with-out-str (pprint (walker (edn/read-string (slurp (io/resource "questionnaire.edn"))) form))))
                ))
     :post-redirect? true
     :handle-see-other (ring-response {:headers {"Location" "/"}}))))
