A "Third time" timer base on https://optima.blog/productivity/third-time-a-better-way-to-work rules

Third time is basically
- rest 1/3 of work time. e.g. if work for 1 hour, then earn 20 minutes of rest time
- work time is not fix, you can work as long as you like
- rest time is not fix either, it is a upper bound, in previus example, I can rest up to 20 minutes, if I break rest in 15 minutes, the rest of the 5 minutes can save for later use
- there is long break for launch or supper. long break can be arbitrary long, provided the time is set upfront
- saved rest time can not preserve after long break, or preserve overnight

## use this script

start working timer
```sh
tt -e work
```

start resting timer
```sh
tt -e rest
# {:expire 2024-05-25 19:00:00, :time 13.0}
```
will print
- time: rest time(minutes) upper bound
- expire: will expire in this time


## install this script

locally 
```sh
cd ./time/src
bbin install time.clj --as tt
```

http
```sh
bbin install https://raw.githubusercontent.com/schneiderlin/babashka-scripts/master/time/src/time.clj --as tt
```