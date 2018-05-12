(ns clojure-gembine.utils
  (:require [clojure.java.io :refer [resource input-stream]])
  (:import [org.apache.commons.io IOUtils]
           [java.io File FileOutputStream]
           [javax.imageio ImageIO]
           [org.opencv.highgui Highgui]
           [org.opencv.core Mat MatOfInt]))

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

(defn bufimage-to-mat
  "This is a terribly slow and hacky conversion between java and OpenCV.

I could do something directly with MatOfInt, but I'll worry about this if 
it will be too slow in practice"
  [bufimage]
  (let [temp-file (File/createTempFile "gembine-conversion-" ".png")]
    (ImageIO/write bufimage "png" temp-file)
    (let [result (Highgui/imread (.getAbsolutePath temp-file))]
      (.delete temp-file)
      result)))

(defn match-template
  "Return the point in the bufimage that corresponds to the better match"
  [bufimage mat]
  
  )
