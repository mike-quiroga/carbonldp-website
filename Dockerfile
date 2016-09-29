FROM nginx

COPY dist/site /usr/share/nginx/html
COPY dist/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80