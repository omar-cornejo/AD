# Informe Técnico: Servidor IPTV con Streaming HLS

## 1. Introducción

Este proyecto implementa un sistema completo de servidor IPTV (Internet Protocol Television) capaz de transmitir contenido multimedia utilizando el protocolo HLS (HTTP Live Streaming). El sistema está diseñado con una arquitectura moderna de microservicios, utilizando contenedores Docker para su despliegue y ofreciendo una interfaz de usuario interactiva similar a plataformas de redes sociales como TikTok o Instagram Reels.

El objetivo principal es demostrar la capacidad de procesar, segmentar y distribuir video a través de HTTP de manera eficiente, permitiendo la reproducción adaptativa en diferentes dispositivos y condiciones de red.

## 2. Fundamentos Teóricos

### 2.1. HLS (HTTP Live Streaming)
HLS es un protocolo de comunicación de streaming de medios basado en HTTP desarrollado por Apple. Funciona dividiendo el flujo de video en una secuencia de pequeños archivos basados en HTTP (segmentos `.ts`), cada uno cargando una pequeña parte del video.

**Componentes clave de HLS:**
*   **Playlist (.m3u8):** Un archivo de índice que contiene las URLs de los segmentos de medios. El reproductor descarga primero este archivo para saber qué segmentos reproducir y en qué orden.
*   **Segmentos (.ts):** Archivos de transporte MPEG-2 que contienen el video y audio real. Típicamente tienen una duración de entre 2 a 10 segundos.
*   **Adaptive Bitrate Streaming (ABR):** HLS permite tener múltiples variantes de calidad (360p, 720p, 1080p). El reproductor detecta el ancho de banda del usuario y cambia dinámicamente entre las calidades para evitar cortes (buffering).

### 2.2. FFmpeg
FFmpeg es el motor de procesamiento multimedia utilizado en el backend. Es una colección de software libre que puede grabar, convertir y hacer streaming de audio y video. En este proyecto, FFmpeg se encarga de:
1.  Leer el archivo de video original (mp4, mkv, etc.).
2.  Transcodificar el video a los códecs y bitrates deseados.
3.  Segmentar el video en archivos `.ts`.
4.  Generar el archivo de lista de reproducción `.m3u8`.

### 2.3. WebSocket
Para la funcionalidad de chat en tiempo real, se utiliza el protocolo WebSocket. A diferencia de HTTP (que es unidireccional y sin estado), WebSocket proporciona un canal de comunicación bidireccional y persistente sobre una única conexión TCP. Esto permite que el servidor envíe mensajes a los clientes instantáneamente sin que el cliente tenga que solicitarlos (polling).

## 3. Arquitectura del Sistema

El sistema sigue una arquitectura cliente-servidor desacoplada, contenerizada mediante Docker.

### 3.1. Diagrama de Componentes

```mermaid
graph TD
    Client[Cliente Web (React)] <-->|HTTP/REST| Server[Servidor Express]
    Client <-->|WebSocket| Server
    Server -->|Lectura| FileSystem[Sistema de Archivos]
    FileSystem -->|Streams HLS| Server
    FFmpeg[Proceso de Conversión] -->|Escritura| FileSystem
```

### 3.2. Backend (Node.js + Express)
El servidor actúa como el núcleo del sistema. Sus responsabilidades son:
*   **API REST:** Provee endpoints para listar canales y verificar el estado del servicio.
*   **Servidor de Archivos Estáticos:** Sirve los archivos `.m3u8` y `.ts` necesarios para el streaming.
*   **Servidor de WebSockets:** Gestiona las conexiones de Socket.io para el chat en tiempo real.
*   **Orquestación:** Gestiona la configuración y las rutas.

### 3.3. Frontend (React + Vite)
La interfaz de usuario es una Single Page Application (SPA) construida con React.
*   **Reproductor de Video:** Utiliza `video.js`, una librería robusta compatible con HLS.
*   **Interfaz de Usuario:** Implementa un diseño de "scroll infinito" vertical para cambiar de canal.
*   **Chat:** Componente en tiempo real que se conecta al servidor mediante Socket.io-client.

