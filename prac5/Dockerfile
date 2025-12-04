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

# URLs de Dropbox (para Render) - cambiar dl=0 por dl=1 para descarga directa
ARG VIDEO1_URL="https://www.dropbox.com/scl/fi/sgcvecy6hh457drth3gbu/videoplayback.mp4?rlkey=kmi0vng1igjo1ztdjeusy95m9&st=92k108jt&dl=1"
ARG VIDEO2_URL="https://www.dropbox.com/scl/fi/enjgu4cjn3hwxkxma4thk/video2.mp4?rlkey=up61ixsexrb7c9jsfihanp78a&st=if97db9r&dl=1"

ENV VIDEO1_URL_ENV=${VIDEO1_URL}
ENV VIDEO2_URL_ENV=${VIDEO2_URL}

RUN VIDEO_COUNT=$(find videos/ -name "*.mp4" 2>/dev/null | wc -l); \
    echo "📊 Videos encontrados localmente: $VIDEO_COUNT"; \
    if [ "$VIDEO_COUNT" -eq 0 ]; then \
        echo "📥 No hay videos locales. Descargando desde Dropbox..."; \
        apk add --no-cache wget; \
        echo "Descargando videoplayback.mp4..."; \
        wget -O videos/videoplayback.mp4 "${VIDEO1_URL_ENV}"; \
        echo "Descargando video2.mp4..."; \
        wget -O videos/video2.mp4 "${VIDEO2_URL_ENV}"; \
        ls -lh videos/*.mp4; \
        echo "✅ Videos descargados desde Dropbox"; \
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
