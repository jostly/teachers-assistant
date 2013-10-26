(ns se.citerus.teachersassistant.routes
  (:use compojure.core
        se.citerus.teachersassistant.views
        se.citerus.teachersassistant.buildsystem
        se.citerus.teachersassistant.filehelper)
  (:require [liberator.core :refer [resource defresource]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [clojure.edn :as edn]))

(def config
  (edn/read-string (slurp "config.edn")))

(defn build-watch [dir]
  (println "*** Project dir is " dir " ***")
  (loop [x dir]
    (build dir)
    (watch dir)
    (recur dir)))

(defn timestamp-to-age [entry]
  (assoc entry :age (- (System/currentTimeMillis) (:timestamp entry))))

(defresource build-resource
  :available-media-types ["application/json"]
  :handle-ok (map timestamp-to-age @build-state))

(defroutes app
  (GET "/" [] (index-page))
  (GET "/assistant" [] (assistant-page))
  (GET "/build" [] build-resource)
  (route/resources "/")
  (route/not-found "Page not found"))

(defn boot []
  (.start (Thread. #(build-watch (:project-dir config)))))