#!/bin/bash

echo "🎨 Preparando proyecto para Render.com..."
echo ""

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}📦 Verificando configuración...${NC}"

# Verificar archivos necesarios
if [ ! -f "render.yaml" ]; then
    echo -e "${YELLOW}⚠️  render.yaml no encontrado${NC}"
    exit 1
fi

if [ ! -f "Dockerfile" ]; then
    echo -e "${YELLOW}⚠️  Dockerfile no encontrado${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Archivos de configuración OK${NC}"
echo ""

echo -e "${BLUE}📋 Pasos para deployear en Render.com:${NC}"
echo ""
echo "1️⃣  Sube tu código a GitHub:"
echo "   ${YELLOW}git init${NC}"
echo "   ${YELLOW}git add .${NC}"
echo "   ${YELLOW}git commit -m 'Deploy to Render'${NC}"
echo "   ${YELLOW}git branch -M main${NC}"
echo "   ${YELLOW}git remote add origin https://github.com/tu-usuario/tu-repo.git${NC}"
echo "   ${YELLOW}git push -u origin main${NC}"
echo ""

echo "2️⃣  Crear Web Service en Render:"
echo "   • Ve a: ${BLUE}https://dashboard.render.com${NC}"
echo "   • Click en 'New +' → 'Web Service'"
echo "   • Conecta tu repositorio de GitHub"
echo "   • Selecciona tu repositorio"
echo ""

echo "3️⃣  Configuración automática:"
echo "   ${GREEN}✅ Render detectará automáticamente render.yaml${NC}"
echo "   ${GREEN}✅ Environment: Docker${NC}"
echo "   ${GREEN}✅ Variables de entorno configuradas${NC}"
echo "   ${GREEN}✅ Health check: /api/health${NC}"
echo ""

echo "4️⃣  Deploy:"
echo "   • Click en 'Create Web Service'"
echo "   • Espera 5-10 minutos"
echo "   • Tu app estará en: ${BLUE}https://tu-servicio.onrender.com${NC}"
echo ""

echo -e "${YELLOW}💡 Tips:${NC}"
echo "   • El plan Free duerme después de 15 min sin tráfico"
echo "   • Primera request tarda ~30-60 seg en despertar"
echo "   • Usa UptimeRobot para mantener activo (opcional)"
echo "   • WebSocket funciona nativamente"
echo ""

echo -e "${GREEN}📚 Documentación completa: ${BLUE}./RENDER-DEPLOY.md${NC}"
echo ""
