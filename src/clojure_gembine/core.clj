(ns clojure-gembine.core
  (:import [java.awt Robot Rectangle]
           [javax.imageio ImageIO])
  (:require [clojure-gembine.parameters :refer :all]
            [clojure-gembine.utils :as utils]
            [clojure-gembine.board :as board]
            [clojure-gembine.game :as game]
            [clojure-gembine.move-logic :as move-logic]
            [clojure-gembine.preview :as preview]
            [clojure-gembine.end-game :as end-game]
            [clojure-gembine.keyboard :as keyboard])
  (:gen-class))

(def move-delay-ms 1000)

(def license-blurb "clojure-gembine - Copyright (C) 2018  Riccardo Di Meo
This program comes with ABSOLUTELY NO WARRANTY; This is free software, 
and you are welcome to redistribute it under the terms of the GPL v3")

(defn move
  ([direction]
   (move (utils/new-robot) direction))
  ([robot direction]
   (keyboard/tap-arrow robot direction)
   (utils/sleep move-delay-ms)))

(defn is-gembine? 
  [screenshot]
  (let [title-region (get-screen-section screenshot title-area)]
    (> (nth (utils/match-template title-region gembine-title) 2) 0.95)))

(defn- perform-move [robot direction]
  (move robot direction)
  (let [screenshot (utils/acquire-screen robot)]
    (when (end-game/secret-level? screenshot)
      (end-game/end-game-ritual robot))
    (when (preview/is-game-over? screenshot)
      (println "New game!")
      (let [output (java.io.File/createTempFile "screenshot-" ".png")]
        (println (format "Saving screenshot to %s…" (.getAbsolutePath output)))
        (javax.imageio.ImageIO/write screenshot "PNG" output))
      (keyboard/tap-space)
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

(defn execute-moves
  ([function]
   (execute-moves function move-logic/minimax-moves-evaluator))
  ([function logic]
   (let [robot (utils/new-robot)]
     (if (is-gembine? (utils/acquire-screen robot))
       (dorun
        (repeatedly #(function robot logic)))
       (println "This doesn't look like gembine! Aborting…")))))

(defn -main
  "Start a gembine automatic player in 10 seconds"
  [& args]
  (println license-blurb)
  (doall (map (fn [n]
                (print (format "Starting in %d…    \r" n))
                (flush)
                (utils/sleep 1000))
              (range 10 -1 -1)))
  (println "Starting!                      ")
  (execute-moves move-with-logic))
