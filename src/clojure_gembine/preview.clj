(ns clojure-gembine.preview
  (:require [clojure-gembine.utils :as utils]
            [clojure-gembine.board :as board]
            [clojure-gembine.parameters :refer :all]))

(defn is-game-over?
  [screenshot]
  (let [message-region (get-screen-section screenshot message-area)]
    (> (nth (utils/match-template message-region game-over) 2) 0.8)))

(defn next-move
  [screenshot]
  (board/get-best-match (get-screen-section screenshot message-area)))
