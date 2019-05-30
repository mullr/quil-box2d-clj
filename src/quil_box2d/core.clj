(ns quil-box2d.core
  (:require
   [quil.core :as q]
   [quil.middleware :as m])
  (:import
   (org.jbox2d.common Vec2)
   (org.jbox2d.dynamics Body BodyDef BodyType World)
   (org.jbox2d.collision.shapes EdgeShape PolygonShape)))

(defn vec2 [a b]
  (Vec2. a b))

(defn create-body [world shape]
  (let [bd (BodyDef.)]
    (set! (.type bd) BodyType/DYNAMIC)
    (set! (.position bd) (vec2 0 0))
    (doto (.createBody world bd)
      (.createFixture shape 5))))

(defn read-body [body]
  (let [pos (.getPosition body)]
    {:x (.x pos)
     :y (.y pos)
     :angle (.getAngle body)}))

(defn reset-bodies [bodies]
  (doseq [b bodies]
    (doto b
      (.setTransform (vec2 (* 25 (- (rand) 0.5))
                           (+ 2 (* (rand) 3)))
                     0)
      (.setLinearVelocity (vec2 0 0))
      (.setAwake true)
      (.setActive true)))
  bodies)

(defn setup []
  (q/frame-rate 30)
  (let [gravity (vec2 0 -10)
        world (World. gravity)
        bd-ground (BodyDef.)
        shape0 (doto (EdgeShape.)
                 (.set (vec2 -40 -25) (vec2 40 -25)))
        ground (doto (.createBody world bd-ground)
                 (.createFixture shape0 0))
        shape (doto (PolygonShape.)
                (.setAsBox 1 1))]
    {:world world
     :bodies (reset-bodies (repeatedly 10 #(create-body world shape)))}))

(defn update-state [state]
  (.step (:world state) 0.1 2 2)
  state)

(defn draw-state [state]
  (q/background 240)
  (q/translate [(/ (q/width) 2)
                0])
  (q/scale 20)
  (q/rotate q/PI)
  (doseq [{:keys [x y angle]} (map read-body (:bodies state))]
    (q/with-translation [x y]
      (q/with-rotation [angle]
        (q/rect 0 0 1 1)))))

(defn -main []
  (q/sketch
   :size [500 500]      ; setup function called only once, during sketch
                        ; initialization.
   :setup setup

   :update update-state ; update-state is called on each iteration before
                        ; draw-state.

   :draw draw-state     ; This sketch uses functional-mode middleware. Check
                        ; quil wiki for more info about middlewares and
                        ; particularly fun-mode.

   :middleware [m/fun-mode]))
