# ✅ Checklist para Deploy en Render.com

## 📋 Estado Actual

Tu proyecto **YA ESTÁ PREPARADO** para Render. Solo necesitas seguir estos pasos:

## 🚀 Pasos para Deploy

### 1. Subir código a GitHub ✅
```bash
cd /home/omar/Escritorio/AD/prac5
git add .
git commit -m "Preparado para deploy en Render"
git push origin main
```

### 2. Crear servicio en Render.com 🌐

1. Ve a [dashboard.render.com](https://dashboard.render.com)
2. Click en **"New +"** → **"Web Service"**
3. Conecta tu repositorio: `omar-cornejo/AD`
4. Selecciona el repositorio

### 3. Configuración del servicio ⚙️

**Configuración básica:**
- **Name**: `iptv-hls-streaming` (o el que prefieras)
- **Region**: `Frankfurt` (o la más cercana)
- **Branch**: `main`
- **Root Directory**: `prac5` ⚠️ **IMPORTANTE**
- **Environment**: `Docker`
- **Plan**: `Free`

**Variables de entorno** (se configuran automáticamente desde `render.yaml`):
- `NODE_ENV=production`
- `PORT=10000`

### 4. Deploy 🎉

Click en **"Create Web Service"** y espera 5-10 minutos.

Tu aplicación estará disponible en: `https://iptv-hls-streaming.onrender.com`

## ✅ Archivos de Configuración Verificados

- ✅ `render.yaml` - Configuración de Render
- ✅ `Dockerfile` - Multi-stage build optimizado
- ✅ `.dockerignore` - Excluye archivos innecesarios
- ✅ `.gitignore` - Protege archivos sensibles
- ✅ `package.json` - Scripts de build configurados
- ✅ `server.js` - Health check endpoint (`/api/health`)
- ✅ `client/package.json` - Configuración del frontend

## 📦 Características del Deploy

### ✅ Lo que está incluido:
- ✅ Servidor Express con API REST
- ✅ Frontend React (build optimizado)
- ✅ WebSocket (Socket.io) para chat en tiempo real
- ✅ Health checks automáticos
- ✅ HTTPS automático (certificado SSL)
- ✅ Build multi-stage (reduce tamaño de imagen)
- ✅ Usuario no-root (seguridad)
- ✅ FFmpeg instalado

### ⚠️ Limitaciones del plan Free:
- ❌ **Videos NO se incluyen** (archivos grandes)
- ❌ **Streams NO persisten** entre deploys
- ⏱️ El servicio se "duerme" después de 15 minutos de inactividad
- 💾 Sin almacenamiento persistente

## 🎬 Soluciones para Videos

### Opción 1: Usar videos de ejemplo (Recomendado para pruebas)
Modifica `src/routes/channels.js` para usar URLs de videos públicos:

```javascript
const channels = [
  {
    id: 1,
    name: "Canal Demo",
    streamUrl: "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
    type: "hls"
  }
];
```

### Opción 2: Subir videos a almacenamiento en la nube
- **Cloudinary** (gratis hasta 25GB)
- **AWS S3** (gratis 5GB primer año)
- **Backblaze B2** (10GB gratis)

### Opción 3: Plan de pago de Render
Agregar disco persistente ($7/mes por 50GB)

## 🔧 Comandos Útiles Post-Deploy

### Ver logs en tiempo real:
```bash
# En el dashboard de Render, pestaña "Logs"
```

### Re-deployar:
```bash
git add .
git commit -m "Actualización"
git push origin main
# Auto-deploy activado en render.yaml
```

### Test local antes de deployar:
```bash
docker build -t iptv-test .
docker run -p 8080:8080 iptv-test
# Visita: http://localhost:8080
```

## 🐛 Troubleshooting

### El servicio no inicia:
1. Revisa logs en Render dashboard
2. Verifica que `Root Directory` sea `prac5`
3. Confirma que las dependencias se instalaron correctamente

### Error de build:
- Verifica que `client/package.json` tenga todas las dependencias
- Asegúrate que `npm run build` funciona localmente

### Sin videos/streams:
- Normal en deploy inicial
- Implementa una de las soluciones de la sección "🎬 Soluciones para Videos"

## 📚 Documentación Adicional

- [RENDER-DEPLOY.md](./RENDER-DEPLOY.md) - Guía detallada de deploy
- [README.md](./README.md) - Documentación del proyecto
- [OPTIMIZACIONES.md](./OPTIMIZACIONES.md) - Mejoras implementadas

## 🎯 Próximos Pasos Después del Deploy

1. ✅ Verificar que la app carga en Render
2. 🎬 Configurar fuente de videos (ver "Soluciones para Videos")
3. 🔧 Probar todas las funcionalidades
4. 📊 Monitorear logs y performance
5. 🚀 Compartir tu URL pública

---

**¿Listo para deployar?** Ejecuta:
```bash
git add . && git commit -m "Deploy a Render" && git push origin main
```

Luego sigue los pasos en [dashboard.render.com](https://dashboard.render.com) 🚀
