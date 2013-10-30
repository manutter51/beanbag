(ns beanbag.core)

(defn starts-with-ok [keyname]
  (re-matches #"^ok-(.*)" keyname))

(defn successful? [status-key]
  (if (keyword? status-key)
    (let [keyname (name status-key)]
      (or (= keyname "ok")
          (starts-with-ok keyname)))))

(defn ok
  "Return function result with a status key indicating the function returned successfully."
  ([] (with-meta [:ok] {::bb true}))
  ([data]
     (with-meta [:ok data] {::bb true}))
  ([custom-key data]
     (when (not (successful? custom-key))
       (throw (Exception. "Custom status key must start with \":ok-\".")))
     (with-meta [custom-key data] {::bb true})))

(defn fail
  "Report an error condition. Default status key is :fail. Custom status key must not begin with :ok."
  ([] (fail :fail "Failed"))
  ([error-message] (fail :fail error-message))
  ([custom-key error-message]
     (when (successful? custom-key)
       (throw (Exception. "Cannot use fail with a status key that starts with :ok")))
     (with-meta [custom-key error-message] {::bb true})))

(defn skip
  "Report an unhandled condition. Default status key is :skip. Status key must not begin with :ok"
  ([message]
     (fail :skip message))
  ([custom-key message]
     (fail custom-key message)))

(defmacro cond-result
  "Call a function and then branch based on whether or not the function call
succeeded. The data returned by the function is bound to the symbol you pass
in as the data-var, and branching is controlled by the status key returned
in the beanbag.

Example:

    (cond-result my-data (some-fn 42)
      :ok (println \"Successful, answer was \" my-data)
      :fail (println \"Unable to determine answer, error message was: \" my-data)
      :skip (println \"Answer unclear, please try again later. Reason: \" my-data)
      (println \"Unexpected result status!? \" my-data)"
  [data-var the-fn & body]
  `(let [[status-key# ~data-var] ~the-fn]
     (condp = status-key#
       ~@body)))

(defn beanbag? [v]
  (if-let [m (meta v)]
    (::bb m)))

