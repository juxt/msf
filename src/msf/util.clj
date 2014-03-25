(ns msf.util
  (:require [msf.hash :refer (sha1)]))

(defn field-hash [x]
  (sha1 x))
