# Multi-stage build para otimizar o tamanho da imagem final
FROM maven:3.9.7-eclipse-temurin-21 AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven primeiro (para cache de dependências)
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Baixar dependências (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte
COPY src src

# Build da aplicação (pulando testes para evitar problemas de conexão com BD)
RUN ./mvnw clean package -DskipTests -B

# Estágio de runtime - imagem mais leve
FROM eclipse-temurin:21-jre-alpine

# Instalar curl para health checks
RUN apk add --no-cache curl

# Criar usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR do estágio de build
COPY --from=build /app/target/voltly-0.0.1-SNAPSHOT.jar app.jar

# Alterar ownership para o usuário não-root
RUN chown -R appuser:appgroup /app

# Mudar para usuário não-root
USER appuser

# Expor porta da aplicação
EXPOSE 8080

# Variáveis de ambiente (serão sobrescritas pelo docker-compose)
ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_DATASOURCE_URL=""
ENV SPRING_DATASOURCE_USERNAME=""
ENV SPRING_DATASOURCE_PASSWORD=""
ENV SERVER_PORT=8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
