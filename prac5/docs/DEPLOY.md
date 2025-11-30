# Deploy en Render.com

Guía para deployear el servidor IPTV en Render.com.

## Requisitos Previos

- Cuenta en [Render.com](https://render.com) (plan gratuito disponible)
- Repositorio en GitHub

## Configuración

El proyecto incluye `render.yaml` con la configuración automática:

```yaml
services:
  - type: web
    name: iptv-hls-streaming
    env: docker
    rootDir: prac5
    plan: free
    region: frankfurt
    healthCheckPath: /api/health
```

## Pasos para Deploy

### 1. Subir código a GitHub

```bash
git add .
git commit -m "Preparado para deploy"
git push origin main
```

### 2. Crear Web Service en Render

1. Ir a [dashboard.render.com](https://dashboard.render.com)
2. Click en **"New +"** → **"Web Service"**
3. Conectar repositorio de GitHub
4. Seleccionar el repositorio

### 3. Configurar el servicio

**Configuración básica:**
- **Name**: `iptv-hls-streaming`
- **Region**: `Frankfurt`
- **Branch**: `main`
- **Root Directory**: `prac5`
- **Environment**: `Docker`
- **Plan**: `Free`

### 4. Variables de entorno

Se configuran automáticamente desde `render.yaml`:
- `NODE_ENV=production`
- `PORT=10000`

### 5. Iniciar Deploy

Click en **"Create Web Service"**. El build tarda aproximadamente 8-12 minutos.

## Características del Deploy

### Incluido en el build:
- Servidor Express con API REST
- Frontend React compilado
- WebSocket para chat en tiempo real
- FFmpeg para conversión de videos
- 2 videos de demostración pre-convertidos
- HTTPS automático
- Health checks

### Limitaciones del plan gratuito:
- El servicio se suspende tras 15 minutos de inactividad
- Sin almacenamiento persistente entre deploys
- Videos personalizados requieren solución externa

## Videos de Demostración

El Dockerfile descarga y convierte automáticamente 2 videos durante el build:
- Canal 1: Big Buck Bunny (10 segundos)
- Canal 2: Jellyfish (10 segundos)

## Agregar Videos Personalizados

### Opción 1: Modificar Dockerfile
Agregar más descargas en el Dockerfile:

```dockerfile
RUN curl -L -o videos/video3.mp4 "URL_DEL_VIDEO" && \
    node convert-to-hls.js videos/video3.mp4 canal_3 source
```

### Opción 2: Almacenamiento externo
- Cloudinary (25GB gratis)
- AWS S3 (5GB gratis primer año)
- Backblaze B2 (10GB gratis)

### Opción 3: Plan de pago
Render Starter ($7/mes) incluye disco persistente de 50GB.

## Verificar el Deploy

Tu aplicación estará disponible en:
```
https://iptv-hls-streaming.onrender.com
```

Endpoints disponibles:
- `/` - Frontend
- `/api/channels` - Lista de canales
- `/api/health` - Health check

## Auto-Deploy

El auto-deploy está activado en `render.yaml`. Cada push a `main` desplegará automáticamente.

## Troubleshooting

### El servicio no inicia
- Revisar logs en el dashboard de Render
- Verificar que `Root Directory` sea `prac5`
- Confirmar que el build se completó

### No aparecen canales
- Esperar a que el build complete
- La conversión de videos tarda 1-2 minutos adicionales
- Revisar logs para errores en la conversión

### Error de build
- Verificar que todas las dependencias están en `package.json`
- Confirmar que `npm run build` funciona localmente
- Revisar sintaxis del Dockerfile

## Logs y Monitoreo

Ver logs en tiempo real:
1. Dashboard de Render
2. Seleccionar el servicio
3. Pestaña "Logs"

## Actualizar el Deploy

```bash
git add .
git commit -m "Actualización"
git push origin main
```

El auto-deploy reconstruirá automáticamente.
