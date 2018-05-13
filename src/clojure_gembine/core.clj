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

(defn is-game-over? ;; this one can be easily tested! Strange resuls:
  ;;; game over -> game-over 0.99999 no-message -0.07
  ;;; not game over -> game-over 0.068 no-message -0.044
  [screenshot]
  (let [message-region (get-screen-section screenshot message-area)]
    (> (nth (utils/match-template message-region game-over) 2)
       (nth (utils/match-template message-region no-message) 2))))

(defn test-random-moves [delay]
  (sleep delay)
  (let [robot (new-robot)
        moves (keys arrows)]
    (dorun
     (take-while identity (repeatedly (fn []
                             (println "moving!")
                             (move robot (rand-nth moves))
                             (let [screenshot (utils/acquire-screen robot)]
                               (is-game-over? screenshot))))))
    (println "Game over")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
