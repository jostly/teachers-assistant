(ns se.citerus.teachersassistant.filehelper
  (:use se.citerus.teachersassistant.config)
  (:import [java.nio.file Paths Files LinkOption WatchEvent$Kind WatchEvent$Modifier StandardWatchEventKinds]
           [com.sun.nio.file SensitivityWatchEventModifier]
           [java.io File]
           [org.zeroturnaround.zip ZipUtil]))

(defn get-path [^String dir & dirs]
  "Join a sequence of strings into a java.nio.Path"
  (Paths/get dir (into-array String dirs)))

(defn do-each-file [func path file-pattern]
  "Calls func on each file in path matching file-pattern"
  (with-open [dir-stream (Files/newDirectoryStream path file-pattern)]
    (doall (map func dir-stream)))) ; Force evaluation so we can close dir-stream

(defn delete-file [path]
  (Files/delete path))

(defn delete-files [path file-pattern]
  "Delete files in path matching file-pattern"
  (do-each-file delete-file path file-pattern))

(def watch-events (into-array WatchEvent$Kind
                              [StandardWatchEventKinds/ENTRY_CREATE,
                               StandardWatchEventKinds/ENTRY_DELETE
                               StandardWatchEventKinds/ENTRY_MODIFY]))
(def watch-modifiers (into-array WatchEvent$Modifier
                                 [SensitivityWatchEventModifier/HIGH]))

(defn- directory? [path]
  (Files/isDirectory path (into-array LinkOption [])))

(defn- register [watch path]
  (.register path watch watch-events watch-modifiers)
  (with-open [dir-stream (Files/newDirectoryStream path)]
    (doall (map #(register watch %) (filter directory? dir-stream)))))

(defn watch-path [path]
  (with-open [watch (.. path getFileSystem newWatchService)]
    (register watch path)
    (.take watch)))

(defn watch [dir] (watch-path (get-path dir)))

(defn zip-dir [dir]
  (let [zip-file (File/createTempFile "t-a" ".zip")]
    (ZipUtil/pack (clojure.java.io/file dir)
                  zip-file)

    zip-file))
