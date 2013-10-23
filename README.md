# beanbag

A gentler, less hardball alternative to try/throw/catch.

## Usage

Inside your function calls, use return-result to indicate that the function
is returning successfully and to pass back the result. If your function finds
some kind of error condition that needs to be reported, but doesn't require
full-bore, build-a-stack-trace-and-longjump exception handling, use toss to
return the failure status and an error message.

Wrap your function call inside a when-result block to read the status keys
used by return-result and toss. The structure of the when-result block is
similar to condp (and _is_ a condp under the hood). The default error status
used by toss is :fail, but you can use any key you like (except :ok, which
is the success status).

    (ns your.namespace
      (:require [beanbag.core :refer :all]))
    
    (defn some-fn-that-might-fail [arg]
      (if (even? arg)
        (return-result (* arg 3))
        (toss "I only like even numbers")))

    (defn other-fn [arg]
      (if (nil? arg)
        (toss :not-ready "Arg was nil, must not be ready yet.")
        (return-result (inc arg))))

    (defn careful-fn [f arg]
      (when-result res (f arg)
        :ok (println "Call succeeded and returned " res)
        :fail (println "Call failed. Reason: " res)
        :not-ready (println "Fn wasn't ready, try again later")))
    
    (careful-fn some-fn-that-might-fail 2) ; => prints 6
    (careful-fn some-fn-that-might-fail 3) ; => prints "I only like even numbers"
    (careful-fn other-fn nil) ;              => prints "Fn wasn't ready, try again later"
    (careful-fn other-fn 4) ;                => prints 5

The when-result block returns the your function's result if the function
succeeds, or nil if the function fails.

Footnote: Use the (beanbag? coll) predicate to check whether a given data structure
is a beanbag or not.

## License

Copyright © 2013 Mark Nutter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[![Build Status](https://travis-ci.org/manutter51/beanbag.png)](https://travis-ci.org/manutter51/beanbag.png)
