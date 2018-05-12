(ns clojure-gembine.utils
  (:require [clojure.java.io :refer [resource input-stream]])
  (:import [org.apache.commons.io IOUtils]
           [java.io File FileOutputStream]
           [org.opencv.highgui Highgui]))

(defn load-opencv-libraries []
  (clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME))

(defn template-as-file
  "Load a template from the resources and save it to a temporary file

From a cursory search, OpenCV likes files it would seem…"
  [template-name]
  (let [temp-file (File/createTempFile "gembine-template-" (str "-" template-name))]
    (with-open [output (FileOutputStream. temp-file)
                input (input-stream (resource (str "templates/" template-name)))]
      (IOUtils/copy input output))
    temp-file))

(defn- read-mat 
  "Convert a file into a OpenCV mat"
  [file]
  (Highgui/imread (.getAbsolutePath file)))

(defn read-template
  "Read a java resource into a mat"
  [template-name]
  (let [temporary-file (template-as-file template-name)]
    (read-mat temporary-file)
    (.delete temporary-file)))
