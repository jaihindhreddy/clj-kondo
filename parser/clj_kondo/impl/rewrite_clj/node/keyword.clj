(ns ^{:no-doc true} clj-kondo.impl.rewrite-clj.node.keyword
  (:require [clj-kondo.impl.rewrite-clj.node.protocols :as node]))

;; ## Node

(defrecord KeywordNode [k namespaced?]
  node/Node
  (tag [_] :token)
  (printable-only? [_] false)
  (sexpr [_]
    (if (and namespaced?
             (not (namespace k)))
      (keyword
        (name (ns-name *ns*))
        (name k))
      k))
  (length [this]
    (let [c (inc (count (name k)))]
      (if namespaced?
        (inc c)
        (if-let [nspace (namespace k)]
          (+ 1 c (count nspace))
          c))))
  (string [_]
    (str (when namespaced? ":")
         (pr-str k)))

  Object
  (toString [this]
    (node/string this)))

(node/make-printable! KeywordNode)

;; ## Constructor

(defn keyword-node
  "Create node representing a keyword. If `namespaced?` is given as `true`
   a keyword à la `::x` or `::ns/x` (i.e. namespaced/aliased) is generated."
  [k & [namespaced?]]
  {:pre [(keyword? k)]}
  (->KeywordNode k namespaced?))
