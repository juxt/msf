(ns msf.system
  (:require
   modular.core
   [msf.questions :refer (read-questions render-module)]
   [com.stuartsierra.component :as component]
   [modular.http-kit :refer (new-webserver)]
   [hiccup.core :refer (html)]))

(defn handler [req]
  {:status 200
   :body (->> "resources/questionnaire.edn"
              read-questions
              :modules
              first
              render-module
              html
              ;;(spit "resources/q.html")
              )})

(defrecord HandlerProvider []
  component/Lifecycle
  (start [this] this)
  (stop [this] this)
  modular.ring/RingHandlerProvider
  (handler [this] handler))

(defn new-system []
  (-> (component/system-map
       :web-server (new-webserver {:port 8000})
       :handler (->HandlerProvider))

      (modular.core/system-using {})))
