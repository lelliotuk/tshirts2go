(ns ttg.api
  (:require
			[compojure.core :refer :all]
      [compojure.route :as route]
			[ring.util.response :as resp]
      [ttg.dbmethods :refer :all]
      [clojure.data.json :as json]
      [clojure.string :refer [join blank?]]))
  

(defn in-stock
  "Check if specific quantity is available"
 [req] (let [form   (req :params)
             amount (Integer/parseInt (form :amount))] 
          (json/write-str {:available (and
            (<=
               amount
               (db-get-sku-stock (form :sku)))
            (> amount 0)  )})))

(defn buy [req] (let [form (req :params)]
  "Create order, validate address, check stock"
                 (json/write-str (if (some blank? [(form :fullName)
                                                   (form :address1)
                                                   (form :city)
                                                   (form :postcode)
                                                   (form :sku)
                                                   (form :amount)])
                                                        {:error "Some required fields are empty"}
                                                        {:success (db-place-order (form :fullName)
                                                                                (join "\n" [(form :address1) (form :address2) (form :city) (form :postcode)])
                                                                                (form :sku)
                                                                                (Integer/parseInt (form :amount))
                                                                                 )}))))


(defn get-stock
  "Get all stock"
  [req] (json/write-str (db-get-stock)))

(defn set-stock
  "Set stock for specific SKU"
  [req] (let [form (req :params)] (db-set-stock (form :sku) (Integer/parseInt (form :amount)))))

(defn get-orders 
  "Get all orders"
  [req] (json/write-str (db-get-orders)))


(def api-routes
  "API endpoints"
  (routes
    (GET "/init" [] (init-db))
    (GET "/reset" [] (init-data))
    
    (GET "/client/instock" [] in-stock)
    (POST "/client/buy" [] buy)
    
    (GET "/admin/getstock" [] get-stock)
    (POST "/admin/setstock" [] set-stock)
    
    (GET "/admin/getorders" [] get-orders)))