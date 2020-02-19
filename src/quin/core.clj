(ns quin.core
  (:require
   [clojure.data.json :as json]
   [clojure.string :refer [split]]))

(defn decode-base64
  "Decode a base 64 string"
  [base64-string]
  (->
   (java.util.Base64/getDecoder)
   (.decode  base64-string)
   (String.)))

(defn base64-json->map
  "Decode the token contents and return edn"
  [base64]
  (->
   base64
   (decode-base64)
   (json/read-str :key-fn keyword)))

(defn token-sections-reducer
  "Takes array of token sections and reduces them into a map"
  [token-sections base64-jwt-string]
  ;; Assumes the order [ header payload signature ]
  (reduce (fn
            [accumulator section]
            (if
             (contains? accumulator :header)
              (if (contains? accumulator :payload)
                ;; If the :header section and :payload 
                ;; section is present
                (assoc accumulator :signature section)

                ;; If the :header section is present
                (assoc accumulator :payload (base64-json->map section)))

              ;; I the :header section is not present
              ;; then add it
              (assoc accumulator :header (base64-json->map section))))
          {:base64-jwt-string base64-jwt-string}
          token-sections))

(defn unpack
  "Unpacks the information of a jwt"
  [base64-jwt-string]
  (->
   base64-jwt-string

   ;; Split the jwt into the three sections
   ;; <header>.<payload>.<signature>
   ;; [<header>, <payload>, <signature>]
   (split #"\.")

   ;; Append the decoded data to the token map
   ;; {:header <header data> 
   ;;  :payload <payload data> 
   ;;  :signature <signature data>}
   (token-sections-reducer base64-jwt-string)))

(defn get-token-from-authorization-header
  "Get the token part of authorization header string"
  [authorization-header-string]
  (->
   authorization-header-string

;; Assuming the authorization string looks 
   ;; like "Bearer this-is-some-token"
   ;; split it into ["Bearer" "this-is-some-token"]
   (split #" ")

   ;; Then return the 2nd element in the list 
   ;; e.g. "this-is-some-token"
   (nth 1)))

(defn get-authorization-header
  "Get authorization header from Ring request"
  [request]
  (->
   request
   (:headers)
   (get "authorization")))

(defn get-token-string-from-request
  "Read jwt token from Authorization header"
  [request]
  (->
   request
   (get-authorization-header)
   (get-token-from-authorization-header)))

