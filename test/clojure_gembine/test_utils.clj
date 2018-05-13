(ns clojure-gembine.test-utils
  (:require [clojure.java.io :refer [resource]])
  (:import [javax.imageio ImageIO]))

(defn load-mock-image [id]
  (ImageIO/read (resource (str "mock/gembine-" id ".png"))))
