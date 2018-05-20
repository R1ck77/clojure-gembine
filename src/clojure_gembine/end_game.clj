(ns clojure-gembine.end-game
  (:require [clojure-gembine.utils :as utils]
            [clojure-gembine.parameters :refer :all]))

(defn secret-level? [screenshot]
  (let [secret-area-region (get-screen-section screenshot secret-level-area)]
    (> (nth (utils/match-template secret-area-region entering-secret-area) 2) 0.8)))

(defn end-game-ritual [robot]
  
  )
