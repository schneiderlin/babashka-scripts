scripts to open vscode project and config the workspace

## use this script
```bash
vsc
```

first time you should only see one option "configuration".
it will open the configuration file of this cli.
populate with other project in the same manner.
for example
```edn
[{:name "vscode tui"
  :location "C:/Users/zihao/Desktop/workspace/babashka-scripts/vsc_tui"}
 {:name "babashka scripts"
  :location "C:/Users/zihao/Desktop/workspace/babashka-scripts"}
 {:name "third time tracker"
  :location "C:/Users/zihao/Desktop/workspace/babashka-scripts/time"}
 {:name "clean logseq assets"
  :location "C:/Users/zihao/Desktop/workspace/babashka-scripts/logseq"}
 {:name "configuration"
  :location "C:/Users/zihao/.local/bin/vsc_tui.edn"}]
```

save configuration file and `vsc` again.

## install this script
locally 
```sh
cd ./vsc_tui
bbin install . --as vsc
```