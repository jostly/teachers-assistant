(ns se.citerus.teachersassistant.test.filehelper
  (:use clojure.test
        se.citerus.teachersassistant.filehelper))

(import '(java.nio.file Files))
(import '(java.nio.file.attribute FileAttribute PosixFilePermissions))

(deftest get-path-test
  (is (= "foo" (.toString (get-path "foo"))))
  (is (=
       (format "foo%sbar" (java.io.File/separator))
       (.toString (get-path "foo" "bar")))))

(def default-file-attributes
  (into-array FileAttribute [(PosixFilePermissions/asFileAttribute
   (PosixFilePermissions/fromString "rwxrwxrwx"))]))

(defn- create-temp-dir [prefix]
  (Files/createTempDirectory prefix default-file-attributes))

(defn- create-temp-file [tempDir]
  (Files/createTempFile tempDir "TEST-" ".xml" default-file-attributes))

(deftest delete-files-test
  (testing "Delete one file"
    (let [tempDir (create-temp-dir "foo")]
      (create-temp-file tempDir)
      (delete-files tempDir "TEST-*")
      (Files/delete tempDir)))
  (testing "Delete several files"
    (let [tempDir (create-temp-dir "foo")]
      (create-temp-file tempDir)
      (create-temp-file tempDir)
      (create-temp-file tempDir)
      (delete-files tempDir "TEST-*")
      (Files/delete tempDir))))

