#!/bin/bash

# Script para compactar o projeto Voltly para entrega
# Este script cria um arquivo ZIP com todos os artefatos necessários

set -e  # Exit on any error

echo "📦 Compactando projeto Voltly para entrega..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log
log() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Verificar se estamos no diretório correto
if [ ! -f pom.xml ]; then
    error "pom.xml não encontrado. Execute este script no diretório raiz do projeto."
fi

# Nome do arquivo de saída
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
OUTPUT_FILE="voltly_devops_${TIMESTAMP}.zip"

log "Criando arquivo de compactação: $OUTPUT_FILE"

# Criar diretório temporário
TEMP_DIR="temp_package"
mkdir -p "$TEMP_DIR"

log "Copiando arquivos do projeto..."

# Copiar arquivos essenciais
cp -r src "$TEMP_DIR/"
cp -r .github "$TEMP_DIR/"
cp -r nginx "$TEMP_DIR/"
cp -r scripts "$TEMP_DIR/"
cp -r docs "$TEMP_DIR/"
cp Dockerfile "$TEMP_DIR/"
cp compose.yaml "$TEMP_DIR/"
cp docker-compose.production.yml "$TEMP_DIR/"
cp docker-compose.staging.yml "$TEMP_DIR/"
cp env.example "$TEMP_DIR/"
cp env.production.example "$TEMP_DIR/"
cp env.staging.example "$TEMP_DIR/"
cp README-DEVOPS.md "$TEMP_DIR/"
cp pom.xml "$TEMP_DIR/"
cp mvnw "$TEMP_DIR/"
cp mvnw.cmd "$TEMP_DIR/"
cp -r .mvn "$TEMP_DIR/"

# Copiar arquivos de configuração se existirem
if [ -f .gitignore ]; then
    cp .gitignore "$TEMP_DIR/"
fi

if [ -f HELP.md ]; then
    cp HELP.md "$TEMP_DIR/"
fi

log "Criando arquivo ZIP..."

# Criar arquivo ZIP
if command -v zip &> /dev/null; then
    zip -r "$OUTPUT_FILE" "$TEMP_DIR"
elif command -v 7z &> /dev/null; then
    7z a "$OUTPUT_FILE" "$TEMP_DIR"
else
    error "Nenhuma ferramenta de compactação encontrada (zip ou 7z)"
fi

# Limpar diretório temporário
rm -rf "$TEMP_DIR"

# Verificar se o arquivo foi criado
if [ -f "$OUTPUT_FILE" ]; then
    FILE_SIZE=$(du -h "$OUTPUT_FILE" | cut -f1)
    success "Projeto compactado com sucesso!"
    echo ""
    echo "📁 Arquivo criado: $OUTPUT_FILE"
    echo "📊 Tamanho: $FILE_SIZE"
    echo ""
    echo "📋 Conteúdo do pacote:"
    echo "   ✅ Código fonte completo"
    echo "   ✅ Dockerfile e docker-compose"
    echo "   ✅ Pipelines CI/CD (GitHub Actions)"
    echo "   ✅ Scripts de automação"
    echo "   ✅ Documentação DevOps"
    echo "   ✅ Configurações de ambiente"
    echo "   ✅ Nginx configuration"
    echo ""
    echo "🚀 Pronto para entrega!"
else
    error "Falha ao criar arquivo ZIP"
fi
