(ns beanbag.core)

(defn starts-with-ok [keyname]
  (re-matches #"^ok-(.*)" keyname))

(defn successful? [status-key]
  (let [keyname (name status-key)]
    (or (= keyname "ok")
        (starts-with-ok keyname))))

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
  "Report an unhandled condition. Default status key is :skipped. Status key must not begin with :ok"
  ([message]
     (fail :skip message))
  ([custom-key message]
     (fail custom-key message)))

(defmacro when-result
  "Call a function and then branch based on whether or not the function call
succeeded. Whatever you pass in as the data-var will be set to the function result
if the call is successful, or to an error message if the call fails. Returns the
function result if the function succeeds, or nil if the function fails."
  [data-var the-fn & body]
  `(let [[status-key# ~data-var] ~the-fn]
     (condp = status-key#
       ~@body)
     (when (successful? status-key#)
       ~data-var)))

(defn beanbag? [v]
  (if-let [m (meta v)]
    (::bb m)))

