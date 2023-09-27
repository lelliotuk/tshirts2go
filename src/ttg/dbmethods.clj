(ns ttg.dbmethods
  (:require [clojure.java.jdbc :refer :all]
            [clojure.math.combinatorics :refer [cartesian-product]])
  (:gen-class))



(def db
  "Database information"
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.sqlite"
   })


(def skus
  "Every possible SKU (for database initialisation)"
  (mapv #(apply str %) (cartesian-product ["S" "M" "L" "X" "2"] 
                                          ["BK" "GY" "WE" "RD" "GN" "BE" "PE"] 
                                          ["S" "P"])))



(defn init-db 
  "Create database tables if they don't exist. Runs at start"
  []
  (db-do-commands db
                       [(create-table-ddl :stock
                                         [[:sku :text "PRIMARY KEY"]
                                          [:amount :int]]
                                         {:conditional? true})
                       (create-table-ddl :orders
                                         [[:id :integer "PRIMARY KEY AUTOINCREMENT"]
                                          [:timestamp :datetime :default :current_timestamp]
                                          [:fullname :text]
                                          [:address :text]
                                          [:sku :text]
                                          [:amount :int]
                                          [:status :int]]
                                         {:conditional? true})]))

(defn init-data 
  "Completely reset database, delete all data, recreate tables"
  []
  (execute! db ["DROP TABLE IF EXISTS stock"])
  (execute! db ["DROP TABLE IF EXISTS orders"])
  (init-db)
  (execute! db ["DELETE FROM stock"])
  (execute! db ["DELETE FROM orders"])
  (insert-multi! db :stock (mapv (fn [s] {:sku s :amount 100}) skus))
  "Success")

(defn db-get-sku-stock
  "Get stock for specific SKU"
  [sku]
  (let [result (query db ["select * from stock where sku = ?" sku] {:as-arrays true})]
    (if (empty? result)
      -1
      ((first result) :amount))))


(defn db-place-order
  "Create order"
  [fullname address sku amount]
  (if (<= amount (db-get-sku-stock sku)) (do
                                            (insert! db :orders {:fullname fullname :address address :sku sku :amount amount :status 0})
                                            (execute! db ["UPDATE stock SET amount = amount - ? WHERE sku = ?" amount sku]) true)
                                          false))

(defn db-set-stock 
  "Set stock for specific SKU"
  [sku amount]
  (update! db :stock {:amount amount} ["sku=?" sku] ))




(defn db-get-stock 
  "Get all stock"
  []
  (mapv (fn [r] [(r :sku) (r :amount)]) (query db ["select * from stock"])))

(defn db-get-orders 
  "Get all orders"
  []
  (mapv (fn [r] [(r :id) (r :timestamp) (r :fullname) (r :address) (r :sku) (r :amount) (r :status)]) (query db ["select * from orders"])))

