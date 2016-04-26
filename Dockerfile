FROM java:8

ADD carbon-platform.jar /carbon/carbon-platform.jar
ADD lib /carbon/lib

EXPOSE 8083

ENTRYPOINT java -jar /carbon/carbon-platform.jar