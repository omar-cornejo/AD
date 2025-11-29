#!/bin/bash

# Script de setup rápido para el proyecto

echo "🚀 Configurando proyecto IPTV HLS Server..."

# Verificar Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js no está instalado. Por favor instálalo primero."
    exit 1
fi

echo "✅ Node.js $(node -v) detectado"

# Verificar FFmpeg
if ! command -v ffmpeg &> /dev/null; then
    echo "⚠️  FFmpeg no está instalado."
    echo "   Instálalo con: sudo apt install ffmpeg (Ubuntu/Debian)"
    exit 1
fi

echo "✅ FFmpeg $(ffmpeg -version | head -n1 | cut -d' ' -f3) detectado"

# Instalar dependencias
echo "📦 Instalando dependencias del servidor..."
npm install

echo "📦 Instalando dependencias del cliente..."
cd client && npm install && cd ..

# Crear directorios necesarios
echo "📁 Creando directorios..."
mkdir -p streams videos

echo ""
echo "✅ Setup completado!"
echo ""
echo "📖 Próximos pasos:"
echo "   1. Coloca tus videos en la carpeta 'videos/'"
echo "   2. Conviértelos: npm run convert -- videos/tu_video.mp4 mi_canal source"
echo "   3. Inicia el servidor: npm run dev"
echo ""
