(ns clojure-gembine.utils-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :refer [file input-stream resource]]
            [clojure-gembine.utils :refer :all])
  (:import [org.apache.commons.io IOUtils]
           [java.io FileInputStream]))

(deftest test-template-as-file
  (testing "return a valid readable file"
    (let [result (template-as-file "red-bead.png")]
      (is (.exists result))
      (is (.canRead result))
      (is (.delete result))))
  (testing "content of the copy and of the resource are identical"
    (let [temp-file (template-as-file "green-bead.png")]
      (with-open [expected (input-stream (resource "templates/green-bead.png"))
                  result (FileInputStream. temp-file)]
        (is (IOUtils/contentEquals expected result))
        (.delete temp-file)))))
