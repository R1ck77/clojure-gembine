(ns clojure-gembine.test-utils
  (:require [clojure.java.io :refer [resource]])
  (:import [javax.imageio ImageIO]))

(defn load-mock-image []
  (ImageIO/read (resource "mock/modified-with-game-over-screen.png")))
