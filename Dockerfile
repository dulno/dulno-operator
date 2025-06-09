FROM alpine

COPY /build/libs/operator-1.0.0-SNAPSHOT.jar operator.jar

USER 1000