FROM java:8

ADD carbon-platform.jar /carbon/carbon-platform.jar
ADD lib /carbon/lib

RUN echo "deb http://ppa.launchpad.net/adiscon/v8-devel/ubuntu precise main" >> /etc/apt/source.list
RUN echo "deb-src http://ppa.launchpad.net/adiscon/v8-devel/ubuntu precise main" >> /etc/apt/source.list
RUN apt-get update
RUN apt-get -y install rsyslog

ADD rsyslog.conf /etc/rsyslog.conf
ADD 22-loggly.conf /etc/rsyslog.d/22-loggly.conf

EXPOSE 8083

ENTRYPOINT service rsyslog start && java -jar /carbon/carbon-platform.jar