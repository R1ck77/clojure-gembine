(ns clojure-gembine.core
  (:import [java.awt Robot Rectangle]
           [java.awt.event KeyEvent]
           [javax.imageio ImageIO])
  (:require [clojure-gembine.parameters :refer :all]
            [clojure-gembine.utils :as utils]
            [clojure-gembine.board :as board]
            [clojure-gembine.game :as game]
            [clojure-gembine.move-logic :as move-logic]
            [clojure-gembine.preview :as preview])
  (:gen-class))

(def move-delay-ms 1000)

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

(defn- perform-move [robot direction]
  (move robot direction)
  (let [screenshot (utils/acquire-screen robot)]
    (when (preview/is-game-over? screenshot)
      (println "New game!")
      (let [output (java.io.File/createTempFile "screenshot-" ".png")]
        (println (format "Saving screenshot to %s…" (.getAbsolutePath output)))
        (javax.imageio.ImageIO/write screenshot "PNG" output))
      (tap-space)
      (utils/sleep move-delay-ms))))

(defn move-randomly
  "Must return true when is game over, nil otherwise"
  [robot]
  (perform-move robot (rand-nth game/moves)))

(defn move-with-logic
  [robot logic]
  (let [before-move (utils/acquire-screen robot)
        initial-board (board/read-board before-move)
        next-symbol (preview/next-move before-move)]
    (perform-move robot (logic initial-board next-symbol))))

(defn execute-moves [function]
  (let [robot (new-robot)
        logic move-logic/minimax-moves-evaluator]
    (when (is-gembine? (utils/acquire-screen robot))
     (dorun
      (repeatedly #(function robot logic))))))

(defn -main
  "Start a gembine automatic player in 10 seconds"
  [& args]
  (doall (map (fn [n]
                (print (format "Starting in %d…    \r" n))
                (flush)
                (utils/sleep 1000))
              (range 10 -1 -1)))
  (println "Starting!                      ")
  (execute-moves move-with-logic))
