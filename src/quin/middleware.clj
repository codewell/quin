(ns quin.middleware
  (:require
   [quin.core :refer [get-token-string-from-request
                      unpack]]))

(defn read-token-middleware
  "Takes a request and unpacks the token and associates it with the request map"
  [request]
  (->
   request
   (get-token-string-from-request)
   (unpack)
   (merge request)))
