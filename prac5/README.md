# 📺 Servidor IPTV Local con HLS

Servidor IPTV local profesional que convierte videos a formato HLS (HTTP Live Streaming) y los sirve mediante una interfaz web moderna estilo TikTok/Reels.

## ✨ Características

- 🎬 Conversión de videos a formato HLS con múltiples perfiles de calidad
- 📡 Servidor HTTP optimizado con Express.js
- 🎨 Interfaz web moderna tipo Reels/TikTok
- 🔄 Scroll vertical fluido entre canales
- 🎮 Controles de reproducción personalizados
- 📱 Diseño responsive (móvil y escritorio)
- 🐳 Soporte completo para Docker
- 🔌 API REST para gestión de canales
- ⚡ Build optimizado multi-stage

## 🏗️ Estructura del Proyecto

```
prac5/
├── client/                  # Frontend React
│   ├── src/
│   │   ├── components/     # Componentes React
│   │   │   ├── ReelsView.jsx
│   │   │   └── VideoPlayer.jsx
│   │   ├── hooks/          # Custom hooks
│   │   │   └── index.js
│   │   ├── App.jsx
│   │   └── main.jsx
│   └── package.json
├── src/
│   ├── config/             # Configuraciones
│   │   ├── server.config.js
│   │   └── ffmpeg.config.js
│   └── routes/             # Rutas de API
│       ├── channels.js
│       └── playlist.js
├── streams/                # Streams HLS generados
├── videos/                 # Videos fuente
├── server.js              # Servidor Express
├── convert-to-hls.js      # Script de conversión
├── Dockerfile
├── docker-compose.yml
└── package.json
```

## 📋 Requisitos

- Node.js 18+ o superior
- FFmpeg
- Docker (opcional)

### Instalar FFmpeg

**Ubuntu/Debian:**
```bash
sudo apt update && sudo apt install ffmpeg
```

**Fedora:**
```bash
sudo dnf install ffmpeg
```

**Arch Linux:**
```bash
sudo pacman -S ffmpeg
```

**macOS:**
```bash
brew install ffmpeg
```

## 🚀 Instalación y Uso

### Opción 1: Instalación Local

1. **Clonar e instalar dependencias:**
```bash
npm install
cd client && npm install && cd ..
```

2. **Convertir videos a HLS:**
```bash
node convert-to-hls.js <video> <nombre_canal> [perfil]
```

**Perfiles disponibles:**
- `source` - Copia directa sin recodificar (rápido, recomendado)
- `low` - 360p, 500kbps (móvil, datos limitados)
- `medium` - 720p, 1500kbps (estándar)
- `high` - 1080p, 3000kbps (alta calidad)

**Ejemplos:**
```bash
# Conversión rápida sin recodificar
node convert-to-hls.js videos/pelicula.mp4 peliculas source

# Calidad media optimizada
node convert-to-hls.js videos/serie.mkv series medium
```

3. **Iniciar servidor de desarrollo:**
```bash
npm run dev
```

Esto inicia:
- **Backend API**: http://localhost:8080
- **Frontend (Vite)**: http://localhost:3000

Accede a http://localhost:3000 en tu navegador.

4. **Construir para producción:**
```bash
npm run build
npm start
```

### Opción 2: Docker (Recomendado)

1. **Construir y ejecutar con Docker Compose:**
```bash
docker-compose up -d
```

2. **Ver logs:**
```bash
docker-compose logs -f
```

3. **Detener:**
```bash
docker-compose down
```

4. **Convertir videos dentro del contenedor:**
```bash
docker exec iptv-hls-server node convert-to-hls.js /app/videos/video.mp4 mi_canal source
```

## 🌐 Endpoints API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Interfaz web principal |
| GET | `/api/channels` | Lista todos los canales disponibles |
| GET | `/api/health` | Estado del servidor |
| GET | `/streams/:canal/playlist.m3u8` | Playlist HLS de un canal |

## 📦 Scripts NPM

```bash
npm start          # Iniciar servidor de producción
npm run dev        # Modo desarrollo (servidor + cliente)
npm run build      # Construir cliente para producción
npm run convert    # Alias para convert-to-hls.js
```

## ⚙️ Configuración

### Configuración del Servidor
Edita `src/config/server.config.js`:
```javascript
module.exports = {
  port: 8080,
  env: 'production',
  cors: { origin: '*' }
};
```

### Configuración de FFmpeg
Edita `src/config/ffmpeg.config.js` para ajustar perfiles de calidad y configuración HLS.

## 🎯 Características Técnicas

### Backend
- Express.js con arquitectura modular
- Rutas separadas por funcionalidad
- Manejo de errores centralizado
- Health checks para monitoring
- Logging estructurado

### Frontend
- React 18 con hooks modernos
- Custom hooks reutilizables
- Video.js para reproducción HLS
- Gestos táctiles para móvil
- Scroll snap API
- Diseño responsive

### DevOps
- Dockerfile multi-stage optimizado
- Usuario no-root para seguridad
- Health checks integrados
- Volúmenes persistentes
- Build cache optimizado

## 🔧 Solución de Problemas

**El video no se reproduce:**
- Verifica que FFmpeg esté instalado: `ffmpeg -version`
- Comprueba que exista `streams/[canal]/playlist.m3u8`
- Revisa los logs del navegador (F12)

**Error de conversión:**
- Verifica el formato del video de entrada
- Intenta con perfil `source` para copia directa
- Revisa que tengas espacio en disco

**Docker no inicia:**
- Verifica que los puertos estén disponibles: `lsof -i :8080`
- Revisa logs: `docker-compose logs`

## 📝 Desarrollo

