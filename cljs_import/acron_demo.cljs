(ns acron-demo
    (:require 
     ["acorn" :as acorn]
     ["acorn-jsx" :as jsx]))

(def parser (.extend (.-Parser acorn)
         ((.-default jsx))))

(def ast (.parse parser "my(<jsx/>, 'code');"))

(js->clj (.parse js/JSON (.stringify js/JSON ast)))


(.keys js/Object (first (.-body ast)))

(println (js->clj ast))

(js->clj #js {:foo "bar"})
(js->clj (js/Object.))

;;  jsx-to-clojurescript $(Get-Clipboard)