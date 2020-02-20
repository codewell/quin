(ns quin.middleware-test
  (:require [clojure.test :refer :all]
            [quin.middleware :refer :all]))

;; Standard dummy jwt from jwt.io
(def
  test-jwt
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")

(def
  test-jwt-map
  {:header {:alg "HS256"
            :typ "JWT"}

   :payload {:sub "1234567890"
             :name "John Doe"
             :iat 1516239022}

   :signature "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

   :base64-jwt-string test-jwt})

(def test-jwt-secret "your-256-bit-secret")

(def dummy-request {:headers {"authorization" (str "Bearer " test-jwt)}})

;; Token request middleware
(deftest token-middleware
  (testing "Request token middlware"
    (is (=
         (quin.middleware/read-token-middleware dummy-request)
         (merge dummy-request test-jwt-map)))))
