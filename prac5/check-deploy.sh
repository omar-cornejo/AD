#!/bin/bash

echo "🔍 Verificación Pre-Deploy para Render.com"
echo "=========================================="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Contadores
checks_passed=0
checks_failed=0

# Función para verificar
check() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
        ((checks_passed++))
    else
        echo -e "${RED}❌ $2${NC}"
        ((checks_failed++))
    fi
}

echo -e "${BLUE}📁 Verificando archivos...${NC}"

# Verificar archivos esenciales
[ -f "render.yaml" ]
check $? "render.yaml existe"

[ -f "Dockerfile" ]
check $? "Dockerfile existe"

[ -f "package.json" ]
check $? "package.json existe"

[ -f "server.js" ]
check $? "server.js existe"

[ -f ".dockerignore" ]
check $? ".dockerignore existe"

[ -f ".gitignore" ]
check $? ".gitignore existe"

[ -f "client/package.json" ]
check $? "client/package.json existe"

echo ""
echo -e "${BLUE}🔧 Verificando configuración...${NC}"

# Verificar que render.yaml tiene rootDir
if grep -q "rootDir: prac5" render.yaml; then
    check 0 "render.yaml tiene rootDir configurado"
else
    check 1 "render.yaml NECESITA rootDir: prac5"
fi

# Verificar health check en server.js
if grep -q "/api/health" server.js; then
    check 0 "Health check endpoint existe"
else
    check 1 "Falta health check endpoint"
fi

# Verificar script de build en package.json
if grep -q '"build":' package.json; then
    check 0 "Script de build configurado"
else
    check 1 "Falta script de build"
fi

echo ""
echo -e "${BLUE}📦 Verificando dependencias...${NC}"

# Verificar que node_modules no está en git
if [ -d "node_modules" ] && ! grep -q "node_modules" .gitignore; then
    check 1 "node_modules NO está en .gitignore"
else
    check 0 "node_modules correctamente ignorado"
fi

# Verificar que videos grandes no están en git
if grep -q "videos/\*" .gitignore; then
    check 0 "Videos ignorados en git"
else
    check 1 "Videos NO están en .gitignore"
fi

echo ""
echo -e "${BLUE}🔗 Verificando git...${NC}"

# Verificar git remoto
if git remote -v | grep -q "github.com"; then
    check 0 "Repositorio de GitHub configurado"
else
    check 1 "No hay repositorio remoto configurado"
fi

# Verificar estado de git
if git diff-index --quiet HEAD --; then
    check 0 "No hay cambios sin commitear"
else
    echo -e "${YELLOW}⚠️  Hay cambios sin commitear${NC}"
fi

echo ""
echo "=========================================="
echo -e "${BLUE}📊 Resumen:${NC}"
echo -e "${GREEN}✅ Verificaciones pasadas: $checks_passed${NC}"
echo -e "${RED}❌ Verificaciones fallidas: $checks_failed${NC}"
echo "=========================================="
echo ""

if [ $checks_failed -eq 0 ]; then
    echo -e "${GREEN}🎉 ¡Todo listo para deploy en Render!${NC}"
    echo ""
    echo -e "${BLUE}Próximos pasos:${NC}"
    echo "1. git add ."
    echo "2. git commit -m 'Preparado para Render'"
    echo "3. git push origin main"
    echo "4. Ir a https://dashboard.render.com y crear Web Service"
    echo ""
    exit 0
else
    echo -e "${RED}⚠️  Hay problemas que resolver antes del deploy${NC}"
    echo ""
    exit 1
fi
