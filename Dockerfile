FROM carbonldp/jspm-typescript

COPY server /usr/share/server
COPY dist/site /usr/share/server/html

WORKDIR /usr/share/server

EXPOSE 80

CMD jspm run server --port 80 --root /usr/share/server/html --route-table route-table.json