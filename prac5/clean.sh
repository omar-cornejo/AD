#!/bin/bash

echo "Limpiando proyecto..."

if [ -d "node_modules" ]; then
    echo "Eliminando node_modules del servidor..."
    rm -rf node_modules
fi

if [ -d "client/node_modules" ]; then
    echo "Eliminando node_modules del cliente..."
    rm -rf client/node_modules
fi

if [ -d "client/dist" ]; then
    echo "Eliminando build del cliente..."
    rm -rf client/dist
fi

echo "Limpieza completada"
echo "Para reinstalar: npm run setup"
