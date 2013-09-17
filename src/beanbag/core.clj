(ns beanbag.core)

(defn
  ^{:doc "Return function result with a status flag indicating the function returned
successfully."}
  result [data]
  [:ok data])

(defn
  ^{:doc "Report an error condition. Default status key is :fail."}
  toss
  ([error-message] (toss :fail error-message))
  ([status-key error-message]
     (when (= status-key :ok)
       (throw (Exception. "Cannot use toss with a status key of :ok")))
     [status-key error-message]))

(defmacro
  ^{:doc "Call a function and then branch based on whether or not the function call
succeeded. Whatever you pass in as the data-var will be set to the function result
if the call is successful, or to an error message if the call fails. Returns the
function result if the function succeeds, or nil if the function fails."}
  with-result [data-var the-fn & body]
  `(let [[status-key# ~data-var] ~the-fn]
     (condp = status-key#
       ~@body)
     (when (= status-key# :ok)
       ~data-var)))
