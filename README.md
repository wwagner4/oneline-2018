# oneline-2018
for details see: http://entelijan.net/oneline

# Docker

clone
```
git clone https://github.com/wwagner4/oneline-2018.git
```

build
```
docker build -t oneline .
```

run
```
docker rm -f oneline

docker run --rm -i -t -p 8887:8099 oneline
docker run --name oneline -p 8887:8099 oneline &
```
url
```
http://localhost:8887/index
http://37.252.189.71:8887/index
```

