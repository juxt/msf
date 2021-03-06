(ns user
  (:require
   [com.stuartsierra.component :as component]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [msf.system :refer (new-system)]
   [clojure.pprint :refer (pprint)]
   [clojure.reflect :refer (reflect)]
   [clojure.repl :refer (apropos dir doc find-doc pst source)]
   [modular :refer (system)]
   [cylon.core :as cylon]))

(defn init
  "Constructs the current development system."
  []
  (alter-var-root
   #'system
   (constantly (new-system))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system component/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))

(defn add-user! [uid pw]
  (cylon/add-user!
   (-> system :protection-system :user-password-authorizer)
   uid pw))
