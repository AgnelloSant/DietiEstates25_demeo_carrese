# --- Stage 1: Build ---
FROM node:22-alpine AS build-stage
WORKDIR /app
ENV CYPRESS_INSTALL_BINARY=0
COPY package*.json ./
RUN npm install
COPY . .
# --- FORCE RELATIVE PATHS ---
# Sovrascriviamo le variabili per essere sicuri che la build usi paths relativi
# e ignori qualsiasi .env.production che potrebbe esserci
ENV VITE_API_PROPERTY_URL="/properties"
ENV VITE_API_USER_URL="/user"

RUN npx vite build

# COMANDO DIAGNOSTICO: Vedremo questo log nelle GitHub Actions!
RUN echo "CONTENUTO DELLA CARTELLA DIST:" && ls -R /app/dist

# --- Stage 1.5: Dev ---
FROM node:22-alpine AS dev-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 5173
CMD ["npm", "run", "dev", "--", "--host"]

# --- Stage 2: Production ---
FROM nginx:stable-alpine

# Rimuoviamo la configurazione di default per evitare conflitti
RUN rm /etc/nginx/conf.d/default.conf

# Copiamo la tua configurazione in una posizione che Nginx DEVE leggere
COPY nginx.conf /etc/nginx/conf.d/frontend.conf

# Puliamo e copiamo i file
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Permessi: Assicuriamoci che Nginx possa leggere i file
RUN chmod -R 755 /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]