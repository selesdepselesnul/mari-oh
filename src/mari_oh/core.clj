(ns mari-oh.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn get-walk-image [walk-pos]
  (q/load-image (str "Walk (" walk-pos ").png")))

(defn get-dead-image [dead-pos]
  (q/load-image (str "Dead (" dead-pos ").png")))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:character {:walk-images (map get-walk-image (range 1 11)) 
               :walk-post 1
               :is-walking? true
               :dead-images (map get-dead-image (range 2 13))
               :dead-post 1
               :x 20}})

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  state)

(defn draw-state [state]
  (let [character (:character state)]
    (q/image (q/load-image "BG.png") 0 0)
    (if (:is-walking? character)
      (q/image (get-walk-image (:walk-post character)) (:x character) 500 100 100)
      (q/image (get-dead-image (:dead-post character)) (:x character) 500 100 100))
     state))

(q/defsketch mari-oh
  :title "You spin my circle right round"
  :size [800 600]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode]
  :key-pressed (fn [state {:keys [key key-code]}]
                 (case key
                   :right
                   (->
                    (update-in state
                              [:character :walk-post]
                              (fn [x] (if (= x 10) 1 (+ x 1))))
                    (update-in [:character :x] (fn [x] (+ x 0.5))))
                   :left                                        
                   (->
                    (update-in state
                              [:character :walk-post]
                              (fn [x] (if (= x 1) 10 (- x 1))))
                    (update-in [:character :x] (fn [x] (- x 0.5))))
                   :up                                        
                   (->
                    (update-in state
                              [:character :dead-post]
                              (fn [x] (if (= x 2) 12 (- x 1))))
                    (update-in [:character :is-walking?] (fn [_] false)))
                   :down                                        
                   (->
                    (update-in state
                              [:character :dead-post]
                              (fn [x] (if (= x 12) 2 (+ x 1))))
                    (update-in [:character :is-walking?] (fn [_] false)))
                   state)))
