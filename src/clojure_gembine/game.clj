(ns clojure-gembine.game)

(def next-cell {:rb :rB
                :rB :rt
                :rt :rs
                :rs :rp
                :rp :gb
                :gb :gB
                :gB :gt
                :gt :gs
                :gs :gp
                :gp :bb
                :bb :bB
                :bB :bt
                :bt :bp
                :bp :bp}) ;;; this one is completely made up: I don't know what comes here

(def moves #{:up :down :left :right})

(defn rotate
  "Rotate the board in a way that the right move corresponds to the previous up move"
  [board]
  (vec
   (map (fn [index] (vec (map #(nth % index) (reverse board))))
        [0 1 2 3])))

;;; TODO/FIXME these rule are overkill. They can probably be simplified…
(defn- tuple-conversion [a b]
  (cond   
    ;;; same symbol on both → transmutation
    (and (= a b) (not (nil? a))) [(get next-cell a) nil]
    ;;; second cell empty → no movement
    (nil? b) [a nil]
    ;;; first empty
    (nil? a) [b nil]
    ;;; different symbols → no movement
    (not= a b) [a b]
    ;;; fail early in case of unexpected results
    true (throw (RuntimeException. (format "Unexpected result [%s %s]" a b)))))

(defn- reduce-tuple [{result :result seed :seed} value]
  (let [[next-a next-b] (tuple-conversion seed value)]
    {:result (conj result next-a)
     :seed next-b}))

(defn- step-row [row]
  (let [{result :result, seed :seed} (reduce reduce-tuple
                                             {:result []
                                              :seed (first row)}
                                             (rest row))]
    (conj (vec result) seed)))

(defn step
  "Performs a left move on the board, stopping before the new element addition"
  [board]
  [(step-row (nth board 0))
   (step-row (nth board 1))
   (step-row (nth board 2))
   (step-row (nth board 3))])

(defn- Tf [board direction f] 
  (case direction
    :down (-> board rotate f)
    :up (-> board rotate rotate rotate f)
    :left (f board)
    :right (-> board rotate rotate f)))


(defn- TfinvT [board direction f] 
  (case direction
    :down (-> board rotate f rotate rotate rotate)
    :up (-> board rotate rotate rotate f rotate)
    :left (f board)
    :right (-> board rotate rotate f rotate rotate)))

(defn move
  "Perform the specified move, which might have no result."
  [board direction]
  (TfinvT board direction step))

(defn can-move?
  "Check whether the board can be moved in the specific direction.

Easiest (and dumbest) way is to try to perform the move and see if anything changed.

Good enough performance wise, at least for now."
  [board direction]
  (not (= board (move board direction))))

(def direct-transform-for-move {:down #(-> % rotate)
                                :up #(-> % rotate rotate rotate)
                                :left identity
                                :right #(-> % rotate rotate)})

(def inverse-transform-for-move {:down #(-> % rotate rotate rotate)
                                 :up #(-> % rotate)
                                 :left identity
                                 :right #(-> %  rotate rotate)})

(defn all-left-insertions-of
  [board element]
  (map #(assoc-in board [% 3] element)
       (filter #(nil? (get-in board [% 3]))
               [0 1 2 3])))

(defn all-left-insertions [board candidates]
  (mapcat #(all-left-insertions-of board %) candidates))

(defn move-outcomes
  [board move next-candidates]
  (vec (map #(if (= :game-over %) :game-over ((get inverse-transform-for-move move) %))
        (let [rotated-board ((get direct-transform-for-move move) board)]
          (let [evolved-board (step rotated-board)]
            (if (= evolved-board rotated-board)
              [:game-over]
              (all-left-insertions evolved-board next-candidates)))))))

(defn evolve-board
  "Evolve a board into a vector of possible outcomes"
  [board next-candidates]
  (let [ordered-moves (vec moves)]
    (zipmap ordered-moves
            (map #(move-outcomes board % next-candidates)
                 ordered-moves))))
