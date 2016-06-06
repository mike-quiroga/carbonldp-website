FROM java:8

COPY target/carbon-platform.jar /opt/carbon/carbon-platform.jar
COPY target/lib /opt/carbon/lib
COPY config.properties /opt/carbon/config/config.properties

COPY datadog.conf /etc/dd-agent/datadog.conf
RUN apt-get update && apt-get install -y \
        apt-transport-https
RUN sh -c "echo 'deb https://apt.datadoghq.com/ stable main' > /etc/apt/sources.list.d/datadog.list"
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C7A7DA52
RUN apt-get update && apt-get install -y datadog-agent

EXPOSE 8083

ENTRYPOINT /etc/init.d/datadog-agent start && java -jar /opt/carbon/carbon-platform.jar