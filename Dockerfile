FROM nginx:1.11.9-alpine

COPY server/default.conf /etc/nginx/conf.d/default.conf

COPY public /usr/share/nginx/html