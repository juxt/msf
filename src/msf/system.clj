(ns msf.system
  (:require
   [msf.api :refer (questionnaire)]
   modular.core
   [com.stuartsierra.component :as component]
   [modular.http-kit :refer (new-webserver)]
   [hiccup.core :refer (html)]))

(defrecord HandlerProvider []
  component/Lifecycle
  (start [this] this)
  (stop [this] this)
  modular.ring/RingHandlerProvider
  (handler [this] #'questionnaire))

(defn new-system []
  (-> (component/system-map
       :web-server (new-webserver {:port 8000})
       :handler (->HandlerProvider))

      (modular.core/system-using {})))
