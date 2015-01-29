# beanbag

A gentler, less hardball alternative to try/throw/catch.

## Usage

In non-trivial code, it's not uncommon to encounter situations where you need
a function that returns both a data result and a metadata status. For example,
you need a row from the database, but the function that returns the data also
needs to report any database errors that render the function result invalid.
Or you may wish to call a function that may or may not succeed, and for which
nil is a valid result (preventing you from using nil as a failure indicator).

Sometimes, you can even have functions whose result is neither a success nor
an error, and should simply be ignored. For example, you may call a service
that takes a while to start up. If the service isn't ready yet, you can't get
valid data from it, but you want to distinguish actual errors (the server
crashed) from simple delays (the server isn't ready yet). 

Beanbag allows you to return extended function results using an Erlang-ish
tuple containing a status key (:ok, :fail, or :skip) plus either a data value
(if the function returned ok) or a status message (in case of failed or skipped
results). For convenience, Beanbag also defines a cond-like macro that lets
you easily respond appropriately to the function results depending on their
status.

Inside your function calls, use (ok data) to indicate that the function
is returning successfully and to pass back the result. If your function finds
some kind of error condition that needs to be reported, but doesn't require
full-bore, build-a-stack-trace-and-longjump exception handling, use (fail msg) 
to return the failure status and an error message. Or use (skip msg) to 
indicate that the results are not valid, but should not be treated as an
error.

For more fine-grained distinctions between status conditions, you can define
your own custom status keys, as the first argument to ok, fail, or skip. Success
statuses should begin with ":ok-", but fail and skip can use any status keys
that you like (as long as they don't start with "ok-").

Wrap your function call inside a cond-result block to read the status keys
used by ok, fail and skip. The structure of the cond-result block is
similar to condp (and _is_ a condp under the hood).

    (ns your.namespace
      (:require [beanbag.core :as bb]))
    
    (defn some-fn-that-might-fail [arg]
      (if (even? arg)
        (bb/ok (* arg 3))
        (bb/fail "I only like even numbers")))

    (defn other-fn [arg]
      (if (nil? arg)
        (bb/skip :not-ready "Arg was nil, must not be ready yet.")
        (bb/ok (inc arg))))

    (defn careful-fn [f arg]
      (bb/cond-result res (f arg)
        :ok (println "Call succeeded and returned " res)
        :fail (println "Call failed. Reason: " res)
        :not-ready (println "Fn wasn't ready, try again later")))
    
    (careful-fn some-fn-that-might-fail 2) ; => prints 6
    (careful-fn some-fn-that-might-fail 3) ; => prints "I only like even numbers"
    (careful-fn other-fn nil) ;              => prints "Fn wasn't ready, try again later"
    (careful-fn other-fn 4) ;                => prints 5

The cond-result block returns the your function's result if the function
succeeds, or nil if the function fails.

Footnote: Use the (beanbag? coll) predicate to check whether a given data structure
is a beanbag or not.

## License

Copyright © 2013 Mark Nutter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[![Build Status](https://travis-ci.org/manutter51/beanbag.png)](https://travis-ci.org/manutter51/beanbag.png)
