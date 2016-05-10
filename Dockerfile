FROM java:8

COPY carbon-platform.jar /carbon/carbon-platform.jar
COPY lib /carbon/lib

COPY datadog.conf /etc/dd-agent/datadog.conf
RUN apt-get update && apt-get install -y \
        apt-transport-https
RUN sh -c "echo 'deb https://apt.datadoghq.com/ stable main' > /etc/apt/sources.list.d/datadog.list"
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C7A7DA52
RUN apt-get update && apt-get install -y \
        datadog-agent

EXPOSE 8083

ENTRYPOINT /etc/init.d/datadog-agent start && java -jar /carbon/carbon-platform.jar