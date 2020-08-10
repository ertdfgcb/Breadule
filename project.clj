(defproject breadule "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library]]
                 [thheller/shadow-cljs "2.10.17"]
                 [reagent "0.10.0"]
                 [re-frame "1.0.0"]
                 [re-com "2.8.0"]
                 [compojure "1.6.1"]
                 [yogthos/config "1.1.7"]
                 [ring "1.8.1"]
                 [ring/ring-defaults "0.3.2"]
                 [com.novemberain/monger "3.1.0"]
                 [cljs-bach "0.3.0"]
                 [day8.re-frame/http-fx "0.2.1"]
                 [ring/ring-json "0.5.0"]]

  :plugins [[lein-shadow "0.2.0"]
            [lein-cljfmt "0.6.8"]
            [lein-shell "0.5.0"]]

  :min-lein-version "2.9.0"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]


  :shell {:commands {"open" {:windows ["cmd" "/c" "start"]
                             :macosx  "open"
                             :linux   "xdg-open"}}}

  :shadow-cljs {:nrepl {:port 8777}
                
                :builds {:app {:target :browser
                               :output-dir "resources/public/js/compiled"
                               :asset-path "/js/compiled"
                               :modules {:app {:init-fn breadule.core/init
                                               :preloads [devtools.preload]}}

                               :devtools {:http-root "resources/public"
                                          :http-port 8280
                                          :http-handler breadule.handler/dev-handler
                                          }}}}

  :aliases {"dev"          ["with-profile" "dev" "do"
                            ["shadow" "watch" "app"]]
            "prod"         ["with-profile" "prod" "do"
                            ["shadow" "release" "app"]]
            "build-report" ["with-profile" "prod" "do"
                            ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                            ["shell" "open" "target/build-report.html"]]
            "karma"        ["with-profile" "prod" "do"
                            ["shadow" "compile" "karma-test"]
                            ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "1.0.2"]]
    :source-paths ["dev"]}

   :prod {}
   
   :uberjar {:source-paths ["env/prod/clj"]
             :omit-source  true
             :main         breadule.server
             :aot          [breadule.server]
             :uberjar-name "breadule.jar"
             :prep-tasks   ["compile" ["prod"]]}}

  :prep-tasks [])
