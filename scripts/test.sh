#!/bin/bash

# Script de testes para o projeto Voltly
# Este script executa todos os testes da aplicação

set -e  # Exit on any error

echo "🧪 Executando testes do projeto Voltly..."

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

# Verificar se Maven está disponível
if ! command -v ./mvnw &> /dev/null; then
    error "Maven wrapper não encontrado."
fi

log "Executando testes unitários..."
./mvnw test

if [ $? -eq 0 ]; then
    success "Testes unitários passaram!"
else
    error "Testes unitários falharam!"
fi

log "Executando testes de integração..."
./mvnw verify -P integration-tests

if [ $? -eq 0 ]; then
    success "Testes de integração passaram!"
else
    warning "Testes de integração falharam ou não estão configurados."
fi

log "Gerando relatório de cobertura..."
./mvnw jacoco:report

if [ $? -eq 0 ]; then
    success "Relatório de cobertura gerado!"
    log "Relatório disponível em: target/site/jacoco/index.html"
else
    warning "Falha ao gerar relatório de cobertura."
fi

log "Executando análise de qualidade de código..."
./mvnw spotbugs:check

if [ $? -eq 0 ]; then
    success "Análise de qualidade passou!"
else
    warning "Análise de qualidade encontrou problemas."
fi

log "Executando verificação de dependências..."
./mvnw org.owasp:dependency-check-maven:check

if [ $? -eq 0 ]; then
    success "Verificação de dependências passou!"
else
    warning "Verificação de dependências encontrou vulnerabilidades."
fi

log "Todos os testes foram executados!"
echo ""
echo "📊 Relatórios gerados:"
echo "   - Cobertura: target/site/jacoco/index.html"
echo "   - SpotBugs: target/spotbugsXml.xml"
echo "   - Dependency Check: target/dependency-check-report.html"
echo ""
echo "✅ Testes concluídos com sucesso!"
