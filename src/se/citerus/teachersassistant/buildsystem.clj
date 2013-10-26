(ns se.citerus.teachersassistant.buildsystem
  (:use se.citerus.teachersassistant.filehelper
        se.citerus.teachersassistant.config
        se.citerus.teachersassistant.resultparser))

(import '(org.gradle.tooling GradleConnector BuildException)
        '(java.io ByteArrayOutputStream))

(def test-report-key "There were failing tests. See the report at: ")
(def test-report-dir (:test-report-dir config))
(def test-report-filter (:test-report-filter config))
(def build-state (atom '()))

(defn- gradle-connection [path]
  "Create a Gradle tooling API connection for the specified directory"
  (let [connector (GradleConnector/newConnector)]
    (.forProjectDirectory connector (.toFile path))
    (.connect connector)))

(defn- gradle-launcher [connection & tasks]
  (let [launcher (.newBuild connection)
        outputStream (ByteArrayOutputStream.)]
    (.setStandardOutput launcher outputStream)
    (.setStandardError launcher outputStream)
    (.forTasks launcher (into-array String tasks))
    (.withArguments launcher (into-array String ["-q"]))))

(defn- failed-tests? [e]
  (if (nil? e)
    false
    (if (.startsWith (.getMessage e) test-report-key)
      true
      (failed-tests? (.getCause e)))))

(defn- test-path [projectPath]
  (.resolve projectPath test-report-dir))

(defn- build-path [projectPath]
  "Launch a gradle build in the specified directory"
  (delete-files (test-path projectPath) test-report-filter)
  (let [connection (gradle-connection projectPath)]
    (try
      (.run (gradle-launcher connection (:test-task config)))
      { :build :complete,
        :tests (flatten (do-each-file parse-junit (test-path projectPath) test-report-filter)) }
      (catch Exception e
        (if (failed-tests? e)
          { :build :complete,
            :tests (flatten (do-each-file parse-junit (test-path projectPath) test-report-filter)) }
          { :build :failed }))
      (finally (.close connection))
      )))

(defn build [pathString]
  (let [current-result (assoc (build-path (get-path pathString)) :timestamp (System/currentTimeMillis))]
    (swap! build-state #(cons current-result %))
    current-result
    ))

