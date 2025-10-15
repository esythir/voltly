#!/bin/bash

# Script de build para o projeto Voltly
# Este script automatiza o processo de build e deploy

set -e  # Exit on any error

echo "🚀 Iniciando build do projeto Voltly..."

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

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    error "Docker não está rodando. Por favor, inicie o Docker Desktop."
fi

# Verificar se Docker Compose está disponível
if ! command -v docker-compose &> /dev/null; then
    error "Docker Compose não está instalado."
fi

log "Verificando arquivos de configuração..."

# Verificar se arquivo .env existe
if [ ! -f .env ]; then
    warning "Arquivo .env não encontrado. Copiando de env.example..."
    cp env.example .env
    warning "Por favor, edite o arquivo .env com suas configurações."
fi

# Verificar se Dockerfile existe
if [ ! -f Dockerfile ]; then
    error "Dockerfile não encontrado."
fi

# Verificar se docker-compose.yml existe
if [ ! -f compose.yaml ]; then
    error "compose.yaml não encontrado."
fi

log "Parando containers existentes..."
docker-compose down

log "Removendo imagens antigas..."
docker-compose down --rmi all

log "Construindo imagens Docker..."
docker-compose build --no-cache

log "Iniciando serviços..."
docker-compose up -d

log "Aguardando serviços ficarem prontos..."
sleep 30

# Verificar se os serviços estão rodando
log "Verificando status dos serviços..."
docker-compose ps

# Verificar health checks
log "Verificando health checks..."
if docker-compose exec -T voltly curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    success "Aplicação Voltly está rodando e saudável!"
else
    warning "Aplicação pode não estar totalmente pronta ainda."
fi

if docker-compose exec -T postgres pg_isready -U voltly_user -d voltly > /dev/null 2>&1; then
    success "Banco de dados PostgreSQL está rodando!"
else
    warning "Banco de dados pode não estar totalmente pronto ainda."
fi

log "Build concluído!"
echo ""
echo "🌐 Aplicação disponível em:"
echo "   - Voltly API: http://localhost:8080"
echo "   - Health Check: http://localhost:8080/actuator/health"
echo "   - Nginx Proxy: http://localhost:80"
echo ""
echo "📊 Para ver logs:"
echo "   - docker-compose logs -f voltly"
echo "   - docker-compose logs -f postgres"
echo ""
echo "🛑 Para parar:"
echo "   - docker-compose down"
