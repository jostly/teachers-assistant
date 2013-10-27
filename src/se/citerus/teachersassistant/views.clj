(ns se.citerus.teachersassistant.views
  (:use se.citerus.teachersassistant.config
        [hiccup core page element]))

(defn- jquery [] (include-js "/js/jquery-1.10.2.min.js"))

(defn assistant-page []
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title "TDD Assistant"]
    (include-css "/css/assistant.css")
    (jquery)
    (include-js "/js/jquery-ui-1.10.3.custom.min.js")]
   [:body
    (include-js "/js/assistant.js")
    [:div#status "Status bar"]
    [:div#time]
    [:div#failed]]))

(defn index-page []
  (html5
   [:head
    [:title "Teacher's Pet"]
    (include-css "/css/style.css")
    ]
   [:body
    [:div
      [:a {:href "#"
           :onClick "window.open('/assistant', 'TDD Assistant', 'toolbar=yes,location=yes,directories=yes,status=no,menubar=yes,scrollbars=yes,resizable=yes,width=400,height=600'); return false;"}
       "Open Assistant in new window"]
     ]
    [:br]
    [:div
      [:a {:href "/assistant"} "Open Assistant"]
     ]
    [:br]
    [:div
     [:a {:href "/browse/"} "Browse"]
     ]
    [:br]
    [:div
     [:a {:href "/download"} "Download zip"]
     ]
    ]))

(defn- project-path [path]
  (clojure.java.io/file (:project-dir config) path))

(def project-name "root")

(defn- rel-path [path entry]
  (str "/browse/"
    (if (or
         (= "/" path)
         (= "" path))
      entry
      (str path "/" entry))))

(defn- directory-listing [path entry]
  [:tr.directory
   [:td
    (link-to (rel-path path entry) entry)
    ]]
  )

(defn- split-path-to-links [path]
  (loop [path-parts (clojure.string/split path #"/")
         current []
         result []]
    (if (first path-parts)
      (let [head (first path-parts)
            nc (if (= project-name head) current (conj current head))
            head-link (if (= project-name head) "" head)]

        (recur (rest path-parts)
               nc
               (conj result
                     (link-to (rel-path (clojure.string/join "/" current) head-link)
                              head))))
      result)))

(defn- make-linkable-header [path]
  (let [links (split-path-to-links (str project-name "/" path))]
    [:div.nav (interpose " / " links)]))

(defn- browse-directory [path pp]
  (html5
   [:head
    [:title "Browse " path]
    (include-css "/css/github.css")
    (include-css "/css/browse.css")
    (include-js "/js/highlight.pack.js")
    (jquery)
    ]
   [:body
    (make-linkable-header path)
    [:table.directory
     (map #(directory-listing path %) (.list pp))
     ]]))

(defn- browse-file [path pp]
  (html5
   [:head
    [:title "Browse " path]
    (include-css "/css/github.css")
    (include-css "/css/browse.css")
    (include-js "/js/highlight.pack.js")
    (jquery)
    [:script "hljs.tabReplace = '    '; hljs.initHighlightingOnLoad();"]
    ]
   [:body
    (make-linkable-header path)
    [:pre
     [:code (slurp pp)]]]))

(defn browse-page [path]
  (let [pp (project-path path)]
    (if (.isDirectory pp)
      (browse-directory path pp)
      (browse-file path pp))))

