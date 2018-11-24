(ns fastlane.core
  (:require
   [clojure.java.io :as io]
   [cognitect.transit :as transit]
   [nrepl.core :as nrepl]
   [nrepl.transport :refer [fn-transport]])
  (:import
   [java.io EOFException PushbackInputStream]
   [java.net Socket SocketException]))

(defmacro ^{:private true} rethrow-on-disconnection
  [^Socket s & body]
  `(try
     ~@body
     (catch EOFException e#
       (throw (SocketException. "The transport's socket appears to have lost its connection to the nREPL server")))
     (catch Throwable e#
       (if (and ~s (not (.isConnected ~s)))
         (throw (SocketException. "The transport's socket appears to have lost its connection to the nREPL server"))
         (throw e#)))))

(defn build-transit-transport-using-type
  "Returns a functions with a Transport implementation that serializes
  messages over the given Socket or InputStream/OutputStream with Transit
  using `transit-type`."
  [transit-type]
  (fn transit-fn
    ([^Socket s] (transit-fn s s s))
    ([in out & [^Socket s]]
     (let [in (PushbackInputStream. (io/input-stream in))
           out (io/output-stream out)
           reader (transit/reader in transit-type)
           writer (transit/writer out transit-type)]
       (fn-transport
        #(let [payload (rethrow-on-disconnection s (try
                                                     (transit/read reader)
                                                     (catch RuntimeException e
                                                       (throw (.getCause e)))))]
           (cond-> payload
             (get payload "op") (update "op" name)
             (get payload "status") (update "status"
                                            (fn [status]
                                              (if (coll? status)
                                                (map name status)
                                                (name status))))))
        #(rethrow-on-disconnection s
                                   (locking out
                                     (try
                                       (transit/write writer %)
                                       (.flush out)
                                       (catch RuntimeException e
                                         (throw (.getCause e))))))
        (fn []
          (if s
            (.close s)
            (do
              (.close in)
              (.close out)))))))))

(def transit+msgpack
  "Returns a Transport implementation that serializes messages
  over the given Socket or InputStream/OutputStream with Transit
  using msgpack."
  (build-transit-transport-using-type :msgpack))

(def transit+json
  "Returns a Transport implementation that serializes messages
  over the given Socket or InputStream/OutputStream with Transit
  using json."
  (build-transit-transport-using-type :json))

(def transit+json-verbose
  "Returns a Transport implementation that serializes messages
  over the given Socket or InputStream/OutputStream with Transit
  using json-verbose."
  (build-transit-transport-using-type :json-verbose))

(defn- ^java.net.URI to-uri
  [x]
  {:post [(instance? java.net.URI %)]}
  (if (string? x)
    (java.net.URI. x)
    x))

(defn- socket-info
  [x]
  (let [uri (to-uri x)
        port (.getPort uri)]
    (merge {:host (.getHost uri)}
           (when (pos? port)
             {:port port}))))

(defmethod nrepl/url-connect "transit+msgpack"
  [uri]
  (apply nrepl/connect (mapcat identity
                               (merge {:transport-fn transit+msgpack
                                       :port 7888}
                                      (socket-info uri)))))

(defmethod nrepl/url-connect "transit+json"
  [uri]
  (apply nrepl/connect (mapcat identity
                               (merge {:transport-fn transit+json
                                       :port 7888}
                                      (socket-info uri)))))

(defmethod nrepl/url-connect "transit+json-verbose"
  [uri]
  (apply nrepl/connect (mapcat identity
                               (merge {:transport-fn transit+json-verbose
                                       :port 7888}
                                      (socket-info uri)))))
