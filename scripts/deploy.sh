#!/bin/bash

# Script de deploy para o projeto Voltly
# Este script automatiza o processo de deploy em diferentes ambientes

set -e  # Exit on any error

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

# Função para mostrar ajuda
show_help() {
    echo "Uso: $0 [AMBIENTE]"
    echo ""
    echo "AMBIENTE:"
    echo "  staging    Deploy para ambiente de staging"
    echo "  production Deploy para ambiente de produção"
    echo "  local      Deploy local com Docker Compose"
    echo ""
    echo "Exemplos:"
    echo "  $0 staging"
    echo "  $0 production"
    echo "  $0 local"
}

# Verificar argumentos
if [ $# -eq 0 ]; then
    show_help
    exit 1
fi

ENVIRONMENT=$1

case $ENVIRONMENT in
    "staging")
        log "🚀 Iniciando deploy para STAGING..."
        
        # Aqui você pode adicionar comandos específicos para staging
        # Por exemplo: kubectl apply, docker-compose up, etc.
        
        log "Configurando variáveis de ambiente para staging..."
        export SPRING_PROFILES_ACTIVE=staging
        export SPRING_DATASOURCE_URL=jdbc:postgresql://staging-db:5432/voltly
        export SPRING_DATASOURCE_USERNAME=voltly_staging
        export SPRING_DATASOURCE_PASSWORD=staging_password
        
        log "Executando deploy para staging..."
        # kubectl apply -f k8s/staging/
        # ou
        # docker-compose -f docker-compose.staging.yml up -d
        
        success "Deploy para staging concluído!"
        ;;
        
    "production")
        log "🚀 Iniciando deploy para PRODUÇÃO..."
        
        # Confirmação para produção
        read -p "Tem certeza que deseja fazer deploy em PRODUÇÃO? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log "Deploy cancelado pelo usuário."
            exit 0
        fi
        
        log "Configurando variáveis de ambiente para produção..."
        export SPRING_PROFILES_ACTIVE=production
        export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/voltly
        export SPRING_DATASOURCE_USERNAME=voltly_prod
        export SPRING_DATASOURCE_PASSWORD=prod_password
        
        log "Executando deploy para produção..."
        # kubectl apply -f k8s/production/
        # ou
        # docker-compose -f docker-compose.production.yml up -d
        
        success "Deploy para produção concluído!"
        ;;
        
    "local")
        log "🚀 Iniciando deploy LOCAL..."
        
        # Verificar se Docker está rodando
        if ! docker info > /dev/null 2>&1; then
            error "Docker não está rodando. Por favor, inicie o Docker Desktop."
        fi
        
        log "Parando containers existentes..."
        docker-compose down
        
        log "Construindo e iniciando serviços..."
        docker-compose up --build -d
        
        log "Aguardando serviços ficarem prontos..."
        sleep 30
        
        # Verificar health checks
        if docker-compose exec -T voltly curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            success "Deploy local concluído! Aplicação rodando em http://localhost:8080"
        else
            warning "Deploy local concluído, mas aplicação pode não estar totalmente pronta."
        fi
        ;;
        
    *)
        error "Ambiente inválido: $ENVIRONMENT"
        show_help
        exit 1
        ;;
esac

log "Deploy concluído com sucesso!"
