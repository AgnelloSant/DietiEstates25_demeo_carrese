# Stage 1: Build
FROM node:22-alpine AS build-stage
WORKDIR /app

# Disabilita Cypress per velocità
ENV CYPRESS_INSTALL_BINARY=0

COPY package*.json ./
# Usiamo 'ci' invece di 'install' per una build pulita e deterministica
RUN npm ci 

# Copiamo TUTTO il resto (inclusi i cambiamenti al titolo e al codice)
COPY . .

# Forziamo Vite a svuotare la cartella dist prima di buildare
RUN npx vite build --emptyOutDir

# Stage 2: Production
FROM nginx:stable-alpine AS production-stage
# Pulizia totale della cartella di destinazione
RUN rm -rf /usr/share/nginx/html/*

# Copia i file dalla build
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]