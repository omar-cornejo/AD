# 🎨 Deploy a Render.com

Este proyecto está listo para deployear en [Render.com](https://render.com)

## 📋 Pre-requisitos

- Cuenta en Render.com (gratis)
- Repositorio en GitHub/GitLab (recomendado) o deploy manual

## 🚀 Método 1: Deploy desde GitHub (Recomendado)

### 1. Subir código a GitHub

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/tu-usuario/tu-repo.git
git push -u origin main
```

### 2. Crear Web Service en Render

1. Ve a [dashboard.render.com](https://dashboard.render.com)
2. Click en **"New +"** → **"Web Service"**
3. Conecta tu repositorio de GitHub
4. Selecciona tu repositorio

### 3. Configurar el servicio

- **Name**: `iptv-hls-streaming` (o el nombre que prefieras)
- **Region**: Frankfurt (o la más cercana a ti)
- **Branch**: `main`
- **Root Directory**: (dejar vacío)
- **Environment**: `Docker`
- **Plan**: `Free`

### 4. Variables de entorno (automáticas desde render.yaml)

Render detectará automáticamente `render.yaml` y configurará:
- `NODE_ENV=production`
- `PORT=10000`

### 5. Deploy

Click en **"Create Web Service"** y espera 5-10 minutos.

## 🚀 Método 2: Deploy Manual

Si no quieres usar GitHub:

### 1. Instalar Render CLI

```bash
npm install -g @render/cli
```

### 2. Login

```bash
render login
```

### 3. Deploy

```bash
render deploy
```

## 🔧 Configuración Avanzada

### Agregar volumen persistente (Opcional)

Para mantener streams entre deploys:

1. En tu servicio, ve a **"Disks"**
2. Click en **"Add Disk"**
3. Name: `streams`
4. Mount Path: `/app/streams`
5. Size: 1GB (suficiente para empezar)

**Nota:** Los discos no están disponibles en el plan Free.

### Variables de entorno adicionales

Puedes agregar más variables en el dashboard:

```
CLIENT_URL=https://tu-servicio.onrender.com
```

## 📦 Características de Render

- ✅ **Free tier**: 750 horas/mes gratis
- ✅ **WebSocket**: Soportado nativamente
- ✅ **Docker**: Build automático desde Dockerfile
- ✅ **SSL**: HTTPS automático con certificado Let's Encrypt
- ✅ **Auto-deploy**: Desde GitHub automáticamente
- ✅ **Health checks**: Configurado en `/api/health`
- ⚠️ **Sleep**: El plan Free duerme después de 15 min de inactividad

## 🌐 URLs

Después del deploy, Render te dará una URL como:

```
https://iptv-hls-streaming.onrender.com
```

## 🔄 Re-deployear

### Desde GitHub (Automático)
```bash
git add .
git commit -m "Update"
git push
```

Render detectará el push y re-deployará automáticamente.

### Manual
```bash
render deploy
```

## 📊 Monitoreo

### Ver logs

En el dashboard:
- Ve a tu servicio
- Click en **"Logs"**
- Ver logs en tiempo real

### Métricas

- CPU usage
- Memory usage
- Request count
- Response times

## 💡 Tips Importantes

### ⚠️ Plan Free - Limitaciones

1. **Sleep mode**: Duerme después de 15 min sin tráfico
   - Primera request tarda ~30-60 segundos en despertar
   - Solución: Usa un servicio de ping cada 10 min

2. **Build time**: ~5-10 minutos
   - Render cachea layers de Docker

3. **Bandwidth**: 100 GB/mes gratis

### 🎯 Optimizaciones

**Mantener el servicio activo (Free plan):**

Crea un cron job o usa servicios como:
- [UptimeRobot](https://uptimerobot.com) (gratis)
- [Cron-job.org](https://cron-job.org) (gratis)

Configura un ping cada 10 minutos a tu URL.

**Acelerar deploys:**

Render cachea layers de Docker. El Dockerfile multi-stage ya está optimizado.

## 🐛 Troubleshooting

### El servicio no inicia

Ver logs en el dashboard:
```
Logs → Buscar errores
```

### Error de build

1. Verifica que `Dockerfile` esté en la raíz
2. Revisa que `render.yaml` tenga la configuración correcta
3. Verifica que todas las dependencias estén en `package.json`

### WebSocket no conecta

Verifica en `Chat.jsx` que la URL sea correcta:
```javascript
const socketUrl = import.meta.env.PROD 
  ? window.location.origin 
  : 'http://localhost:8080';
```

### Servicio lento (sleep mode)

Es normal en el plan Free. Primera request tarda en despertar.

**Soluciones:**
1. Upgrade a plan Starter ($7/mes - sin sleep)
2. Usa un servicio de ping
3. Acepta el delay inicial

## 📝 Comandos Útiles (CLI)

```bash
render services list          # Ver servicios
render services logs <id>     # Ver logs
render services restart <id>  # Reiniciar servicio
render services scale <id>    # Escalar (requiere plan pago)
```

## 🎯 Próximos Pasos

1. ✅ Deploy inicial completado
2. Configura dominio custom (opcional)
3. Configura UptimeRobot para evitar sleep
4. Upgrade a plan Starter si necesitas sin sleep ($7/mes)
5. Monitorea logs y métricas

## 💰 Precios

- **Free**: $0/mes
  - 750 horas/mes
  - Sleeps después de 15 min
  - 100 GB bandwidth

- **Starter**: $7/mes
  - Sin sleep mode
  - 100 GB bandwidth
  - Mejor performance

- **Standard**: $25/mes
  - 1TB bandwidth
  - Más recursos

## 🔗 Enlaces Útiles

- Dashboard: https://dashboard.render.com
- Docs: https://render.com/docs
- Status: https://status.render.com
- Soporte: support@render.com

---

**¿Preguntas?** Revisa la [documentación oficial](https://render.com/docs)
