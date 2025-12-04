# IPTV HLS Server 📺

Sistema de streaming de video bajo demanda (VOD) basado en HLS con interfaz estilo Reels/TikTok.

## 📹 Gestión de Videos

**Los videos NO están en el repositorio** (son demasiado grandes para GitHub).

### Opción 1: Subir videos desde la interfaz (Nuevo! 📤)

1. Accede a `http://localhost:8080/upload`
2. Arrastra y suelta tu video o selecciónalo
3. Dale un nombre al canal
4. El sistema automáticamente:
   - ✅ Convertirá el video a HLS
   - ✅ Lo guardará localmente (desarrollo) o en Dropbox (producción)
   - ✅ Estará disponible inmediatamente en el streaming

📖 **[Guía completa de configuración de uploads](docs/UPLOAD_GUIDE.md)** ← Cómo obtener el token de Dropbox

### Opción 2: Para Producción (Render/Docker Hub):
1. Sube tus videos a **Dropbox**
2. Obtén el link de descarga directa (termina en `dl=1`)
3. Configura en Render:
   - Variable `DROPBOX_ACCESS_TOKEN` = Token de API de Dropbox (para uploads desde la interfaz)

### Opción 3: Para Desarrollo Local:
1. Coloca tus videos `.mp4` en la carpeta `videos/`
2. Docker los convertirá automáticamente a HLS

📖 Ver `videos/UPLOAD_INSTRUCTIONS.md` para detalles

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

**Acceso:**
- 🖥️ Local: `http://localhost:8080`
- 🌐 LAN: `http://TU_IP_LOCAL:8080` (ej: `http://192.168.1.100:8080`)

El servidor está configurado para ser accesible desde toda tu red local. Otros dispositivos en tu LAN pueden acceder usando tu IP local.

**Para encontrar tu IP local:**
```bash
# Linux/Mac
hostname -I | awk '{print $1}'

# Windows (PowerShell)
(Get-NetIPAddress -AddressFamily IPv4 -InterfaceAlias "Wi-Fi" -or -InterfaceAlias "Ethernet").IPAddress
```

Luego accede desde cualquier dispositivo en la misma red: `http://TU_IP:8080`

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
│   │   │   ├── VideoPlayer.jsx
│   │   │   ├── ReelsView.jsx
│   │   │   ├── Chat.jsx
│   │   │   └── UploadVideo.jsx  # 📤 Nuevo: Upload de videos
│   │   └── hooks/       # Custom hooks
│   └── dist/            # Build de producción
├── src/
│   ├── config/          # Configuración
│   └── routes/          # Rutas de API
│       ├── channels.js
│       ├── playlist.js
│       └── upload.js    # 📤 Nuevo: Endpoint de upload
├── streams/             # Streams HLS generados
├── videos/              # Videos de origen
├── uploads/             # Videos temporales durante upload
├── server.js            # Servidor Express
├── convert-to-hls.js    # Script de conversión
└── Dockerfile           # Configuración Docker
```

## 📤 API de Upload

### POST `/api/upload`

Sube un video que se convertirá automáticamente a HLS.

**Parámetros (FormData):**
- `video`: Archivo de video (mp4, mkv, avi, mov, webm)
- `channelName`: Nombre del canal (solo letras, números, guiones y guiones bajos)

**Límites:**
- Tamaño máximo: 500MB por video
- Formatos soportados: mp4, mkv, avi, mov, webm

**Respuesta exitosa:**
```json
{
  "success": true,
  "message": "Video subido y convertido exitosamente",
  "channel": "mi_canal",
  "localPath": "/videos/mi_canal.mp4",  // En local
  "dropboxUrl": "https://...",          // En producción
  "size": 75894272
}
```

### GET `/api/upload/status`

Verifica la disponibilidad del servicio de upload.

**Respuesta:**
```json
{
  "uploadEnabled": true,
  "maxFileSize": "500MB",
  "allowedFormats": ["mp4", "mkv", "avi", "mov", "webm"],
  "environment": "development",
  "dropboxEnabled": false
}
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

## ⚙️ Variables de Entorno

### Desarrollo Local
```bash
NODE_ENV=development
PORT=8080
HOST=0.0.0.0  # Para acceso LAN
```

### Producción (Render)
```bash
NODE_ENV=production
PORT=8080
HOST=0.0.0.0
DROPBOX_ACCESS_TOKEN=sl.xxxxxxxxxxxxx  # Token de API de Dropbox (para uploads)
CLIENT_URL=https://tu-app.onrender.com
```

**¿Cómo obtener DROPBOX_ACCESS_TOKEN?**
1. Ve a https://www.dropbox.com/developers/apps
2. Crea una nueva app → Scoped access → Full Dropbox
3. En Settings → Generated access token → Generate
4. Copia el token y añádelo a las variables de entorno en Render

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
