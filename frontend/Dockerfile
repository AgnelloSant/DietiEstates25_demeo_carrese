# Stage 1: Build
FROM node:22-alpine AS build-stage
WORKDIR /app

# Saltiamo cypress per velocità
ENV CYPRESS_INSTALL_BINARY=0 

COPY package*.json ./
# Installazione pulita
RUN npm ci 

# Copiamo i file
COPY . .

# --- IL TRUCCO ---
# Forziamo Vite a generare nomi diversi e pulire la cartella
RUN npx vite build --emptyOutDir

# Stage 2: Production
FROM nginx:stable-alpine
# Pulizia maniacale
RUN rm -rf /usr/share/nginx/html/*

# Copia i file dalla build appena fatta
COPY --from=build-stage /app/frontend/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]