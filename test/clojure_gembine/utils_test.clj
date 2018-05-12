(ns clojure-gembine.utils-test
  (:require [clojure.test :refer :all]
            [clojure-gembine.utils :refer :all]))

(deftest test-template-as-file
  (testing "return a valid readable file"
    (let [result (template-as-file "red-bead.png")]
      (try
        (is (.exists result))
        (is (.canRead result))
        (finally
          (.delete result))))))
