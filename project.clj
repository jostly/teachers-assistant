(defproject teachers-assistant "0.9.0"
  :description "Teacher's Assistant"
  :repositories [["releases" "http://repo.gradle.org/gradle/libs-releases-local"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.zeroturnaround/zt-zip "1.6"]
                 [org.gradle/gradle-tooling-api "1.8"]
                 [org.slf4j/slf4j-simple "1.7.2"]
                 [compojure "1.1.3"]
                 [liberator "0.9.0"]
                 [hiccup "1.0.4"]]
  :plugins [[lein-ring "0.8.7"]]
  :ring { :handler se.citerus.teachersassistant.routes/app
          :init se.citerus.teachersassistant.routes/boot })
