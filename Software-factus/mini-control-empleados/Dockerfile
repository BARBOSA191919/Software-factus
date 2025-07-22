# Etapa de compilación
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia pom.xml y descarga dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el resto del código fuente
COPY src ./src

# Construye el proyecto
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17
WORKDIR /app

# Copiar el jar generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Puerto de Railway o por defecto
ENV PORT=8080
EXPOSE ${PORT}

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
