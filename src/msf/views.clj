(ns msf.views
  (:require
   [com.stuartsierra.component :as component]
   [modular.bidi :refer (BidiRoutesContributor new-bidi-routes)]
   [bidi.bidi :refer (->WrapMiddleware path-for ->Redirect Matched resolve-handler unresolve-handler ->Files)]
   [cylon.core :refer (new-optionally-protected-bidi-routes)]
   [msf.css :refer (css-page)]
   [msf.api :as api]
   [datomic.api :as d]
   [hiccup.core :refer (html)]
   [clostache.parser :as parser]
   [modular.protocols :refer (Index)]))

(defn make-content-wrapper [menu]
  (fn [{routes :modular.bidi/routes :as req} content]
    (parser/render-resource
     "templates/page.html.mustache"
     {:title "MSF"
      :content content
      :app-name "MSF"
      :title-link "/"
      :menu (for [{:keys [label path]} menu]
              {:listitem (html [:li [:a {:href path} label]])})})))

(defn make-resource-handlers []
  (let [p (promise)]
    @(deliver p {:css css-page})))

(defn make-resource-routes [handlers]
  ["/"
   [["style.css" (:css handlers)]]])

(defn new-resource-routes []
  (-> (make-resource-handlers)
      make-resource-routes
      new-bidi-routes))

(defprotocol MenuItems
  (menu-items [_]))

(defrecord MenuIndex []
  component/Lifecycle
  (start [this]
    (assoc this
      :menuitems (->> this vals (filter (partial satisfies? MenuItems)) (mapcat menu-items))))
  (stop [this] this)

  Index
  (types [this] #{MenuItems})

  BidiRoutesContributor
  (routes [this]
    (let [cw (make-content-wrapper (:menuitems this))]
      [""
       (vec (for [{:keys [path resource-factory]} (:menuitems this)]
              [path (resource-factory cw)]))]))
  (context [_] ""))

(defn new-menu-index []
  (->MenuIndex))

(defrecord HtmlResource [label path resource-factory]
  component/Lifecycle
  (start [this]
    (assoc this
      :menuitems [{:label label
                   :path path
                   :resource-factory resource-factory
                   }]))
  (stop [this] this)
  MenuItems
  (menu-items [this] (:menuitems this)))

(defn new-html-resource [label path resource-factory]
  (->HtmlResource label path resource-factory))

(defn make-resource-handlers []
  (let [p (promise)]
    @(deliver p {:css css-page})))

(defn make-resource-routes [handlers cfg]
  (println cfg)
  ["/"
   [["style.css" (:css handlers)]
    ["bootstrap" (->Files {:dir (:bootstrap-dist cfg)})]
    ["jquery" (->Files {:dir (:jquery-dist cfg)})]]])

(defn new-resource-routes [cfg]
  (-> (make-resource-handlers)
      (make-resource-routes cfg)
      new-bidi-routes))

(defn make-main-handlers [menuitems]
  (assert menuitems "No menu items!")
  (let [cw (make-content-wrapper menuitems)
        p (promise)]
    @(deliver p
              {:index (fn [req] {:status 200 :body (cw req "Welcome")})})))

(defn make-main-routes [handlers]
  ["/"
   [["" (->Redirect 307 (:index handlers))]
    ["index.html" (:index handlers)]]])

(defn new-main-routes []
  (component/using
   (new-optionally-protected-bidi-routes
    (fn [c] (->> (get-in c [:menu :menuitems])
                 make-main-handlers make-main-routes)))
   [:menu]))
