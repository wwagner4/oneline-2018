FROM openjdk:11

ENV SBT_VERSION "1.3.0"

RUN apt update
RUN apt install -y ant

WORKDIR /pgm
RUN curl -L https://github.com/sbt/sbt/releases/download/v1.3.0/sbt-1.3.0.tgz | tar -zx

COPY . /app

EXPOSE 8099

WORKDIR /app

RUN ant dist

RUN cd target/oneline

WORKDIR /app/target/oneline

CMD java -jar oneline.jar
