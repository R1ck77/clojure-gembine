(ns clojure-gembine.utils
  (:require [clojure.java.io :refer [resource input-stream]])
  (:import [org.apache.commons.io IOUtils]
           [java.io File FileOutputStream]
           [javax.imageio ImageIO]
           [java.awt Rectangle Robot Toolkit]
           [org.opencv.highgui Highgui]
           [org.opencv.imgproc Imgproc]
           [org.opencv.core Core CvType Mat MatOfInt]))

(defn load-opencv-libraries []
  (clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME))

(defn sleep [ms]
  (Thread/sleep ms))

(defn- get-screen-size []
  (let [dimension (.getScreenSize (Toolkit/getDefaultToolkit))]
    [(.width dimension) (.height dimension)]))

(defn acquire-screen
  ([]
   (acquire-screen (Robot.)))
  ([robot]
   (let [[width height] (get-screen-size)]
     (.createScreenCapture robot (Rectangle. 0 0 width height)))))

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
  (Highgui/imread (.getAbsolutePath file) Highgui/IMREAD_COLOR))

(defn read-template
  "Read a java resource into a mat"
  [template-name]
  (let [temporary-file (template-as-file template-name)]
    (try
      (read-mat temporary-file)
      (finally
        (.delete temporary-file)))))

(defn bufimage-to-mat
  "This is a terribly slow and hacky conversion between java and OpenCV.

I could do something directly with MatOfInt, but I'll worry about this if 
it will be too slow in practice"
  [bufimage]
  (let [temp-file (File/createTempFile "gembine-conversion-" ".png")]
    (ImageIO/write bufimage "png" temp-file)
    (let [result (Highgui/imread (.getAbsolutePath temp-file) Highgui/IMREAD_COLOR)]
      (.delete temp-file)
      result)))

(defn create-match-template-output [image template]
  (let [output-rows (+ 1 (- (.rows image) (.rows template)))
        output-cols (+ 1 (- (.cols image) (.cols template)))]
    (Mat. output-rows output-cols CvType/CV_32FC1)))

(defn extract-max-from-mat [mat]
  (let [result (Core/minMaxLoc mat)
        maxVal (.maxVal result)
        maxLoc (.maxLoc result)]
    [(.x maxLoc) (.y maxLoc) maxVal]))

(defn match-template
  "Return the point in the bufimage that corresponds to the best match"
  [bufimage template]
  (let [input (bufimage-to-mat bufimage)
        result (create-match-template-output input template)]
    (Imgproc/matchTemplate input template result Imgproc/TM_CCOEFF_NORMED)
    (extract-max-from-mat result)))

(defn dump-mat [filename mat]
  (Highgui/imwrite filename mat))

(defn dump-bufimage [filename bufimage]
  (ImageIO/write bufimage "png" (clojure.java.io/file filename)))

(defn nano-to-milli [nano]
  (/ nano 1e6))

(defmacro slower-than [delay & body]
  `(let [start# (System/nanoTime)
         result# (do ~@body)
         length# (nano-to-milli (- (System/nanoTime) start#))]
     (when (< length# ~delay)
       (sleep (- ~delay length#)))
     result#))
