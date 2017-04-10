(ns deathrider.message
  (:use [clojure.core.async :only [>! <!]]
        [deathrider gameboard player])
  (:require [taoensso.nippy :as nippy])
  (:import [java.io InputStream
                    DataInputStream
                    OutputStream
                    DataOutputStream
                    IOException]
           [java.net Socket]))

(defn- new-usercmd [id ty data]
  {:player-id id :type ty :data data})

(defn usercmd-player-id [e] (:player-id e))

(defn usercmd-type [e] (:type e))

(defn new-turn-usercmd
  [id dir]
  (new-usercmd id :turn dir))

(defn turn-dir [e]
  (assert (= :turn (:type e)))
  (:data e))

(defn new-quit-usercmd [id]
  (new-usercmd id :quit nil))

(defn new-snapshot [players]
  {:players players})

(defn snapshot-players [s]
  (:players s))

(defn get-data-output-stream [^Socket s]
  (DataOutputStream. (.getOutputStream s)))

(defn get-data-input-stream [^Socket s]
  (DataInputStream. (.getInputStream s)))

(defn flush-os! [^OutputStream os]
  (.flush os))

(defn close-socket! [^Socket s]
  (.close s))

(defn read-usercmd! [is id]
  (try
    (nippy/thaw-from-in! is)
    (catch Throwable e
      (.printStackTrace e)
      (new-quit-usercmd id))))

