FROM java:8

ADD target/carbon-platform.jar /carbon/carbon-platform.jar
ADD target/lib /carbon/lib

EXPOSE 8083
ENTRYPOINT [ "java", "-jar", "/carbon/carbon-platform.jar" ]