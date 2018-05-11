(ns clojure-automate.core
  (:import [java.awt Robot Rectangle Toolkit]
           [java.awt.event KeyEvent]
           [javax.imageio ImageIO])
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

(defn- screen-size []
  (let [dimension (.getScreenSize (Toolkit/getDefaultToolkit))]
    [(.width dimension) (.height dimension)]))

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


(defn test-random-moves [delay n]
  (sleep delay)
  (let [robot (new-robot)
        moves (keys arrows)]
    (repeatedly n #(move robot
                         (rand-nth moves)))))

(defn take-screenshot [robot]
  
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
