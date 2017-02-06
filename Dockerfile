FROM carbonldp/jspm-typescript

COPY public /usr/share/server/html

EXPOSE 80

ENTRYPOINT jspm run server --port 80 --root /usr/share/server/html --route-table route-table.json