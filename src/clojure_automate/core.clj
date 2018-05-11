(ns clojure-automate.core
  (:import [java.awt Robot]
          [java.awt.event KeyEvent])
  (:gen-class))

(def arrows {
             :up KeyEvent/VK_UP
             :down KeyEvent/VK_DOWN
             :left KeyEvent/VK_LEFT
             :right KeyEvent/VK_RIGHT
             })

(defn tap-key
  "Do a quick tap on the keyboard"
  ([code] (tap-key (Robot.) code))
  ([robot code]
   (doto robot
     (.keyPress code)
     (.keyRelease code))))

(defn tap-character
  ([char] (tap-character (Robot.) char))
  ([robot char]
   (tap-key robot
            (KeyEvent/getExtendedKeyCodeForChar (int char)))))

(defn tap-arrow
  ([arrow] (tap-arrow (Robot.) arrow))
  ([robot arrow]
   (tap-character robot (get arrows arrow))))

(defn move-at-random []
  )



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
