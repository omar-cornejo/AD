# Servidor IPTV con HLS Streaming

Servidor IPTV que convierte videos a formato HLS y los sirve mediante una interfaz web moderna estilo Reels.

## Características

- Conversión de videos a formato HLS con múltiples perfiles de calidad
- Servidor HTTP con Express.js
- Interfaz web moderna tipo Reels/TikTok
- Chat en tiempo real con WebSocket
- Diseño responsive
- Soporte para Docker
- API REST para gestión de canales

## Estructura del Proyecto

```
prac5/
├── client/                 # Frontend React + Vite
│   ├── src/
│   │   ├── components/    # Componentes UI
│   │   ├── hooks/         # Custom hooks
│   │   └── App.jsx
│   └── package.json
├── src/
│   ├── config/            # Configuraciones del servidor
│   └── routes/            # Rutas de API
├── streams/               # Streams HLS generados
├── videos/                # Videos fuente
├── server.js             # Servidor Express
├── convert-to-hls.js     # Script de conversión
└── Dockerfile
```

## Requisitos

- Node.js 18+
- FFmpeg

## Instalación Local

```bash
# Instalar dependencias
npm install
cd client && npm install && cd ..

# Convertir videos a HLS
node convert-to-hls.js videos/tu_video.mp4 nombre_canal medium

# Iniciar servidor (desarrollo)
npm run dev

# Iniciar servidor (producción)
npm run build
npm start
```

## Docker

```bash
# Construir y ejecutar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

## API Endpoints

- `GET /api/channels` - Lista de canales disponibles
- `GET /api/health` - Health check
- `GET /streams/:channel/playlist.m3u8` - Playlist HLS del canal

## Conversión de Videos

```bash
node convert-to-hls.js <archivo> <nombre_canal> [perfil]

# Perfiles disponibles:
# - source: Sin recodificar (rápido)
# - low: 360p, 500kbps
# - medium: 720p, 1500kbps  
# - high: 1080p, 3000kbps

# Ejemplo
node convert-to-hls.js video.mp4 mi_canal medium
```

## Deploy en Render.com

Ver [docs/DEPLOY.md](./docs/DEPLOY.md) para instrucciones completas de deployment.

## Configuración

Editar `src/config/server.config.js` para configurar:
- Puerto del servidor
- Rutas de archivos
- CORS

Editar `src/config/ffmpeg.config.js` para configurar perfiles de conversión.

## Licencia

MIT
