# Stage 1: Build
FROM node:22-alpine AS build-stage
WORKDIR /app
# Saltiamo cypress per velocizzare la GitHub Action
ENV CYPRESS_INSTALL_BINARY=0 
COPY package*.json ./
RUN npm install
COPY . .
RUN npx vite build

# Stage 2: Production
FROM nginx:stable-alpine AS production-stage
# PULIZIA FORZATA
RUN rm -rf /usr/share/nginx/html/*
# Copia il contenuto della dist (assicurati sia /app/dist)
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]