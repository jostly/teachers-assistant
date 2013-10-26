(ns se.citerus.teachersassistant.views
  (:use [hiccup core page element]))

(defn assistant-page []
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title "TDD Assistant"]
    (include-css "/css/assistant.css")
    (include-js "/js/jquery-1.10.2.min.js")
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
    ]))
