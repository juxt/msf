(ns msf.system
  (:require
   [msf.api :refer (questionnaire)]
   [modular.core :refer (add-index-dependencies)]
   [clojure.java.io :as io]
   [com.stuartsierra.component :as component]
   [modular.http-kit :refer (new-webserver)]
   [modular.bidi :refer (new-bidi-ring-handler-provider new-bidi-routes)]
   [cylon.core :refer (new-default-protection-system new-protected-bidi-routes)]
   [hiccup.core :refer (html)]))

(defn new-main-routes []
  (new-protected-bidi-routes
   ["/index.html" questionnaire]))

(defn new-system []
  (let [system-map (component/system-map
       :web-server (new-webserver {:port 8000})
       :bidi-ring-handler (new-bidi-ring-handler-provider)
       :new-main-routes (new-main-routes)

       :protection-system
       (new-default-protection-system
        :password-file (io/file (System/getProperty "user.home") ".msf-passwords.edn")))]
    (component/system-using system-map (-> {} (add-index-dependencies system-map)))))
