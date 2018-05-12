(ns clojure-gembine.utils
  (:require [clojure.java.io :refer [resource input-stream]])
  (:import [org.apache.commons.io IOUtils]
           [java.io File FileOutputStream]))

(defn template-as-file
  "Load a template from the resources and save it to a temporary file

From a cursory search, OpenCV likes files it would seem…"
  [template-name]
  (let [temp-file (File/createTempFile "gembine-template-" (str "-" template-name))]
    (with-open [output (FileOutputStream. temp-file)
                input (input-stream (resource (str "templates/" template-name)))]
      (IOUtils/copy input output))
    temp-file))