### Estructura de Custom Hooks
- `useSwipe`: Manejo de gestos táctiles y scroll
- `useChannels`: Gestión de canales y API
- `useTimeFormat`: Formateo de tiempo de video

### Añadir Nuevos Perfiles de Calidad
Edita `src/config/ffmpeg.config.js` y añade tu perfil personalizado.

## 🎨 Deploy en Render.com

Este proyecto está listo para deployear en Render.com. Ver [RENDER-DEPLOY.md](./RENDER-DEPLOY.md) para instrucciones completas.

**Quick start:**
1. Sube tu código a GitHub
2. Conecta tu repo en [render.com](https://render.com)
3. Render detectará automáticamente `render.yaml`
4. Deploy automático con Docker

**Características:**
- ✅ Free tier con 750h/mes
- ✅ WebSocket nativo
- ✅ SSL automático
- ✅ Auto-deploy desde GitHub
- ⚠️ Sleep después de 15 min (plan Free)

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:
1. Fork el proyecto
2. Crea una rama para tu feature
3. Commit tus cambios
4. Push a la rama
5. Abre un Pull Request

## 📄 Licencia

ISC

## 👤 Autor

Desarrollado para streaming local de contenido multimedia.

---

**Nota:** Este proyecto está optimizado para uso local. Para producción en internet, considera implementar autenticación, HTTPS y protección DDoS.

**Ejemplo:**
```bash
node convert-to-hls.js video.mp4 canal1
node convert-to-hls.js pelicula.mkv peliculas
node convert-to-hls.js serie.avi series
```

Esto creará una carpeta `streams/<nombre_canal>` con los archivos HLS.

### 2. Iniciar el servidor

```bash
npm start
```

El servidor se iniciará en `http://localhost:8080`

### 3. Acceder al reproductor

Abre tu navegador y ve a:
- **Reproductor web:** http://localhost:8080
- **API de canales:** http://localhost:8080/api/channels
- **Playlist principal:** http://localhost:8080/playlist.m3u8

## 📁 Estructura del Proyecto

```
prac5/
├── server.js              # Servidor Express
├── convert-to-hls.js      # Script de conversión a HLS
├── package.json           # Dependencias
├── README.md             # Este archivo
├── public/               # Archivos estáticos
│   └── index.html        # Reproductor web
└── streams/              # Streams HLS (generados)
    ├── canal1/
    │   ├── playlist.m3u8
    │   └── segment*.ts
    └── canal2/
        ├── playlist.m3u8
        └── segment*.ts
```

## 🎥 Formatos de Video Soportados

FFmpeg soporta una amplia variedad de formatos:
- MP4, MKV, AVI, MOV
- FLV, WMV, WebM
- MPEG, MPG, TS
- Y muchos más

## 🌐 Acceso desde Otros Dispositivos

Para acceder desde otros dispositivos en tu red local:

1. Obtén tu IP local:
```bash
ip addr show
```

2. Accede desde otro dispositivo usando:
```
http://<TU_IP>:8080
```

Ejemplo: `http://192.168.1.100:8080`

## 🔧 Configuración Avanzada

### Cambiar el puerto del servidor

Edita `server.js` y cambia la línea:
```javascript
const PORT = 8080;
```

### Ajustar calidad del HLS

Edita `convert-to-hls.js` y modifica los parámetros de FFmpeg:
```javascript
'-hls_time', '10',  // Duración de cada segmento (segundos)
'-codec:', 'copy',  // Cambia a 'libx264' para recodificar
```

## 📱 Reproducción en Dispositivos

- **Navegadores:** Chrome, Firefox, Safari, Edge (reproduce directamente)
- **VLC:** Abre la URL del stream
- **Aplicaciones IPTV:** Usa la URL de la playlist M3U8

## ⚠️ Notas

- Los archivos HLS ocupan espacio. Un video de 1GB puede generar ~1GB de segmentos
- La conversión puede tomar tiempo dependiendo del tamaño del video
- Usa `codec: copy` para conversión rápida sin recodificación
- Para mejor compatibilidad, considera recodificar a H.264/AAC

## 🐳 Docker (Recomendado)

### Construir y ejecutar con Docker Compose

```bash
# Construir la imagen
docker-compose build

# Iniciar el contenedor
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener el contenedor
docker-compose down
```

### Agregar videos al contenedor

Simplemente coloca tus videos en la carpeta `videos/` y el contenedor los convertirá automáticamente al iniciar:

```bash
# Copiar videos a la carpeta
cp mi_video.mp4 videos/

# Reiniciar el contenedor para procesar nuevos videos
docker-compose restart
```

### Comandos Docker útiles

```bash
# Ver contenedor en ejecución
docker ps

# Acceder al contenedor
docker exec -it iptv-hls-server sh

# Ver logs en tiempo real
docker-compose logs -f iptv-server

# Eliminar todo (incluyendo volúmenes)
docker-compose down -v
```

### Construir imagen Docker manualmente

```bash
# Construir imagen
docker build -t iptv-hls-server .

# Ejecutar contenedor
docker run -d \
  -p 8080:8080 \
  -v $(pwd)/streams:/app/streams \
  -v $(pwd)/videos:/app/videos \
  --name iptv-server \
  iptv-hls-server
```

## 🆘 Solución de Problemas

### FFmpeg no encontrado
```bash
which ffmpeg  # Verifica que FFmpeg esté instalado
```

### Error de permisos
```bash
chmod +x convert-to-hls.js
chmod +x docker-entrypoint.sh
```

### Puerto en uso
Cambia el puerto en `server.js` o `docker-compose.yml`, o detén el proceso que usa el puerto 8080

### Docker no encuentra los videos
Asegúrate de que los videos estén en la carpeta `videos/` antes de iniciar el contenedor

## 📝 Licencia

ISC
