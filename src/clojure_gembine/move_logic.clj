(ns clojure-gembine.move-logic
  (:require [clojure-gembine.game :as game]
            [clojure-gembine.board :as board]
            [clojure-gembine.score :as score]))

(def moves [:up :down :left :right])

(defn- compare-moves
  [[_ points]]
  (- points))

(defn minimax-moves-evaluator [board next-element]
  (get-in
   (vec
    (sort-by compare-moves
             (map (fn [[move boards]]
                    (vector move (apply min (map score/simple-score boards))))
                  (game/evolve-board board #{next-element}))))
   [0 0]))

(defn- extract-expanded-moves-composite-score
  "Return a score in the form: [average, contains :game-over yes/no] "
  [boards]
  (let [has-game-over (boolean (some #{:game-over} boards))]
    (vector (/ (apply + (map score/simple-score boards)) (count boards))
            has-game-over)))

(defn- expanded-moves [board next-elements]
  (mapcat second (game/evolve-board board next-elements)))

(defn- expand-moves
  [boards next-elements]
  (vec
   (map (fn [board]
          (if (= :game-over board)
            [(score/simple-score :game-over) true]
            (extract-expanded-moves-composite-score (expanded-moves board next-elements))))
        boards)))

(defn- rate-expanded-moves [composite-scores]
  (if (some #(not (second %)) composite-scores)
    (apply min (map first composite-scores))
    (score/simple-score :game-over)))

(defn- keep-only-feasible
  "Filter out impossible moves.

  Required because the 2-deep minimax doesn't distinguishes between immediate game-over and late one."
  [board results]
  (filter (fn [[move _]] (game/can-move? board move))
          results))

(defn create-minimax-two-ahead-moves-evaluator
  "Create a minimax that looks 2 steps ahead. Not optimized, sketched code

Ignores the weight of dead paths…"
  []
  (let [allowed-next-elements (atom #{:rb :rB})]
    (fn [board next-element]
      (swap! allowed-next-elements #(conj % next-element))
      (get-in (vec (keep-only-feasible board
                    (sort-by compare-moves
                             (map (fn [[move boards]]
                                    (vector move (rate-expanded-moves (expand-moves boards @allowed-next-elements))))
                                  (game/evolve-board board #{next-element})))))
              [0 0]))))




