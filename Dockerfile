FROM nginx:latest
COPY nginx/nginx.conf /etc/nginx/nginx.conf
COPY certs /etc/nginx/certs
EXPOSE 8443