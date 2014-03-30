(ns msf.system
  (:require
   [clojure.java.io :as io]
   [clojure.tools.reader.reader-types :refer (indexing-push-back-reader)]
   clojure.tools.reader
   [com.stuartsierra.component :as component]
   [cylon.core :refer (new-default-protection-system new-optionally-protected-bidi-routes)]
   [hiccup.core :refer (html)]
   [modular.bidi :refer (new-bidi-ring-handler-provider new-bidi-routes)]
   [modular.core :refer (add-index-dependencies)]
   [modular.http-kit :refer (new-webserver)]
   [msf.api :refer (questionnaire)]
   [msf.views :refer (new-main-routes new-menu-index new-resource-routes new-html-resource)]))

(defn config []
  (let [f (io/file (System/getProperty "user.home") ".msf.edn")]
    (when (.exists f)
      (clojure.tools.reader/read
       (indexing-push-back-reader
        (java.io.PushbackReader. (io/reader f)))))))

(defn new-system []
  (let [cfg (config)
        system-map
        (component/system-map
         :web-server (new-webserver {:port 8000})
         :bidi-ring-handler (new-bidi-ring-handler-provider)

         :menu (new-menu-index)
         :resource-routes (new-resource-routes cfg)
         :new-main-routes (new-main-routes)
         :api-routes (new-html-resource questionnaire)

         :protection-system
         (new-default-protection-system
          :password-file (io/file (System/getProperty "user.home") ".msf-passwords.edn")))]

    (component/system-using system-map (-> {:new-main-routes [:protection-system]} (add-index-dependencies system-map)))))
