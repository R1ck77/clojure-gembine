(ns clojure-gembine.core
  (:import [java.awt Robot Rectangle]
           [java.awt.event KeyEvent]
           [javax.imageio ImageIO])
  (:require [clojure-gembine.parameters :refer :all]
            [clojure-gembine.utils :as utils])
  (:gen-class))

(def move-delay-ms 400)

(def arrows {:up KeyEvent/VK_UP
             :down KeyEvent/VK_DOWN
             :left KeyEvent/VK_LEFT
             :right KeyEvent/VK_RIGHT})

(defn new-robot []
  (Robot.))

(defn- sleep [ms]
  (Thread/sleep ms))

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
   (sleep move-delay-ms)))

(defn is-game-over?
  [screenshot]
  (let [message-region (get-screen-section screenshot message-area)]
    (> (nth (utils/match-template message-region game-over) 2) 0.8)))

(defn is-gembine? 
  [screenshot]
  (let [title-region (get-screen-section screenshot title-area)]
    (> (nth (utils/match-template title-region gembine-title) 2) 0.95)))

(defn test-random-moves [delay]
  (sleep delay)
  (let [robot (new-robot)
        moves (keys arrows)]
    (dorun
     (take-while identity (repeatedly (fn []
                             (move robot (rand-nth moves))
                             (let [screenshot (utils/acquire-screen robot)]
                               (is-game-over? screenshot))))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
