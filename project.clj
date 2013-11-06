(defproject beanbag "0.2.3"
  :description "Like try/catch/throw, but not as hardball."
  :url "https://github.com/manutter51/beanbag"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.6-beta1"]]
                   :plugins [[lein-midje "3.1.1"]]}
            })
