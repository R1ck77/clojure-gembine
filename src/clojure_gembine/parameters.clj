(ns clojure-gembine.parameters
  (require [clojure-gembine.utils :refer [template-as-file]]))

(def gembine-banner [811 202 1105 276])

(def checkboard-coordinates [758 420 1163 825])

(def message-area [1330 550 1490 700])


(defonce gembine-title-file (template-as-file "gembine-title.png"))

(defonce game-over-file (template-as-file "game-over.png"))
(defonce no-message-file (template-as-file "no-message.png"))


(defonce void-board-file (template-as-file "void-board.png"))

;;; tiles (in order of 
(defonce red-bead-file (template-as-file "red-bead.png"))
(defonce red-ball-file (template-as-file "red-ball.png"))
(defonce red-triangle-file (template-as-file "red-triangle.png"))
(defonce red-square-file (template-as-file "red-square.png"))
(defonce red-pentagon-file (template-as-file "red-pentagon.png"))
(defonce green-bead-file (template-as-file "green-bead.png"))
(defonce green-ball-file (template-as-file "green-ball.png"))
(defonce green-triangle-file (template-as-file "green-triangle.png"))
(defonce green-pentagon-file (template-as-file "green-pentagon.png"))

