FROM node:18-alpine AS builder

WORKDIR /app

COPY client/package*.json ./client/
WORKDIR /app/client
RUN npm install
COPY client/ ./
RUN npm run build

FROM node:18-alpine

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

RUN mkdir -p streams videos uploads

COPY videos/ ./videos/

ARG DEMO_VIDEO_URL="https://www.dropbox.com/scl/fi/enjgu4cjn3hwxkxma4thk/video2.mp4?rlkey=up61ixsexrb7c9jsfihanp78a&st=if97db9r&dl=1"
ENV DEMO_VIDEO_URL_ENV=${DEMO_VIDEO_URL}

RUN VIDEO_COUNT=$(find videos/ -name "*.mp4" 2>/dev/null | wc -l); \
    echo "Videos encontrados localmente: $VIDEO_COUNT"; \
    if [ "$VIDEO_COUNT" -eq 0 ]; then \
        echo "No se han encontrado videos locales. Descargando video demo desde Dropbox..."; \
        apk add --no-cache wget; \
        wget -O videos/demo.mp4 "${DEMO_VIDEO_URL_ENV}"; \
        echo "Video demo descargado ($(du -h videos/demo.mp4 | cut -f1))"; \
    fi && \
    echo "Convirtiendo videos a HLS..."; \
    find videos/ -name "*.mp4" -type f | while IFS= read -r video; do \
        filename=$(basename "$video" .mp4); \
        sanitized=$(echo "$filename" | tr ' ' '_' | tr -cd '[:alnum:]_-'); \
        echo "Convirtiendo: $video -> $sanitized"; \
        node convert-to-hls.js "$video" "$sanitized" source || echo "Error convirtiendo $filename"; \
    done && \
    rm -rf videos/*.mp4 && \
    echo "Conversión completada. Streams disponibles:"; \
    ls -la streams/ && \
    chown -R nodejs:nodejs /app

USER nodejs

EXPOSE 8080

ENV NODE_ENV=production \
    PORT=8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD node -e "require('http').get('http://localhost:8080/api/health', (r) => {process.exit(r.statusCode === 200 ? 0 : 1)})"

CMD ["node", "server.js"]
