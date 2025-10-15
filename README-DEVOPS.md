# Voltly - Plataforma IoT para Gestão Energética

## 📋 Sobre o Projeto

O **Voltly** é uma plataforma IoT para gestão energética com monitoramento de consumo em tempo real e desligamento remoto de dispositivos via microserviço RESTful. A aplicação foi desenvolvida em Java Spring Boot e implementa práticas completas de DevOps para automatizar todo o ciclo de vida da aplicação.

## 🚀 Como Executar Localmente com Docker

### Pré-requisitos
- Docker Desktop instalado
- Docker Compose instalado
- Git instalado

### Passos para Execução

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd voltly
   ```

2. **Configure as variáveis de ambiente**
   ```bash
   cp env.example .env
   # Edite o arquivo .env conforme necessário
   ```

3. **Execute a aplicação com Docker Compose**
   ```bash
   # Build e execução de todos os serviços
   docker-compose up --build
   
   # Ou em modo detached (background)
   docker-compose up --build -d
   ```

4. **Verifique se os serviços estão rodando**
   ```bash
   docker-compose ps
   ```

5. **Acesse a aplicação**
   - **Aplicação**: http://localhost:8080
   - **API Health Check**: http://localhost:8080/actuator/health
   - **Nginx Proxy**: http://localhost:80
   - **Banco PostgreSQL**: localhost:5432

### Comandos Úteis

```bash
# Parar todos os serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v

# Ver logs da aplicação
docker-compose logs -f voltly

# Executar testes
docker-compose exec voltly ./mvnw test

