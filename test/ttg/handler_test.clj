(ns ttg.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [ttg.handler :refer :all]
            [clojure.data.json :as json]))

(deftest test-app

  (testing "client page route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200)) ))
      
  (testing "admin page route"
    (let [response (app (mock/request :get "/admin"))]
      (is (= (:status response) 200)) ))
      
  (testing "static resource serving"
    (let [response (app (mock/request :get "/js/client.js"))]
      (is (= (:status response) 200)) ))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))



  (testing "database reset, and at least 10 of SKU 'SBKS' available"
    (let [response (app (mock/request :get "/api/reset"))]
      (is (= (response :body) "Success")))

    (let [response (app (mock/request :get "/api/client/instock?sku=SBKS&amount=10"))]
      (is (= ((json/read-str (response :body)) "available") true))))


  (testing "set stock, confirm"
    (app (mock/request :post "/api/admin/setstock?sku=SBKP&amount=1234"))
    (let [body (json/read-str ((app (mock/request :get "/api/admin/getstock")) :body))]
      (is (some (fn [x] (= x ["SBKP" 1234])) body)) ))
      
      
  (testing "place order, confirm"
    (app (mock/request :post "/api/client/buy?fullName=Elliot&address1=Some&address2=place&city=Sheffield&postcode=SSSSS&sku=MBKS&amount=5"))
    (let [body (json/read-str ((app (mock/request :get "/api/admin/getorders")) :body))]
      (is (some (fn [x] (and (= (x 2) "Elliot") (= (x 3) "Some\nplace\nSheffield\nSSSSS") (= (x 4) "MBKS") (= (x 5) 5))) body)) )))
