# --- Stage 1: Build ---
FROM node:22-alpine AS build-stage
WORKDIR /app

# Installiamo le dipendenze separatamente per sfruttare la cache di Docker
COPY package*.json ./
RUN npm install

# Copiamo i sorgenti e buildiamo
COPY . .
RUN npx vite build

# --- Stage 2: Production (Nginx) ---
FROM nginx:stable-alpine AS production-stage

# Pulizia della cartella di default di Nginx
RUN rm -rf /usr/share/nginx/html/*

# COPIA CORRETTA: Vite mette i file in /app/dist, non in /app/frontend/dist
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Copiamo la tua configurazione di Nginx
# Usiamo la cartella conf.d per sovrascrivere quella di default direttamente
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

# Non usiamo template/envsubst se il file nginx.conf ha già i resolver e gli host statici
# Se invece vuoi usare le variabili d'ambiente, mantieni il percorso /etc/nginx/templates/
# Ma per ora, la configurazione che abbiamo scritto prima è più sicura così:
CMD ["nginx", "-g", "daemon off;"]