# Acessar o banco de dados
docker-compose exec postgres psql -U voltly_user -d voltly
```

## 🔄 Pipeline CI/CD

### Ferramentas Utilizadas
- **GitHub Actions**: Plataforma de CI/CD
- **Docker**: Containerização da aplicação
- **PostgreSQL**: Banco de dados para testes
- **CodeQL**: Análise de segurança do código
- **Trivy**: Scan de vulnerabilidades em imagens Docker

### Etapas do Pipeline

#### 1. **Job: test-and-build**
- ✅ Checkout do código
- ✅ Configuração do Java 21
- ✅ Cache de dependências Maven
- ✅ Execução de testes unitários
- ✅ Execução de testes de integração
- ✅ Geração de relatório de cobertura
- ✅ Build da aplicação
- ✅ Upload de artefatos

#### 2. **Job: build-and-push**
- ✅ Build da imagem Docker
- ✅ Push para GitHub Container Registry
- ✅ Cache de layers Docker
- ✅ Tags automáticas baseadas em branch/commit

#### 3. **Job: deploy-staging**
- ✅ Deploy automático para ambiente de staging (branch develop)

#### 4. **Job: deploy-production**
- ✅ Deploy automático para ambiente de produção (branch main)

#### 5. **Job: notify**
- ✅ Notificações de sucesso/falha do pipeline

### Triggers do Pipeline
- **Push** para branches `main` e `develop`
- **Pull Request** para branch `main`
- **Schedule**: Execução semanal para security scans

## 🐳 Containerização

### Dockerfile

O Dockerfile utiliza uma estratégia de **multi-stage build** para otimizar o tamanho da imagem final:

```dockerfile
# Estágio 1: Build
FROM maven:3.9.7-eclipse-temurin-21 AS build
# ... configurações de build

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre-alpine
# ... configurações de runtime
```

### Estratégias Adotadas

1. **Multi-stage Build**: Separação entre ambiente de build e runtime
2. **Cache de Dependências**: Otimização do build com cache de layers
3. **Usuário Não-root**: Execução com usuário específico para segurança
4. **Health Checks**: Verificação automática de saúde da aplicação
5. **Imagem Alpine**: Base leve para reduzir tamanho da imagem

### Docker Compose

O `docker-compose.yml` orquestra os seguintes serviços:

- **voltly**: Aplicação Spring Boot
- **postgres**: Banco de dados PostgreSQL
- **nginx**: Proxy reverso (opcional)

### Configurações de Rede e Volumes

- **Rede**: `voltly-network` (bridge)
- **Volumes**: Persistência de dados do PostgreSQL
- **Health Checks**: Verificação de saúde dos serviços
- **Dependências**: Orquestração de inicialização

## 📊 Prints do Funcionamento

### Pipeline CI/CD em Execução

![Pipeline CI/CD](docs/images/pipeline-execution.png)
*Pipeline executando com sucesso - todas as etapas passaram*

### Deploy em Staging

![Staging Environment](docs/images/staging-deploy.png)
*Aplicação rodando no ambiente de staging*

### Deploy em Produção

![Production Environment](docs/images/production-deploy.png)
*Aplicação rodando no ambiente de produção*

### Health Checks

![Health Checks](docs/images/health-checks.png)
*Verificação de saúde da aplicação funcionando*

### Docker Containers

![Docker Containers](docs/images/docker-containers.png)
*Containers rodando localmente com Docker Compose*

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21**: Linguagem de programação
- **Spring Boot 3.4.5**: Framework principal
- **Spring Security**: Autenticação e autorização
- **Spring Data JPA**: Persistência de dados
- **Spring Validation**: Validação de dados
- **Flyway**: Migração de banco de dados
- **JWT**: Autenticação stateless

### Banco de Dados
- **PostgreSQL 15**: Banco de dados principal
- **Oracle Database**: Banco de dados de produção (FIAP)

### DevOps & Infraestrutura
- **Docker**: Containerização
- **Docker Compose**: Orquestração local
- **GitHub Actions**: CI/CD
- **Nginx**: Proxy reverso
- **Maven**: Gerenciamento de dependências

### Ferramentas de Qualidade
- **JaCoCo**: Cobertura de testes
- **CodeQL**: Análise de segurança
- **Trivy**: Scan de vulnerabilidades
- **OWASP Dependency Check**: Verificação de dependências

### Monitoramento
- **Spring Actuator**: Health checks e métricas
- **Docker Health Checks**: Verificação de saúde dos containers

## 📁 Estrutura do Projeto

```
voltly/
├── .github/workflows/          # Pipelines CI/CD
│   ├── ci-cd.yml              # Pipeline principal
│   └── security-scan.yml      # Scans de segurança
├── nginx/                     # Configurações do Nginx
│   └── nginx.conf
├── src/                       # Código fonte
│   ├── main/java/            # Código Java
│   └── main/resources/       # Recursos e configurações
├── Dockerfile                 # Imagem Docker
├── compose.yaml              # Orquestração Docker
├── env.example               # Variáveis de ambiente
└── README.md                 # Documentação
```

## 🔧 Configurações de Ambiente

### Variáveis de Ambiente Principais

```bash
# Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/voltly
SPRING_DATASOURCE_USERNAME=voltly_user
SPRING_DATASOURCE_PASSWORD=voltly_password

# Aplicação
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8080

# Segurança
SECURITY_JWT_SECRET=my.super.secret.jwt.passphrase.for.docker
```

## 🚨 Troubleshooting

### Problemas Comuns

1. **Erro de conexão com banco**
   ```bash
   # Verificar se o PostgreSQL está rodando
   docker-compose ps postgres
   
   # Ver logs do banco
   docker-compose logs postgres
   ```

2. **Porta já em uso**
   ```bash
   # Verificar processos usando a porta
   netstat -tulpn | grep :8080
   
   # Parar containers existentes
   docker-compose down
   ```

3. **Problemas de build**
   ```bash
   # Limpar cache do Docker
   docker system prune -a
   
   # Rebuild sem cache
   docker-compose build --no-cache
   ```

## 📝 Checklist de Entrega

- [x] **Projeto compactado em .ZIP com estrutura organizada**
- [x] **Dockerfile funcional**
- [x] **docker-compose.yml ou arquivos Kubernetes**
- [x] **Pipeline com etapas de build, teste e deploy**
- [x] **README.md com instruções e prints**

## 👥 Equipe

- **Desenvolvedor**: [Seu Nome]
- **Projeto**: Voltly - Plataforma IoT para Gestão Energética
- **Curso**: FIAP - DevOps

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais no contexto do curso FIAP.

---

**Voltly** - Transformando a gestão energética através da tecnologia IoT 🚀
