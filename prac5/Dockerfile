FROM node:18-alpine AS builder

# Instalar dependencias de build
WORKDIR /app

# Copiar y construir el frontend
COPY client/package*.json ./client/
WORKDIR /app/client
RUN npm install
COPY client/ ./
RUN npm run build

# Etapa final
FROM node:18-alpine

# Instalar FFmpeg
RUN apk add --no-cache ffmpeg

# Crear usuario no-root
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001

WORKDIR /app

# Copiar dependencias del servidor
COPY package*.json ./
RUN npm install --production

# Copiar código fuente
COPY server.js ./
COPY convert-to-hls.js ./
COPY src/ ./src/

# Copiar build del frontend desde builder
COPY --from=builder /app/client/dist ./client/dist

# Crear directorios necesarios
RUN mkdir -p streams videos

# Descargar videos de ejemplo y convertir a HLS
RUN apk add --no-cache curl && \
    echo "📥 Descargando videos de ejemplo..." && \
    curl -L -o videos/demo1.mp4 "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4" && \
    echo "🔄 Convirtiendo canal 1..." && \
    node convert-to-hls.js videos/demo1.mp4 canal_1 source && \
    echo "📥 Descargando segundo video..." && \
    curl -L -o videos/demo2.mp4 "https://test-videos.co.uk/vids/jellyfish/mp4/h264/360/Jellyfish_360_10s_1MB.mp4" && \
    echo "🔄 Convirtiendo canal 2..." && \
    node convert-to-hls.js videos/demo2.mp4 canal_2 source && \
    echo "🧹 Limpiando..." && \
    rm -rf videos/*.mp4 && \
    chown -R nodejs:nodejs /app && \
    apk del curl && \
    echo "✅ Canales de ejemplo listos"

# Cambiar a usuario no-root
USER nodejs

# Exponer puerto
EXPOSE 8080

# Variables de entorno
ENV NODE_ENV=production \
    PORT=8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD node -e "require('http').get('http://localhost:8080/api/health', (r) => {process.exit(r.statusCode === 200 ? 0 : 1)})"

# Comando de inicio
CMD ["node", "server.js"]
