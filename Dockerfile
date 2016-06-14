FROM java:8

COPY target/carbon-platform.jar /opt/carbon/carbon-platform.jar
COPY target/lib /opt/carbon/lib

EXPOSE 8083

ENTRYPOINT java -jar /opt/carbon/carbon-platform.jar