# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

# 1) Instalar la biblioteca compartida en el repositorio Maven local de la imagen
COPY drinks-common/mvnw drinks-common/mvnw
COPY drinks-common/.mvn drinks-common/.mvn
COPY drinks-common/pom.xml drinks-common/pom.xml
COPY drinks-common/src drinks-common/src
RUN cd drinks-common && chmod +x mvnw && ./mvnw -B install -DskipTests

# 2) Compilar el microservicio
COPY inventory-service/mvnw inventory-service/mvnw
COPY inventory-service/.mvn inventory-service/.mvn
COPY inventory-service/pom.xml inventory-service/pom.xml
COPY inventory-service/src inventory-service/src
RUN cd inventory-service && chmod +x mvnw && ./mvnw -B package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
COPY --from=builder /build/inventory-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
