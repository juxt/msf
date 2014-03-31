(ns msf.css
  (:require
   [ring.util.response :as ring-resp]
   [garden.core :refer (css)]
   [garden.units :refer (px pt em percent)]
   [garden.color :refer (hsl rgb)]))

(defn css-page [req]
  (-> (css
       [:h1 :h2 :h3 {:color (rgb 0 0 154)}]
       #_[:td {:font-family "monospace" :font-size (pt 12)}]
       #_[:td.numeric {:text-align :right}]
       #_[:th.numeric {:text-align :right}]
       [:div.container-narrow {:margin-left (pt 10) :font-size (pt 12)}]
       [:dt {:float :left}]
       [:dd {:margin-left (em 12 )}]
       [:p {:width (em 60)}]
       [:div.index {:padding-left (percent 20)
                    :padding-top (em 2)
                    }]
       )
      ring-resp/response
      (ring-resp/content-type "text/css")))
