FROM node:20-alpine AS build
WORKDIR /app
COPY frontend/tienda-frontend/package*.json ./
RUN npm ci
COPY frontend/tienda-frontend/ ./
RUN npm run build

FROM nginx:1.27-alpine
COPY frontend/tienda-frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/tienda-frontend/ /usr/share/nginx/html/
