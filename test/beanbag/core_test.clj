(ns beanbag.core-test
  (:require [midje.sweet :refer :all]
            [beanbag.core :refer :all]))

(defn always-fails []
  (fail "Function failed"))

(defn fails-with-custom []
  (fail :not-ready "Function not ready"))

(defn always-succeeds []
  (ok {:data "Some data."}))

(defn succeeds-with-condition []
  (ok :ok-ignore "Some data you can ignore."))

(defn never-handled []
  (skip "Nothing happened"))

(fact "about default fail"
      (let [some-state (atom nil)]
        (cond-result r (always-fails)
                     :ok (reset! some-state "ok")
                     :fail (reset! some-state "fail"))
        @some-state => "fail"))

(fact "about custom fail"
      (let [some-state (atom nil)]
        (cond-result r (fails-with-custom)
                     :ok (reset! some-state "ok")
                     :fail (reset! some-state "fail")
                     :not-ready (reset! some-state "not-ready"))
        @some-state => "not-ready"))

(facts "about ok"
      (let [some-state (atom nil)]
        (cond-result r (always-succeeds)
                     :ok (reset! some-state "ok")
                     :fail (reset! some-state "fail")
                     :not-ready (reset! some-state "not-ready"))
        @some-state => "ok"
        (cond-result r (succeeds-with-condition)
                     :ok (reset! some-state "ok")
                     :ok-ignore (reset! some-state "ok-ignore")
                     :fail (reset! some-state "fail")
                     :not-ready (reset! some-state "not-ready"))
        @some-state => "ok-ignore"))

(fact "about skip"
      (let [some-state (atom nil)]
        (cond-result r (never-handled)
                     :ok (reset! some-state "ok")
                     :fail (reset! some-state "fail")
                     :skip (reset! some-state "skipped"))
        @some-state => "skipped"))

(fact "Beanbags have metadata identifying them as beanbags"
      (beanbag? [:any :vector])
      => nil

      (beanbag? (ok :ok))
      => truthy

      (beanbag? (fail "Sample failure"))
      => truthy)
