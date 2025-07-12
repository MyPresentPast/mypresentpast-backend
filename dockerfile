# Etapa 1: Imagen base con JDK para compilar
FROM eclipse-temurin:17-jdk-alpine as builder

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR compilado desde fuera del contenedor
COPY target/*.jar app.jar

# Etapa 2: Imagen mínima para ejecutar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar desde la etapa de construcción
COPY --from=builder /app/app.jar .

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
