(ns ttg.handler
  (:require
			[compojure.core :refer :all]
      [compojure.route :as route]
      [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
      [ring.middleware.multipart-params :refer [wrap-multipart-params]]
			[ring.util.response :as resp]
			[ttg.api :refer :all]
      [ttg.dbmethods :refer :all])
  (:use ring.adapter.jetty))

(defroutes app-routes
  (GET "/" [] (resp/content-type (resp/resource-response "index.html" {:root "public"}) "text/html"))
  (GET "/admin" [] (resp/content-type (resp/resource-response "admin.html" {:root "public"}) "text/html"))
  
  (route/resources "/")
  (context "/api" [] api-routes)
  (route/not-found "Not Found"))

(def app
  (wrap-multipart-params (wrap-defaults app-routes api-defaults)))

(defn -main
  [& args]
  (init-db)
  (run-jetty app {:port 3000}))