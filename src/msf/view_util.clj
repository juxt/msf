(ns msf.view-util
  (:require
   [clojure.set :as set]))

(defn to-table
  ([{:keys [column-order hide-columns formatters classes] :or {column-order (constantly 0)} :as options} rows]
     (if-not (empty? rows)
       (let [ks (sort-by column-order
                         (-> (set (keys (first rows)))
                             (set/union (set (keys formatters)))
                             (set/difference  hide-columns)))]
         [:table.table
          [:thead
           [:tr
            (for [k ks]
              [:th {:class (k classes)} (name k)])]]
          [:tbody
           (for [row rows]
             (cond->>
              [:tr
               (for [k ks]
                 [:td {:class (k classes)}
                  ((or (k formatters) k) row)])]))]])
       [:p "(No data)"]))
  ([rows] (to-table {} rows)))
