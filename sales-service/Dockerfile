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
COPY sales-service/mvnw sales-service/mvnw
COPY sales-service/.mvn sales-service/.mvn
COPY sales-service/pom.xml sales-service/pom.xml
COPY sales-service/src sales-service/src
RUN cd sales-service && chmod +x mvnw && ./mvnw -B package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
COPY --from=builder /build/sales-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
