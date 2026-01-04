# IPTV HLS Server

Sistema de streaming de video bajo demanda (VOD) basado en HLS, con una interfaz de consumo tipo Reels.

## Estructura del repositorio

- `client/`: Frontend (React + Vite)
- `src/`: Código del servidor (configuración, rutas y middleware)
- `streams/`: Playlists HLS y segmentos generados
- `videos/`: Vídeos fuente (no incluidos en el repositorio)
- `uploads/`: Archivos temporales para procesos de subida
- `server.js`: Servidor Express principal
- `convert-to-hls.js`: Script de conversión a HLS
- `Dockerfile`, `docker-compose.yml`: Contenedores y orquestación

## Requisitos

- Node.js 18 o superior
- FFmpeg (necesario para conversión local)
- Docker y Docker Compose (recomendado para despliegue)

## Resumen de funcionamiento

1. Un vídeo fuente se coloca en `videos/` o se sube mediante la interfaz `/api/upload`.
2. El sistema convierte el archivo a HLS (genera `playlist.m3u8` y segmentos `.ts`) y los coloca en `streams/<canal>/`.
3. El frontend reproduce las playlists HLS desde `/streams`.

## Instalación y ejecución

### Opción recomendada: Docker

```bash
# Construir la imagen y dependencias
docker compose build

# Levantar servicios en segundo plano
docker compose up -d

# Ver logs
docker compose logs -f

# Parar y eliminar
docker compose down
```

El servidor escucha en el puerto configurado (por defecto `8080`).

### Desarrollo local

1. Instalar dependencias:

```bash
npm run setup
```

2. Asegurar que `ffmpeg` está disponible (`ffmpeg -version`).

3. Convertir vídeo manualmente (opcional):

```bash
# Sintaxis: npm run convert <ruta_video> <nombre_canal> [perfil]
npm run convert videos/mi_video.mp4 mi_canal source
```

4. Ejecutar en modo desarrollo:

```bash
npm run dev
```

Frontend en desarrollo: `http://localhost:3000`.
Backend: `http://localhost:8080`.

## Endpoints principales

- `GET /api/channels` — Lista de canales detectados en `streams/`.
- `GET /playlist.m3u8` — Playlist general (según configuración).
- `GET /streams/:channel/playlist.m3u8` — Playlist HLS de un canal.
- `GET /streams/:channel/:segment.ts` — Segmentos HLS.
- `POST /api/upload` — Subida de vídeo (FormData: `video`, `channelName`).
- `GET /api/upload/status` — Estado del servicio de subida.
- `GET /api/health` — Estado del servidor (healthcheck).

## API de subida (`POST /api/upload`)

Descripción: Permite subir un archivo de vídeo; el servidor lo convertirá a HLS y lo publicará en `streams/`.

Parámetros (FormData):
- `video`: Archivo de vídeo.
- `channelName`: Identificador del canal (caracteres permitidos: letras, números, guiones y guiones bajos).

Límites y formatos: tamaño máximo configurable (por defecto 500 MB). Formatos habituales compatibles: `mp4`, `mkv`, `avi`, `mov`, `webm`.

Respuesta de ejemplo (éxito):

```json
{
  "success": true,
  "message": "Video subido y convertido exitosamente",
  "channel": "mi_canal",
  "localPath": "/videos/mi_canal.mp4",
  "dropboxUrl": "https://...",
  "size": 75894272
}
```

## Conversión de vídeos

El proyecto incluye el script `convert-to-hls.js` y admite perfiles de conversión:
- `source`: Sin recodificar (rápido)
- `low`: Perfil de baja resolución
- `medium`: Perfil intermedio
- `high`: Perfil alta calidad

Ejemplo de uso:

```bash
npm run convert videos/pelicula.mp4 canal_peliculas source
```

## Variables de entorno importantes

- `NODE_ENV` — `development` o `production`.
- `PORT` — Puerto del servidor (por defecto `8080`).
- `HOST` — Host donde escucha (por defecto `0.0.0.0`).
- `DROPBOX_ACCESS_TOKEN` — Token para integración con Dropbox (producción).
- `CLIENT_URL` — URL del frontend para CORS y Socket.IO.

## Comandos disponibles

- `npm run dev` — Server + cliente en modo desarrollo.
- `npm start` — Ejecutar solo el servidor (producción).
- `npm run setup` — Instalar dependencias del proyecto.
- `npm run convert` — Convertir vídeos manualmente con `convert-to-hls.js`.
- `docker compose build && docker compose up -d` — Construir y desplegar con Docker Compose.

## Despliegue y persistencia

Para entornos en contenedores es recomendable que la carpeta `streams/` se monte como volumen persistente o que los activos se gestionen con almacenamiento externo, ya que la conversión genera archivos que deben sobrevivir a reinicios.

## Solución de problemas

- Reproducción fallida: comprobar que `streams/<canal>/playlist.m3u8` y archivos `.ts` existen y son accesibles.
- No aparecen canales: revisar permisos y estructura de carpetas en `streams/`.
- Errores en conversión: comprobar la versión de FFmpeg (`ffmpeg -version`) y que el archivo fuente no esté corrupto.
- Revisar logs: `docker compose logs -f` o logs del proceso Node.

## Seguridad

Endpoints sensibles pueden protegerse mediante JWT. En producción, configure `DROPBOX_ACCESS_TOKEN` y `CLIENT_URL` correctamente.

## Documentación adicional

Consulte la documentación en `docs/` para guías de despliegue y detalles técnicos extendidos.

## Licencia

Licencia ISC.
