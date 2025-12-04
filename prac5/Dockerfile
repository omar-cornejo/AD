FROM node:18-alpine AS builder

WORKDIR /app

COPY client/package*.json ./client/
WORKDIR /app/client
RUN npm install
COPY client/ ./
RUN npm run build

FROM node:18-alpine

# IMPORTANTE: Re-declarar ARGs después de FROM para que funcionen
ARG VIDEO1_ID=""
ARG VIDEO2_ID=""

RUN apk add --no-cache ffmpeg

RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001

WORKDIR /app

COPY package*.json ./
RUN npm install --production

COPY server.js ./
COPY convert-to-hls.js ./
COPY src/ ./src/

COPY --from=builder /app/client/dist ./client/dist

RUN mkdir -p streams videos

# Copiar videos locales si existen (para desarrollo local)
COPY videos/ ./videos/

# Si no hay videos MP4 y hay IDs de Drive, descargar (para Render)
# Usar ARG que Render puede pasar como build args
ARG VIDEO1_ID=""
ARG VIDEO2_ID=""

# Convertir ARG a ENV para poder usarlos en RUN
ENV VIDEO1_ID_ENV=${VIDEO1_ID}
ENV VIDEO2_ID_ENV=${VIDEO2_ID}

RUN echo "🔍 Checking VIDEO1_ID: ${VIDEO1_ID_ENV}" && \
    echo "🔍 Checking VIDEO2_ID: ${VIDEO2_ID_ENV}" && \
    VIDEO_COUNT=$(find videos/ -name "*.mp4" 2>/dev/null | wc -l); \
    echo "📊 Videos encontrados localmente: $VIDEO_COUNT"; \
    if [ "$VIDEO_COUNT" -eq 0 ] && [ -n "$VIDEO1_ID_ENV" ]; then \
        echo "📥 No hay videos locales. Descargando desde Google Drive..."; \
        apk add --no-cache python3 py3-pip; \
        pip3 install --break-system-packages gdown; \
        echo "Descargando video 1 (ID: ${VIDEO1_ID_ENV})..."; \
        gdown "https://drive.google.com/uc?id=${VIDEO1_ID_ENV}" -O videos/video1.mp4 || \
        gdown --fuzzy "https://drive.google.com/file/d/${VIDEO1_ID_ENV}/view" -O videos/video1.mp4; \
        echo "Descargando video 2 (ID: ${VIDEO2_ID_ENV})..."; \
        gdown "https://drive.google.com/uc?id=${VIDEO2_ID_ENV}" -O videos/video2.mp4 || \
        gdown --fuzzy "https://drive.google.com/file/d/${VIDEO2_ID_ENV}/view" -O videos/video2.mp4; \
        ls -lh videos/*.mp4; \
        file videos/*.mp4; \
        echo "✅ Videos descargados desde Drive"; \
    elif [ "$VIDEO_COUNT" -gt 0 ]; then \
        echo "✅ Usando videos locales"; \
    fi

# Convertir videos a HLS
RUN ls -la videos/ && \
    find videos/ -name "*.mp4" -type f | while IFS= read -r video; do \
        filename=$(basename "$video" .mp4); \
        sanitized=$(echo "$filename" | tr ' ' '_' | tr -cd '[:alnum:]_-'); \
        echo "🎬 Converting: $video -> $sanitized"; \
        node convert-to-hls.js "$video" "$sanitized" source || echo "❌ Error converting $filename"; \
    done && \
    rm -rf videos/*.mp4 && \
    ls -la streams/ && \
    chown -R nodejs:nodejs /app

USER nodejs

EXPOSE 8080

ENV NODE_ENV=production \
    PORT=8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD node -e "require('http').get('http://localhost:8080/api/health', (r) => {process.exit(r.statusCode === 200 ? 0 : 1)})"

CMD ["node", "server.js"]
