# 📤 Guía de Configuración de Upload de Videos

## 🎯 Características

El sistema permite a los usuarios **subir videos directamente desde la interfaz web**. Los videos se procesan automáticamente:

1. ✅ **Conversión a HLS** con FFmpeg
2. ✅ **Almacenamiento dual**:
   - **Local** (desarrollo): Guarda en `/videos`
   - **Producción** (Render): Sube a Dropbox

## 🌐 Acceso a la Interfaz de Upload

Accede a: `http://localhost:8080/upload` o click en el botón 📤 en la interfaz principal.

### Funcionalidades:
- 📁 Drag & Drop de archivos
- 📊 Barra de progreso en tiempo real
- 🎬 Preview del video antes de subir
- ✅ Validación de formato y tamaño
- 🔄 Conversión automática a HLS

## ⚙️ Configuración para Producción (Dropbox)

### Paso 1: Crear App en Dropbox

1. Ve a https://www.dropbox.com/developers/apps
2. Click en **"Create app"**
3. Selecciona:
   - **Scoped access**
   - **Full Dropbox** (acceso completo)
   - Nombre: `iptv-hls-server` (o el que prefieras)
4. Click en **"Create app"**

### Paso 2: Configurar Permisos

En la pestaña **Permissions**, marca los siguientes permisos:

- ✅ `files.content.write` - Para subir archivos
- ✅ `files.content.read` - Para leer archivos
- ✅ `sharing.write` - Para crear links públicos

Click en **"Submit"** para guardar los cambios.

### Paso 3: Generar Token de Acceso

1. Ve a la pestaña **Settings**
2. Busca la sección **"OAuth 2"**
3. En **"Generated access token"**, click en **"Generate"**
4. **Copia el token** (empieza con `sl.` y tiene ~200 caracteres)
5. ⚠️ **Importante**: Este token solo se muestra una vez. Guárdalo en un lugar seguro.

### Paso 4: Configurar en Render

1. Ve a tu proyecto en Render
2. **Environment** → **Add Environment Variable**
3. Añade:
   ```
   Key: DROPBOX_ACCESS_TOKEN
   Value: sl.xxxxxxxxxxxxx... (tu token)
   ```
4. **Save Changes**
5. Render redesplegará automáticamente

### Paso 5: Verificar Configuración

Accede a: `http://tu-app.onrender.com/api/upload/status`

Deberías ver:
```json
{
  "uploadEnabled": true,
  "maxFileSize": "500MB",
  "allowedFormats": ["mp4", "mkv", "avi", "mov", "webm"],
  "environment": "production",
  "dropboxEnabled": true  ← ✅ Debe ser true
}
```

## 🔐 Seguridad

### Tokens de Acceso

Los tokens generados en el paso 3 **no expiran**, pero puedes revocarlos en cualquier momento desde la configuración de tu app en Dropbox.

**Recomendaciones:**
- 🔒 **NUNCA** subas el token a Git
- 🔒 **NUNCA** lo compartas públicamente
- 🔒 Usa variables de entorno (`.env` o Render Environment)
- 🔄 Regenera el token si sospechas que fue comprometido

### Límites de Subida

- **Tamaño máximo por video**: 500MB
- **Formatos permitidos**: mp4, mkv, avi, mov, webm
- **Validación**: El nombre del canal solo puede contener: `a-z A-Z 0-9 _ -`

## 🧪 Pruebas Locales (sin Dropbox)

Para probar localmente sin necesidad de configurar Dropbox:

1. **NO** configures `DROPBOX_ACCESS_TOKEN`
2. Los videos se guardarán en `/videos` automáticamente
3. Verifica con:
   ```bash
   curl http://localhost:8080/api/upload/status
   # dropboxEnabled: false ← Normal en local
   ```

## 📝 Flujo de Upload

### Desarrollo Local
```
Usuario → Upload UI → Backend → /videos → FFmpeg → /streams → Listo ✅
```

### Producción (Render)
```
Usuario → Upload UI → Backend → Dropbox → FFmpeg → /streams → Listo ✅
                                    ↓
                              (almacenamiento permanente)
```

## 🐛 Troubleshooting

### "Dropbox no configurado. Falta DROPBOX_ACCESS_TOKEN"

**Causa**: Variable de entorno no configurada en producción.

**Solución**:
1. Verifica que el token esté configurado en Render
2. Asegúrate de que el token sea válido (empieza con `sl.`)
3. Redespliega la aplicación

### "Error 401: Invalid access token"

**Causa**: Token inválido o revocado.

**Solución**:
1. Ve a https://www.dropbox.com/developers/apps
2. Genera un **nuevo token**
3. Actualiza la variable `DROPBOX_ACCESS_TOKEN` en Render

### "Error al procesar el video"

**Causa**: Archivo corrupto o formato no soportado.

**Solución**:
1. Verifica que el video sea válido (reprodúcelo localmente)
2. Asegúrate de usar formatos soportados: mp4, mkv, avi, mov, webm
3. Verifica que el tamaño sea menor a 500MB

### Videos no aparecen después de subir

**Causa**: La conversión a HLS está en progreso.

**Solución**:
1. El proceso puede tardar 1-5 minutos según el tamaño
2. Monitorea los logs: `docker compose logs -f` (local) o Render logs (producción)
3. Busca mensajes como: `✅ Video convertido exitosamente`
4. Recarga la página después de unos minutos

## 📊 Logs Útiles

### Local (Docker)
```bash
# Ver logs en tiempo real
docker compose logs -f

# Buscar errores de upload
docker compose logs | grep upload

# Buscar conversiones exitosas
docker compose logs | grep "convertido exitosamente"
```

### Producción (Render)
1. Ve a tu proyecto en Render
2. **Logs** (menú lateral)
3. Busca mensajes de:
   - `📥 Video recibido`
   - `🎬 Convirtiendo video a HLS`
   - `☁️ Subiendo a Dropbox`
   - `✅ Video convertido exitosamente`

## 🚀 Uso en la Interfaz

1. **Accede** a `/upload` o click en el botón 📤
2. **Arrastra** un video o click en "Seleccionar archivo"
3. **Nombra** tu canal (auto-sugerido del nombre del archivo)
4. **Sube** y espera:
   - Barra de progreso durante la subida
   - "Procesando video..." cuando se está convirtiendo
   - ✅ Éxito → Redirección automática a la lista de canales
5. **Disfruta** tu nuevo canal en el streaming

## 📈 Mejoras Futuras

- [ ] Soporte para múltiples archivos simultáneos
- [ ] Preview del video antes de subir
- [ ] Selector de calidad (low, medium, high)
- [ ] Edición de metadata (título, descripción)
- [ ] Eliminar videos desde la interfaz
- [ ] Límite de almacenamiento por usuario
- [ ] Autenticación y autorización

## 📧 Soporte

Si tienes problemas con la configuración:
1. Revisa los logs (local o Render)
2. Verifica `/api/upload/status`
3. Consulta esta guía completa

---

**Última actualización**: Diciembre 2025
