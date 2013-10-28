(ns se.citerus.teachersassistant.routes
  (:use compojure.core
        se.citerus.teachersassistant.views
        se.citerus.teachersassistant.config
        se.citerus.teachersassistant.buildsystem
        se.citerus.teachersassistant.filehelper
        ;se.citerus.teachersassistant.browser
        )
  (:require [liberator.core :refer [resource defresource]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]
            [clojure.edn :as edn]))

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

(defresource browse-resource
  :available-media-types ["text/html"]
  :handle-ok (fn [ctx] (browse-page (get-in ctx [:request :params :*])))
  )

(defn- zip-file-name []
  (str (:project-name config)
       "-"
       (.format (java.text.SimpleDateFormat. "yyyy-MM-dd_HHmmss") (java.util.Date.))
       ".zip"))

(defroutes app
  (GET "/" [] (index-page))
  (GET "/assistant" [] (assistant-page))
  (GET "/build" [] build-resource)
  (GET "/browse/*" [] browse-resource)
  (GET "/download" []
       (let [zip-file (zip-dir (:project-dir config))]
         {:headers
          {"Content-Disposition"
           (str "attachment; filename=" (zip-file-name))}
         :body zip-file }))
  (route/resources "/")
  (route/not-found "Page not found"))

(defn boot []
  ; validate settings
  (if (not (.exists (clojure.java.io/file (:project-dir config) "build.gradle")))
    (throw (RuntimeException.
            (format "There is no gradle build file in %s"
                    (:project-dir config)))))
  (if (not (.exists (clojure.java.io/file (:project-dir config) (:test-report-dir config))))
    (throw (RuntimeException.
            (format "There is no test report dir %s"
                    (:test-report-dir config)))))
  ; start the builder
  (.start (Thread. #(build-watch (:project-dir config)))))
