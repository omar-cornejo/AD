# IPTV HLS Server 📺

Sistema de streaming de video bajo demanda (VOD) basado en HLS con interfaz estilo Reels/TikTok.

## 🚀 Inicio Rápido

### Opción 1: Docker (Recomendado)

```bash
# Construir y ejecutar
npm run docker:build
npm run docker:up

# Ver logs
npm run docker:logs

# Detener
npm run docker:down
```

El servidor estará disponible en `http://localhost:8080`

### Opción 2: Desarrollo Local

**Requisitos:**
- Node.js 18+
- FFmpeg

```bash
# Instalar dependencias
npm run setup

# Agregar videos a la carpeta /videos
# Convertir videos a HLS
npm run convert videos/mi_video.mp4 mi_canal source

# Iniciar en modo desarrollo
npm run dev
```

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:3000`

## 📁 Estructura del Proyecto

```
prac5/
├── client/              # Frontend (React + Vite)
│   ├── src/
│   │   ├── components/  # Componentes React
│   │   └── hooks/       # Custom hooks
│   └── dist/            # Build de producción
├── src/
│   ├── config/          # Configuración
│   └── routes/          # Rutas de API
├── streams/             # Streams HLS generados
├── videos/              # Videos de origen
├── server.js            # Servidor Express
├── convert-to-hls.js    # Script de conversión
└── Dockerfile           # Configuración Docker
```

## 🎬 Convertir Videos

```bash
# Sintaxis
npm run convert <video_input> <nombre_canal> [perfil]

# Perfiles disponibles:
# - source: Sin recodificar (rápido)
# - low: 360p, 500kbps
# - medium: 720p, 1500kbps  
# - high: 1080p, 3000kbps

# Ejemplos
npm run convert videos/pelicula.mp4 canal_peliculas source
npm run convert videos/serie.mp4 canal_series medium
```

## 🛠️ Comandos Útiles

```bash
# Desarrollo
npm run dev              # Servidor + Cliente en desarrollo
npm start                # Solo servidor

# Docker
npm run docker:build     # Construir imagen
npm run docker:up        # Iniciar contenedores
npm run docker:down      # Detener contenedores
npm run docker:logs      # Ver logs
npm run docker:restart   # Reiniciar (rebuild completo)

# Mantenimiento
npm run setup            # Instalar todas las dependencias
npm run clean            # Limpiar builds y node_modules
```

## �� API Endpoints

- `GET /api/channels` - Lista de canales disponibles
- `GET /api/health` - Estado del servidor
- `GET /streams/:channel/playlist.m3u8` - Playlist HLS
- `GET /streams/:channel/:segment.ts` - Segmentos de video

## 🎨 Características

- ✅ Streaming HLS con bitrate adaptativo
- ✅ Interfaz tipo Reels (scroll vertical)
- ✅ Chat en tiempo real (Socket.IO)
- ✅ Responsive (móvil y escritorio)
- ✅ Detección automática de canales
- ✅ Docker ready
- ✅ Health checks

## 🔧 Configuración

Copia `.env.example` a `.env` y ajusta las variables:

```env
NODE_ENV=production
PORT=8080
CLIENT_URL=http://localhost:3000
```

## 📚 Documentación Técnica

Consulta la [Memoria Técnica Extensa](docs/MEMORIA_TECNICA_EXTENSA.md) para detalles completos sobre:
- Arquitectura del sistema
- Diagramas de secuencia
- Implementación de HLS
- Estrategias de optimización

## 🐛 Solución de Problemas

**El video no se reproduce:**
- Verifica que el archivo `.m3u8` existe en `/streams/[canal]/`
- Revisa los logs del servidor: `npm run docker:logs`

**No aparecen los canales:**
- Asegúrate de que cada carpeta en `/streams` contiene un `playlist.m3u8`
- Reinicia el servidor

**Error al convertir:**
- Verifica que FFmpeg está instalado: `ffmpeg -version`
- Revisa que el video de origen no está corrupto

## 📄 Licencia

ISC
