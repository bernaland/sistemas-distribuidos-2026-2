FROM node:22-alpine AS build
WORKDIR /app
COPY frontend/tienda-frontend/package*.json ./
RUN npm ci
COPY frontend/tienda-frontend/ ./
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/tienda-frontend /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
