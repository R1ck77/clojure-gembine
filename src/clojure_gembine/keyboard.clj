(ns clojure-gembine.keyboard
  (:require [clojure-gembine.utils :as utils])
  (:import [java.awt.event KeyEvent]))

(def arrows {:up KeyEvent/VK_UP
             :down KeyEvent/VK_DOWN
             :left KeyEvent/VK_LEFT
             :right KeyEvent/VK_RIGHT})

(defn tap-key
  "Do a quick tap on the keyboard"
  [robot code]
  (.keyPress robot code)
  (utils/sleep 100)
  (.keyRelease robot code))

(defn tap-character
  "Tap the specific char"
  [robot char]
  (tap-key robot
           (KeyEvent/getExtendedKeyCodeForChar (int char))))

(defn tap-space
  [robot]
  (tap-key robot KeyEvent/VK_SPACE))

(defn tap-enter
  [robot]
  (tap-key robot KeyEvent/VK_ENTER))

(defn tap-esc
  [robot]
  (tap-key robot KeyEvent/VK_ESCAPE))

(defn tap-arrow
  [robot direction]
  (tap-key robot (get arrows direction)))

