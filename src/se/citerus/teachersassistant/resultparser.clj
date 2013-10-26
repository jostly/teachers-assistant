(ns se.citerus.teachersassistant.resultparser
  (:use se.citerus.teachersassistant.filehelper)
  (:require [clojure.xml :as xml]))


(defn- parse-failure [content]
  (first
    (map #(:message (:attrs %1))
         (filter #(= :failure (:tag %)) content))))

(defn parse-junit [path]
  (let [c (xml/parse (.toFile path))
        tests (filter #(= :testcase (:tag %)) (:content c))]
    (map #(assoc (:attrs %1) :failure (parse-failure (:content %1))) tests)))

;(parse-junit (get-path "TEST-failed-junit-example.xml"))
