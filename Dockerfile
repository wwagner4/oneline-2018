FROM openjdk:11

ENV SBT_VERSION "1.3.0"

RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

RUN apt install -y ant

COPY . /app

EXPOSE 8099

WORKDIR /app

RUN ant dist

RUN cd target/oneline

WORKDIR /app/target/oneline

CMD java -jar oneline.jar
