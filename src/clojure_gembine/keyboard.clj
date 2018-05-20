(ns clojure-gembine.keyboard
  (:require [clojure-gembine.utils :as utils])
  (:import [java.awt.event KeyEvent]))

(def arrows {:up KeyEvent/VK_UP
             :down KeyEvent/VK_DOWN
             :left KeyEvent/VK_LEFT
             :right KeyEvent/VK_RIGHT})

(defn tap-key
  "Do a quick tap on the keyboard"
  ([code]
   (tap-key (utils/new-robot) code))
  ([robot code]
   (doto robot
     (.keyPress code)
     (.keyRelease code))))

(defn tap-character
  "Tap the specific char"
  ([char]
   (tap-character (utils/new-robot) char))
  ([robot char]
   (tap-key robot
            (KeyEvent/getExtendedKeyCodeForChar (int char)))))

(defn tap-space
  ([] (tap-space (utils/new-robot)))
  ([robot]
   (tap-key robot KeyEvent/VK_SPACE)))

(defn tap-arrow
  ([direction]
   (tap-arrow (utils/new-robot) direction))
  ([robot direction]
   (tap-key robot (get arrows direction))))

