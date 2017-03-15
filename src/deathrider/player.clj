(ns deathrider.player
  (:use deathrider.point))

(defrecord Player
  [id status track])

(defn player-id [p]
  (:id p))

(defn alive? [player] 
  (= (:status player) :alive))

(declare ride)
(defn new-player [id pos dir]
  (ride (->Player id :alive (list pos)) dir))

(defn- heading [player]
  (let [head (first (:track player))
        neck (second (:track player))]
    (cond
      (nil? neck) nil

      (== (point-x head)
          (point-x neck))
      (if (> (point-y head)
             (point-y neck))
        :up
        :down)

      true
      (if (> (point-x head)
             (point-x neck))
        :right
        :left))))

(defn ride
  ([player] (ride player (heading player)))
  ([player dir]
   (update player :track
     (fn [[head & tail]]
       (if (or (nil? dir)
               (= dir (heading player)))
         (conj tail (adjacent head (heading player)))
         (conj tail head (adjacent head dir)))))))

(defn die [player]
  (assoc player :status :dead))

(defn hitting? [player other]
  (if (not= player other)
    (let [track (:track other)
          head (first (:track player))]
      (some (fn [[a b]] (between? head a b))
            (map vector track (rest track))))))
