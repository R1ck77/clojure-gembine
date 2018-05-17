(ns clojure-gembine.game)

(def next-cell {
                :rb :rB
                :rB :rt
                :rt :rs
                :rs :rp
                :rp :gb
                :gb :gB
                :gB :gt
                :gt :gs
                :gs :gp
                :gp :gp}) ;;; this one is completely made up

(def moves #{:up :down :left :right})

(defn rotate
  "Rotate the board in a way that the right move corresponds to the previous up move"
  [board]
  (vec
   (map (fn [index] (vec (map #(nth % index) (reverse board))))
        [0 1 2 3])))


(defn- tuple-conversion [a b]
  (cond   ;;; TODO/FIXME these rule are overkill. They can be simplified…
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

(defn- board-indexed-row-ends [board]
  (map #(vector % %2) [0 1 2 3 4] (map #(nth % 3) board)))

(defn- left-insertion-indices
  "Available row indices that have a space for insertion at the end"
  [board]
  (map first (filter #(nil? (second %)) (board-indexed-row-ends board))))

(defn insertion-indices
  "Available indices for insertion in the specified direction.

The indices follow the clockwise convention"
  [board direction]
  (Tf board direction left-insertion-indices))

(defn- left-insert-element
  "Left insert the specific element at the specific index.

Doesn't check for wrong indexes and overwrites the corresponding values.

Returns the new board"
  [board index element]
  (assoc-in board [index 3] element))

(defn insert-element
  "Insert a new element the way the game would

Doesn't check for wrong indexes and overwrites the corresponding values.

Returns the new board"
  [board direction index element]
  (TfinvT board direction #(left-insert-element % index element)))

(defn all-insertions
  "TODO/FIXME could be worth some optimization, and remove some unneeded rotations"
  [board move element]
  (map #(insert-element board move % element)
       (insertion-indices board move)))



;;; this will probably kill most of the other functions in this module…

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
