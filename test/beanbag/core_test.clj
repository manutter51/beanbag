(ns beanbag.core-test
  (:require [midje.sweet :refer :all]
            [beanbag.core :refer :all]))


(defn always-fails []
  (toss "Function failed"))

(defn fails-with-custom []
  (toss :not-ready "Function not ready"))

(defn always-succeeds []
  (return-result {:data "Some data."}))

(fact "about default toss"
      (let [some-state (atom nil)]
        (when-result r (always-fails)
                     :ok (reset! some-state "ok")
                     :fail (reset! some-state "fail"))
        @some-state => "fail"))

(fact "about custom toss"
      (let [some-state (atom nil)]
        (when-result r (fails-with-custom)
                     :ok (reset! some-state "ok")
                     :fail (reset! some-state "fail")
                     :not-ready (reset! some-state "not-ready"))
        @some-state => "not-ready"))

(fact "about return-result"
      (let [some-state (atom nil)]
        (when-result r (always-succeeds)
                     :ok (reset! some-state "ok")
                     :fail (reset! some-state "fail")
                     :not-ready (reset! some-state "not-ready"))
        @some-state => "ok"))
