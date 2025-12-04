# Instrucciones para Subir Videos

## Opción 1: Google Drive (Recomendado)

1. Sube tus videos a Google Drive
2. Haz clic derecho → "Compartir" → "Cualquiera con el enlace"
3. Copia el ID del enlace: `https://drive.google.com/file/d/ESTE_ES_EL_ID/view`
4. Actualiza el Dockerfile con los IDs

## Opción 2: Dropbox

1. Sube los videos a Dropbox
2. Obtén el enlace de descarga directa
3. Cambia `?dl=0` por `?dl=1` al final del enlace
4. Usa ese enlace en el Dockerfile

## Opción 3: GitHub Releases

1. Crea un Release en GitHub
2. Adjunta los videos como Assets
3. Obtén la URL de descarga
4. Usa esa URL en el Dockerfile

## Ejemplo de URLs en Dockerfile:

```dockerfile
RUN curl -L "URL_DIRECTA_VIDEO1" -o videos/video1.mp4 && \
    curl -L "URL_DIRECTA_VIDEO2" -o videos/video2.mp4
```
