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

RUN mkdir -p streams

COPY videos/ ./videos/

RUN ls -la videos/ && \
    find videos/ -name "*.mp4" -type f | while IFS= read -r video; do \
        filename=$(basename "$video" .mp4); \
        sanitized=$(echo "$filename" | tr ' ' '_' | tr -cd '[:alnum:]_-'); \
        echo "Converting: $video -> $sanitized"; \
        node convert-to-hls.js "$video" "$sanitized" source || echo "Error converting $filename"; \
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
