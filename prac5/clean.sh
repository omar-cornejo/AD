#!/bin/bash

# Script de limpieza del proyecto

echo "🧹 Limpiando proyecto..."

# Eliminar node_modules
if [ -d "node_modules" ]; then
    echo "Eliminando node_modules del servidor..."
    rm -rf node_modules
fi

if [ -d "client/node_modules" ]; then
    echo "Eliminando node_modules del cliente..."
    rm -rf client/node_modules
fi

# Eliminar build del cliente
if [ -d "client/dist" ]; then
    echo "Eliminando build del cliente..."
    rm -rf client/dist
fi

# Eliminar streams (opcional - comentado por seguridad)
# read -p "¿Eliminar streams generados? (s/n): " -n 1 -r
# echo
# if [[ $REPLY =~ ^[Ss]$ ]]; then
#     echo "Eliminando streams..."
#     rm -rf streams/*
# fi

echo "✅ Limpieza completada!"
echo "   Para reinstalar: npm run setup"
