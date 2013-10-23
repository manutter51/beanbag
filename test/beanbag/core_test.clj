(ns beanbag.core-test
  (:require [midje.sweet :refer :all]
            [beanbag.core :refer :all]))

(defn always-fails []
  (toss "Function failed"))

(defn fails-with-custom []
  (toss :not-ready "Function not ready"))

(defn always-succeeds []
  (return-result {:data "Some data."}))

(defn succeeds-with-condition []
  (return-result :ok-ignore "Some data you can ignore."))

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

(facts "about return-result"
      (let [some-state (atom nil)]
        (when-result r (always-succeeds)
                     :ok (reset! some-state "ok")
                     :fail (reset! some-state "fail")
                     :not-ready (reset! some-state "not-ready"))
        @some-state => "ok"
        (when-result r (succeeds-with-condition)
                     :ok (reset! some-state "ok")
                     :ok-ignore (reset! some-state "ok-ignore")
                     :fail (reset! some-state "fail")
                     :not-ready (reset! some-state "not-ready"))
        @some-state => "ok-ignore"))

(fact "Beanbags have metadata identifying them as beanbags"
      (beanbag? [:any :vector])
      => nil

      (beanbag? (return-result :ok))
      => truthy

      (beanbag? (toss "Sample failure"))
      => truthy)
