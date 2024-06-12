(ns cljs-import)

(require '[babashka.pods :as pods])

(System/setProperty "https.proxyHost" "localhost")
(System/setProperty "https.proxyPort" "7890")

(pods/load-pod
 'org.babashka/instaparse "0.0.4")

(require '[instaparse.core :as insta])

(def js-import-parser
  (insta/parser
   "<imports> = import <whitespace?> imports?
    import = <'import'> <whitespace?> 
      (defaults | brackets) <whitespace?> 
      from
    brackets = <'{'> 
     <whitespace?> inners? <whitespace?> 
      <'}'> 
    defaults = <whitespace?> inners? <whitespace?> 
    <inners> = inner (<','> <whitespace?> inners)*
    <inner> = #'[a-zA-Z0-9/@.-]+'
    from = <'from'> <whitespace?> <'\\''> inner <whitespace?> <'\\''> <';'?>
    whitespace = #'\\s+'"))

(comment 
  (insta/parse js-import-parser "import {a,b} from 'a';
                               import {c} from 'd';")
  (insta/parse js-import-parser "import a,b from 'a'") 
  
  (insta/parse js-import-parser "import ArrowBackIcon from '@mui/icons-material/ArrowBack';")

  (insta/parse js-import-parser "import { getPlayerIndex } from '@/lib/utils';
import { ZoomInMap, ZoomOutMap } from '@mui/icons-material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ClearIcon from '@mui/icons-material/Clear';
import HomeIcon from '@mui/icons-material/Home';
import UndoIcon from '@mui/icons-material/Undo';
import { Box, IconButton, Tooltip, Typography } from '@mui/material';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTranslation } from 'next-i18next';
import { MutableRefObject, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import MapTile from './MapTile';")
  :rcf)

(defn output->cljs [imports]
  (->> imports
       (map (fn [import_]
              (let [content (rest import_)
                    from (second (second content)) 
                    is-default? (= :defaults (ffirst content)) 
                    names (rest (first content))]
                (str "[" "\"" from "\"" 
                     (if is-default?
                       (str " :default " (clojure.string/join " " names))
                       (str " :refer " "[" (clojure.string/join " " names) "]"))
                     "]"))))
       (clojure.string/join "\n")))

(comment
  (println (-> "import { useGame, useGameDispatch } from '@/context/GameContext';
import useMap from '@/hooks/useMap';
import { Position, SelectedMapTileInfo, TileProp, TileType } from '@/lib/types';
import usePossibleNextMapPositions from '@/lib/use-possible-next-map-positions';
import { getPlayerIndex } from '@/lib/utils';
import { ZoomInMap, ZoomOutMap } from '@mui/icons-material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ClearIcon from '@mui/icons-material/Clear';
import HomeIcon from '@mui/icons-material/Home';
import UndoIcon from '@mui/icons-material/Undo';
import { Box, IconButton, Tooltip, Typography } from '@mui/material';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTranslation } from 'next-i18next';
import { MutableRefObject, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import MapTile from './MapTile';"
      js-import-parser
      output->cljs)) 
  :rcf)

