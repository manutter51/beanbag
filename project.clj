(defproject beanbag "0.2.0-SNAPSHOT"
  :description "Like try/catch/throw, but not as hardball."
  ;:url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [midje "1.6-beta1"]]
  :profiles {:dev {:plugins [[lein-midje "3.1.1"]]}
            })
