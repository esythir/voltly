# estágio de build
FROM maven:3.9.7-eclipse-temurin-21 AS build

WORKDIR /app

# copie somente o pom e o wrapper primeiro para cache de dependências
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# copie o código e gere o JAR (pulando os testes, senão vai dar erro de conexão no Flyway durante o build)
COPY src src
RUN ./mvnw clean package -DskipTests -B

# estágio de runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# pegue o JAR do primeiro estágio
COPY --from=build /app/target/voltly-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# essas variáveis serão injetadas pelo docker-compose
ENV SPRING_DATASOURCE_URL=""
ENV SPRING_DATASOURCE_USERNAME=""
ENV SPRING_DATASOURCE_PASSWORD=""

ENTRYPOINT ["java","-jar","app.jar"]
