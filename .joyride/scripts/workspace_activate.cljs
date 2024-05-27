(ns workspace-activate
  (:require ["vscode" :as vscode]
            [joyride.core :as joyride]
            [promesa.core :as p]))


;;; REPL practice
(comment 
  ;; 选择弹窗
  (p/let [choice (vscode/window.showInformationMessage "Be a Joyrider 🎸" 
                                                       "Yes"
                                                       "Of course!")]
    (if choice
      (.appendLine (joyride/output-channel)
                   (str "You choose: " choice " 🎉"))
      (.appendLine (joyride/output-channel)
                   "You just closed it? 😭")))
  
  ;; output channel
  (doto (joyride/output-channel)
    (.show true) ;; specifically this line. It shows the channel.
    (.appendLine "Welcome Joyrider! This is your User activation script speaking.")
    (.appendLine "Tired of this message popping up? It's the script doing it. Edit it away!")
    (.appendLine "Hint: There is a command: **Open User Script...**"))
  :rcf)

;;; user_activate.cljs skeleton

;; Keep tally on VS Code disposables we register
(defonce !db (atom {:disposables []}))

(comment
  (js-keys (second (:disposables @!db)))
  :rcf)

;; To make the activation script re-runnable we dispose of
;; event handlers and such that we might have registered
;; in previous runs.
(defn- clear-disposables! []
  (run! (fn [disposable]
          (.dispose disposable))
        (:disposables @!db))
  (swap! !db assoc :disposables []))

;; Pushing the disposables on the extension context's
;; subscriptions will make VS Code dispose of them when the
;; Joyride extension is deactivated, or when you rerun
;; `my-main` in this ns (such as rerunning this script).
(defn- push-disposable! [disposable]
  (swap! !db update :disposables conj disposable)
  (-> (joyride/extension-context)
      .-subscriptions
      (.push disposable)))

(defn- my-main []
  (println "Hello World, from my-main in user_activate.cljs script")
  (clear-disposables!))

(when (= (joyride/invoked-script) joyride/*file*)
  (my-main))

;; 创建一个 ssh 的 terminal
(comment
  (def terminal (vscode/window.createTerminal
   #js {:name "ssh"
        :cwd "."}))
  
  (terminal.show true)
  (terminal.sendText "echo haha")
  :rcf)

;; can I use babashka here?
(comment
  (require '[babashka.fs :as fs])
  :rcf)

