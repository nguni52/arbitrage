#!/usr/bin/env bash
if [ -z "$1" ]
then
    ./gradlew build && java -jar build/libs/arbitrage-0.1.1.jar
else
    echo $1
    ./gradlew build && java -jar build/libs/arbitrage-0.1.1.jar $1
fi
