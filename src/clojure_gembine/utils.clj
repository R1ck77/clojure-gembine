(ns clojure-gembine.utils
  (:require [clojure.java.io :refer [resource input-stream]])
  (:import [org.apache.commons.io IOUtils]
           [java.io File FileOutputStream]
           [javax.imageio ImageIO]
           [java.awt Rectangle Robot Toolkit]
           [java.awt.image AffineTransformOp BufferedImage]
           [java.awt.geom AffineTransform]
           [java.util ArrayList]
           [org.opencv.imgproc Imgproc]
           [org.opencv.imgcodecs Imgcodecs]
           [org.opencv.core Core CvType Mat MatOfInt]))

(def HD-size [1920 1080])

(defn load-opencv-libraries []
  (clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME))

(load-opencv-libraries)

(defn sleep [ms]
  (Thread/sleep ms))

(defn new-robot []
  (Robot.))

(defn- get-screen-size []
  (let [dimension (.getScreenSize (Toolkit/getDefaultToolkit))]
    [(.width dimension) (.height dimension)]))

(defn- force-resize-to-fullHD [screenshot [x-factor y-factor]]
  (let [affine-transform (doto (AffineTransform.)
                           (.scale x-factor y-factor))
        affine-transform-op (AffineTransformOp. affine-transform AffineTransformOp/TYPE_BILINEAR)
        output (BufferedImage. (first HD-size) (second HD-size) (.getType screenshot))]
    (.filter affine-transform-op screenshot output)
    output))

(defn- resize-to-fullHD [screenshot]
  (let [orig-size [(.getWidth screenshot)
                   (.getHeight screenshot)]]
    (if (= orig-size HD-size)
      screenshot
      (force-resize-to-fullHD screenshot (map / HD-size orig-size)))))

(defn- capture-screen [robot [x y width height]]
  (.createScreenCapture robot (Rectangle. x y width height)))

(defn acquire-screen
  "It resizes the capture screen to full HD

Granted, it's slower than resizing only the tiles to match, but it's soooooo easy :D"
  ([]
   (acquire-screen (Robot.)))
  ([robot]
   (let [[width height] (get-screen-size)]
     (resize-to-fullHD (capture-screen robot [0 0 width height])))))

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
  (Imgcodecs/imread (.getAbsolutePath file) Imgcodecs/IMREAD_COLOR))

(defn read-template
  "Read a java resource into a mat"
  [template-name]
  (let [temporary-file (template-as-file template-name)]
    (try
      (read-mat temporary-file)
      (finally
        (.delete temporary-file)))))

(defn- convert-mat-to-hue
  [src-mat]
  (let [dst-mat (Mat. (.rows src-mat) (.cols src-mat) CvType/CV_8UC3)
        channels (ArrayList. 3)]
    (Imgproc/cvtColor src-mat dst-mat Imgproc/COLOR_BGR2HSV)
    (Core/split dst-mat channels)
    (first channels)))

(defn read-hue-template
  "Read a java resource into a mat containing just the hue channel"
  [template-name]
  (let [temporary-file (template-as-file template-name)]
    (try
      (convert-mat-to-hue (read-mat temporary-file))
      (finally
        (.delete temporary-file)))))

(defn bufimage-to-mat
  "This is a terribly slow and hacky conversion between java and OpenCV.

I could do something directly with MatOfInt, but I'll worry about this if 
it will be too slow in practice"
  [bufimage]
  (let [temp-file (File/createTempFile "gembine-conversion-" ".png")]
    (ImageIO/write bufimage "png" temp-file)
    (let [result (Imgcodecs/imread (.getAbsolutePath temp-file) Imgcodecs/IMREAD_COLOR)]
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
  (Imgcodecs/imwrite filename mat))

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
