(ns jsx)

(require '[babashka.pods :as pods])

(System/setProperty "https.proxyHost" "localhost")
(System/setProperty "https.proxyPort" "7890")

(pods/load-pod
 'org.babashka/instaparse "0.0.4")

(require '[instaparse.core :as insta])

(def jsx-parser
  (insta/parser 
   "tag = <'<'> name <ws> attributes? <ws> <'>'>
    <attributes> = attribute (<ws> attribute)*
    attribute = name ws? <'='> ws? value
    <name> = #'[a-zA-Z0-9]+'
    <symbol> = #'[(){}=>/]+'
    <value> = (symbol | name)*
    ws = #'\\s+'
    "))

(comment
  (insta/parse jsx-parser "<div
        ref={mapRef}
        tabIndex={0}
        onBlur={() => {
          // TODO: inifite re-render loop. 
          // when surrender or game over dialog is shown. onBlur will execute, it set SelectedMapTile so a re-render is triggered. in the next render, onBlur execute again
          // setSelectedMapTileInfo({ x: -1, y: -1, half: false, unitsCount: 0 });
        }}
      >")
  :rcf)

