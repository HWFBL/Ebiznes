FROM ubuntu

RUN apt-get update && apt-get install -y \
    openjdk-8-jdk \
    openjdk-8-jre

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 \
    JRE_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre

RUN apt-get update && apt-get install -y wget && \
    wget www.scala-lang.org/files/archive/scala-2.12.8.deb && \
    dpkg -i scala-2.12.8.deb

ARG SBT_VERSION=1.3.8

RUN \
  apt-get update && apt-get install -y curl && \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install -y sbt

RUN apt-get install -y \
    vim \
    unzip \
    git 

RUN apt-get install -y npm && npm install -g npm@6.8.0 

EXPOSE 8000 9000 5000 8888

VOLUME /home/rus_1137858/projekt