### 3.4. Infraestructura (Docker)
El proyecto utiliza Docker para garantizar la consistencia entre entornos de desarrollo y producción.
*   **Multi-stage Build:** El `Dockerfile` utiliza múltiples etapas para compilar el frontend y preparar el backend, resultando en una imagen final ligera basada en Alpine Linux.
*   **Volúmenes:** Se utilizan volúmenes para persistir los videos y los streams generados.

## 4. Explicación del Código y Módulos

### 4.1. Conversión de Video (`convert-to-hls.js`)
Este script es crítico para el funcionamiento del sistema.
*   **Análisis:** Usa `ffprobe` para obtener metadatos del video original.
*   **Configuración:** Lee `src/config/ffmpeg.config.js` para determinar los parámetros de codificación (bitrate, resolución, duración de segmentos).
*   **Ejecución:** Lanza un proceso hijo (`spawn`) que ejecuta FFmpeg con los argumentos calculados.
*   **Progreso:** Lee la salida estándar de error (`stderr`) de FFmpeg para calcular y mostrar el porcentaje de progreso en tiempo real.

### 4.2. Servidor Principal (`server.js`)
*   **Configuración de CORS:** Permite peticiones desde el frontend, manejando las diferencias entre desarrollo (localhost) y producción.
*   **Manejo de HLS:** Configura los headers HTTP correctos (`Content-Type`) para archivos `.m3u8` (`application/vnd.apple.mpegurl`) y `.ts` (`video/mp2t`), esenciales para que los navegadores reconozcan el stream.
*   **Socket.IO:** Mantiene un registro de usuarios conectados en memoria (`Map`) y un historial de mensajes efímero.

### 4.3. Rutas de la API (`src/routes/`)
*   **`channels.js`:** Escanea dinámicamente el directorio `streams/`. Identifica cada subdirectorio que contenga un `playlist.m3u8` válido y lo devuelve como un canal disponible. Esto permite agregar contenido simplemente colocando archivos en la carpeta, sin reiniciar el servidor.

### 4.4. Cliente (`client/src/`)
*   **`ReelsView.jsx`:** El componente principal. Maneja el estado de la lista de canales y la navegación. Detecta eventos de teclado (flechas arriba/abajo) y gestos de rueda del ratón para cambiar de canal.
*   **`VideoPlayer.jsx`:** Encapsula `video.js`. Se encarga de inicializar el reproductor HLS, gestionar la limpieza de recursos (dispose) al cambiar de canal para evitar fugas de memoria, y manejar eventos de reproducción/pausa.

## 5. Flujo de Datos

1.  **Inicio:** El servidor arranca y expone el puerto 8080.
2.  **Conversión (Pre-proceso):** Se ejecuta `convert-to-hls.js`. FFmpeg toma un video `.mp4`, lo transcodifica y genera múltiples archivos `.ts` y un `playlist.m3u8` en la carpeta `streams/canal_x`.
3.  **Conexión Cliente:** El usuario accede a la web. React carga y solicita `/api/channels`.
4.  **Selección:** El servidor devuelve la lista de canales basada en las carpetas existentes.
5.  **Streaming:**
    *   El componente `VideoPlayer` solicita el archivo `.m3u8` del canal seleccionado.
    *   El navegador lee el índice y comienza a descargar los segmentos `.ts` secuencialmente.
    *   El video se reproduce.
6.  **Interacción:** Si el usuario envía un mensaje en el chat, se emite un evento Socket.io al servidor, que lo retransmite a todos los clientes conectados.

## 6. Despliegue y Producción

El sistema está optimizado para despliegue en plataformas como Render.com.
*   **Docker:** La imagen incluye Node.js, FFmpeg y todas las dependencias necesarias.
*   **Automatización:** El `Dockerfile` incluye instrucciones para descargar videos de demostración y convertirlos automáticamente durante el proceso de construcción (build), asegurando que el servidor tenga contenido listo nada más arrancar.
*   **Seguridad:** Se ejecuta bajo un usuario no privilegiado (`nodejs`) dentro del contenedor para minimizar riesgos de seguridad.

## 7. Conclusión

El proyecto `prac5` demuestra una implementación robusta y escalable de un servicio de streaming moderno. Combina el procesamiento de bajo nivel de video (FFmpeg) con un servidor web de alto rendimiento (Node.js) y una interfaz de usuario reactiva, cumpliendo con los estándares actuales de la industria para la distribución de contenido multimedia.
