# Etapa 1: Compilar el proyecto con Maven
FROM maven:3.9.10-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copiamos pom.xml y resolvemos dependencias (mejor caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el resto del código fuente
COPY src ./src

# Compilamos el JAR (sin tests para velocidad)
RUN mvn clean package -DskipTests

# Etapa 2: Imagen liviana solo para ejecutar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos el JAR generado en la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
