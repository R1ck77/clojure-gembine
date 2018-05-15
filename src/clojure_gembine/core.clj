(ns clojure-gembine.core
  (:import [java.awt Robot Rectangle]
           [java.awt.event KeyEvent]
           [javax.imageio ImageIO])
  (:require [clojure-gembine.parameters :refer :all]
            [clojure-gembine.utils :as utils]
            [clojure-gembine.preview :as preview])
  (:gen-class))

(def move-delay-ms 400)

(def arrows {:up KeyEvent/VK_UP
             :down KeyEvent/VK_DOWN
             :left KeyEvent/VK_LEFT
             :right KeyEvent/VK_RIGHT})

(defn new-robot []
  (Robot.))

(defn- rectangle [ax ay bx by]
  (Rectangle. ax ay bx by))

(defn tap-key
  "Do a quick tap on the keyboard"
  ([code]
   (tap-key (new-robot) code))
  ([robot code]
   (doto robot
     (.keyPress code)
     (.keyRelease code))))

(defn tap-character
  "Tap the specific char"
  ([char]
   (tap-character (new-robot) char))
  ([robot char]
   (tap-key robot
            (KeyEvent/getExtendedKeyCodeForChar (int char)))))

(defn tap-space
  ([] (tap-space (new-robot)))
  ([robot]
   (tap-key robot KeyEvent/VK_SPACE)))

(defn tap-arrow
  ([direction]
   (tap-arrow (new-robot) direction))
  ([robot direction]
   (tap-key robot (get arrows direction))))

(defn move
  ([direction]
   (move (new-robot) direction))
  ([robot direction]
   (tap-arrow robot direction)
   (utils/sleep move-delay-ms)))

(defn is-gembine? 
  [screenshot]
  (let [title-region (get-screen-section screenshot title-area)]
    (> (nth (utils/match-template title-region gembine-title) 2) 0.95)))

(defn test-dummy-move
  "Must return true when is game over, nil otherwise"
  [robot]
  (let [moves (keys arrows)]
    (move robot (rand-nth moves))
    (let [screenshot (utils/acquire-screen robot)]
      (when (preview/is-game-over? screenshot)
        (tap-space)
        (utils/sleep 2000)))))

(defn test-random-moves [delay]
  (utils/sleep delay)
  (let [robot (new-robot)]
    (when (is-gembine? (utils/acquire-screen robot))
     (dorun
      (take-while not (repeatedly #(test-dummy-move robot)))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
