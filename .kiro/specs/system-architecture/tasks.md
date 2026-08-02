# Plan de Implementación: Arquitectura General del Sistema

## Visión General

Este plan cubre la implementación de la infraestructura arquitectónica completa del Sistema de Gestión de Ventas e Inventario de Bar. Incluye la biblioteca compartida (drinks-common), la infraestructura Docker, el scaffolding de microservicios, la seguridad JWT, los cross-cutting concerns, el esqueleto del frontend Angular y los tests de validación.

## Tareas

- [x] 1. Crear biblioteca compartida drinks-common
  - [x] 1.1 Crear proyecto Maven drinks-common con pom.xml y estructura de paquetes
    - Crear directorio `drinks-common/` con `pom.xml` (parent: spring-boot-starter-parent 4.1.0, groupId: drinks.system, artifactId: drinks-common, versión 1.0.0-SNAPSHOT)
    - Agregar dependencias: spring-boot-starter-webmvc, spring-boot-starter-security, jjwt-api/impl/jackson 0.12.6, logstash-logback-encoder 7.4, lombok
    - Crear estructura de paquetes: `drinks.system.common.security`, `drinks.system.common.exception`, `drinks.system.common.dto`, `drinks.system.common.logging`, `drinks.system.common.client`, `drinks.system.common.audit`
    - _Requisitos: 8.4, 8.5_

  - [x] 1.2 Implementar JwtTokenProvider y SecurityConstants
    - Crear `SecurityConstants.java` con constantes (AUTHORIZATION_HEADER, TOKEN_PREFIX, SECRET_KEY property name)
    - Crear `JwtTokenProvider.java` con métodos: generateToken(userId, username, branchId, permissions), validateToken(token), getClaims(token)
    - Utilizar HMAC-SHA256 con clave secreta configurable vía properties
    - Claims: sub (userId), username, branchId, permissions (lista), iat, exp (15 min configurable)
    - _Requisitos: 4.1, 4.2, 4.8_

  - [x] 1.3 Implementar JwtAuthenticationFilter y UserPrincipal
    - Crear `UserPrincipal.java` (record) que implemente UserDetails con userId, username, branchId, permissions y método hasPermission()
    - Crear `JwtAuthenticationFilter.java` extendiendo OncePerRequestFilter: extraer token del header, validar firma y expiración, construir UserPrincipal, setear SecurityContext, agregar userId al MDC
    - Si no hay token: continuar cadena sin autenticación; si token inválido: retornar 401 con ErrorResponse
    - _Requisitos: 4.4, 4.8, 4.10, 4.11_

  - [x] 1.4 Implementar RequiresPermission (anotación de autorización)
    - Crear anotación `@RequiresPermission(value = "PERMISSION_NAME")` para uso en controllers
    - Crear `PermissionAspect.java` o `PermissionInterceptor.java` que verifique si el UserPrincipal tiene el permiso requerido
    - Si no tiene permiso: retornar 403 Forbidden con ErrorResponse
    - _Requisitos: 4.4, 4.11_

  - [x] 1.5 Implementar jerarquía de excepciones y GlobalExceptionHandler
    - Crear `BaseException.java` (abstract), `ResourceNotFoundException.java` (404), `BusinessConflictException.java` (409), `ValidationException.java` (400), `UnauthorizedException.java` (401), `ForbiddenException.java` (403)
    - Crear `ErrorResponse.java` (record con builder: timestamp, status, error, message, path, correlationId, fieldErrors)
    - Crear `GlobalExceptionHandler.java` (@RestControllerAdvice) con handlers para cada excepción: ResourceNotFound→404, BusinessConflict→409, MethodArgumentNotValid→400 con fieldErrors, Exception genérica→500 con mensaje genérico
    - _Requisitos: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7_

  - [x] 1.6 Implementar CorrelationIdFilter y logging
    - Crear `MdcConstants.java` con claves: CORRELATION_ID, USER_ID, SERVICE_NAME
    - Crear `CorrelationIdFilter.java` (OncePerRequestFilter, orden HIGHEST_PRECEDENCE): leer header X-Correlation-ID, generar UUID si ausente, setear en MDC y response header, limpiar en finally
    - Crear `LoggingInterceptor.java` para log de entrada/salida de requests (método, path, status, duración)
    - _Requisitos: 7.1, 7.2, 3.8_

  - [x] 1.7 Implementar DTOs compartidos (PageResponse, ApiResponse)
    - Crear `PageResponse.java` (record genérico con: content, page, size, totalElements, totalPages, first, last)
    - Crear `ApiResponse.java` (record genérico con: data, message, timestamp)
    - _Requisitos: 8.4_

  - [x] 1.8 Implementar BaseRestClient con retry y propagación de headers
    - Crear `RetryConfig.java` con constantes: MAX_RETRIES=2, INITIAL_BACKOFF=500ms
    - Crear `BaseRestClient.java` (abstract) con método executeWithRetry: propagación automática de JWT (del SecurityContext) y Correlation_ID (del MDC), retry con backoff exponencial para HTTP 5xx, timeout de 30s
    - _Requisitos: 5.5, 5.6_

  - [x] 1.9 Implementar interfaces de auditoría
    - Crear `AuditEvent.java` (record: userId, username, action, module, entityName, entityId, oldValues, newValues, ipAddress, description)
    - Crear `Auditable.java` (interfaz marcadora para entidades auditables)
    - _Requisitos: 7.3_

  - [x] 1.10 Instalar drinks-common en el repositorio Maven local
    - Ejecutar `mvn install -DskipTests` en el directorio drinks-common para publicar el JAR en el repositorio local
    - Verificar que el artefacto `drinks.system:drinks-common:1.0.0-SNAPSHOT` está disponible
    - _Requisitos: 8.4, 8.5_

- [~] 2. Checkpoint - Verificar compilación de drinks-common
  - Asegurar que `mvn compile` ejecuta sin errores en drinks-common, preguntar al usuario si surgen dudas.

- [ ] 3. Configurar infraestructura Docker
  - [x] 3.1 Crear archivo Docker Compose completo
    - Crear `docker-compose.yml` en la raíz del proyecto con los 6 servicios: postgres-drinks (PostgreSQL 16-alpine), nginx-gateway (Nginx 1.25-alpine), access-service, sales-service, inventory-service, reporting-service
    - Configurar red interna `drinks-network`, volumen `drinks_data`, health checks con dependencias (condition: service_healthy)
    - Exponer puertos: 8080 (gateway), 5432 (postgres opcional), variables de entorno desde `.env`
    - _Requisitos: 10.1, 10.3, 10.4, 10.5, 10.6, 10.7_

  - [x] 3.2 Crear configuración de Nginx (API Gateway)
    - Crear `docker/nginx/nginx.conf` con: worker_processes auto, log_format JSON, timeouts (connect 10s, send/read 30s)
    - Configurar upstreams (access-service:8081, sales-service:8082, inventory-service:8083, reporting-service:8084)
    - Configurar CORS con variable `${FRONTEND_ORIGIN}`, manejo de OPTIONS preflight
    - Configurar routing por prefijo: /api/access/, /api/sales/, /api/inventory/, /api/reporting/
    - Agregar generación de X-Correlation-ID ($request_id), endpoint /health y página de error 504
    - _Requisitos: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9_

  - [~] 3.3 Crear Dockerfile multi-stage (patrón compartido)
    - Crear `Dockerfile` en cada directorio de servicio (access-service/, sales-service/, inventory-service/, reporting-service/)
    - Stage 1 (builder): eclipse-temurin:17-jdk-alpine, copiar pom.xml + mvnw, dependency:go-offline, copiar src, package -DskipTests
    - Stage 2 (runtime): eclipse-temurin:17-jre-alpine, usuario no-root (appuser), copiar JAR, HEALTHCHECK con wget, EXPOSE puerto configurable
    - _Requisitos: 10.2_

  - [x] 3.4 Crear archivo .env con todas las variables del sistema
    - Crear `.env.example` con todas las variables documentadas: DB (host, port, name, admin, usuarios por servicio), JWT (secret, expiration), HikariCP (pool size), Frontend origin, URLs inter-servicio
    - Verificar que `.env` ya existe y contiene los valores necesarios (sin sobreescribir secretos)
    - _Requisitos: 9.1, 9.5_

- [~] 4. Checkpoint - Verificar infraestructura Docker
  - Asegurar que la configuración Docker es correcta sintácticamente, preguntar al usuario si surgen dudas.

- [ ] 5. Scaffolding de microservicios backend
  - [x] 5.1 Crear estructura de proyecto para sales-service
    - Crear `sales-service/pom.xml` con dependencias: spring-boot-starter-webmvc, spring-boot-starter-data-jpa, spring-boot-starter-security, spring-boot-starter-validation, spring-boot-starter-actuator, postgresql, drinks-common, springdoc-openapi, lombok
    - Crear estructura de paquetes completa: `drinks.system.salesservice.domain.model`, `.domain.port.in`, `.domain.port.out`, `.domain.exception`, `.application.service`, `.application.dto.request`, `.application.dto.response`, `.application.mapper`, `.infrastructure.adapter.in.rest`, `.infrastructure.adapter.out.persistence.entity`, `.infrastructure.adapter.out.persistence.repository`, `.infrastructure.adapter.out.persistence.adapter`, `.infrastructure.adapter.out.client`, `.infrastructure.config`
    - Crear `SalesServiceApplication.java` con @SpringBootApplication
    - _Requisitos: 8.1, 8.2, 8.3, 8.5, 8.6_

  - [x] 5.2 Crear estructura de proyecto para inventory-service
    - Crear `inventory-service/pom.xml` con mismas dependencias que sales-service
    - Crear estructura de paquetes completa: `drinks.system.inventoryservice.domain.model`, `.domain.port.in`, `.domain.port.out`, `.domain.exception`, `.application.service`, `.application.dto.request`, `.application.dto.response`, `.application.mapper`, `.infrastructure.adapter.in.rest`, `.infrastructure.adapter.out.persistence.entity`, `.infrastructure.adapter.out.persistence.repository`, `.infrastructure.adapter.out.persistence.adapter`, `.infrastructure.config`
    - Crear `InventoryServiceApplication.java` con @SpringBootApplication
    - _Requisitos: 8.1, 8.2, 8.3, 8.5, 8.6_

  - [-] 5.3 Crear estructura de proyecto para reporting-service
    - Crear `reporting-service/pom.xml` con dependencias similares + exportación (poi para Excel, itextpdf para PDF)
    - Crear estructura de paquetes completa: `drinks.system.reportingservice.domain.model`, `.domain.port.in`, `.domain.port.out`, `.domain.exception`, `.application.service`, `.application.dto.request`, `.application.dto.response`, `.application.mapper`, `.infrastructure.adapter.in.rest`, `.infrastructure.adapter.out.persistence.entity`, `.infrastructure.adapter.out.persistence.repository`, `.infrastructure.adapter.out.persistence.adapter`, `.infrastructure.config`
    - Crear `ReportingServiceApplication.java` con @SpringBootApplication
    - _Requisitos: 8.1, 8.2, 8.3, 8.5, 8.6_

  - [-] 5.4 Actualizar access-service existente: agregar dependencia drinks-common y ajustar estructura
    - Agregar dependencia `drinks-common` al `pom.xml` existente del access-service
    - Verificar/crear paquetes faltantes según la arquitectura hexagonal: `domain.port.in`, `domain.port.out`, `application.dto.request`, `application.dto.response`, `application.mapper`, `infrastructure.adapter.in.rest`, `infrastructure.adapter.out.persistence`
    - _Requisitos: 8.3, 8.5_

- [ ] 6. Configurar Spring Boot para cada servicio
  - [~] 6.1 Crear archivos de configuración para sales-service
    - Crear `application.yml`: nombre del servicio, datasource con HikariCP, JPA con schema=sales y ddl-auto=validate, server.port=8082, configuración de servicios externos (inventory URL), JWT secret, actuator endpoints
    - Crear `application-dev.yml`: valores por defecto para desarrollo local (localhost, passwords dev)
    - Crear `application-prod.yml`: sin valores por defecto, error sin stacktrace
    - Crear `logback-spring.xml`: perfiles dev (PLAIN_CONSOLE) y prod (JSON_CONSOLE con logstash encoder)
    - _Requisitos: 9.1, 9.2, 9.3, 9.4, 9.6, 7.1_

  - [~] 6.2 Crear archivos de configuración para inventory-service
    - Crear `application.yml`: datasource con schema=inventory, server.port=8083, JWT secret, actuator
    - Crear `application-dev.yml`, `application-prod.yml`, `logback-spring.xml` (mismo patrón que sales-service)
    - _Requisitos: 9.1, 9.2, 9.3, 9.4, 9.6, 7.1_

  - [~] 6.3 Crear archivos de configuración para reporting-service
    - Crear `application.yml`: datasource con schema=reporting, server.port=8084, JWT secret, actuator
    - Crear `application-dev.yml`, `application-prod.yml`, `logback-spring.xml` (mismo patrón)
    - _Requisitos: 9.1, 9.2, 9.3, 9.4, 9.6, 7.1_

  - [~] 6.4 Actualizar configuración del access-service existente
    - Verificar/actualizar `application.yml` para alinear con el patrón definido: JWT configurable, actuator, HikariCP externalizado
    - Crear `application-prod.yml` si no existe, crear/actualizar `logback-spring.xml`
    - _Requisitos: 9.1, 9.2, 9.3, 9.4, 7.1_

- [ ] 7. Implementar configuración de seguridad por servicio
  - [~] 7.1 Crear SecurityConfig para cada microservicio
    - Crear `SecurityConfig.java` en cada servicio (`infrastructure.config`): @EnableWebSecurity, SecurityFilterChain con: csrf disabled, sessionManagement STATELESS, rutas públicas (/actuator/health, /actuator/info, /swagger-ui/**, /v3/api-docs/**), todas las demás requieren autenticación
    - Registrar JwtAuthenticationFilter antes de UsernamePasswordAuthenticationFilter
    - Registrar CorrelationIdFilter con orden HIGHEST_PRECEDENCE
    - En access-service: agregar rutas públicas adicionales (/api/access/v1/auth/login, /api/access/v1/auth/refresh)
    - _Requisitos: 4.4, 4.8, 4.10, 4.11_

  - [~] 7.2 Crear configuración OpenAPI/Swagger por servicio
    - Crear `OpenApiConfig.java` en cada servicio: @Configuration con @Bean OpenAPI que incluya info (nombre, versión, descripción), securityScheme (bearerAuth JWT)
    - Configurar que Swagger UI solo esté disponible en perfil `dev` (propiedad springdoc.swagger-ui.enabled)
    - _Requisitos: 12.1, 12.2, 12.3, 12.4_

- [~] 8. Checkpoint - Verificar compilación de todos los servicios
  - Asegurar que `mvn compile` ejecuta sin errores en cada servicio (access, sales, inventory, reporting), preguntar al usuario si surgen dudas.

- [ ] 9. Crear esqueleto del frontend Angular
  - [~] 9.1 Crear proyecto Angular y estructura de directorios
    - Crear proyecto `drinks-system-front/` con Angular 17, standalone components, Angular Material
    - Crear estructura: `src/app/core/` (interceptors, guards, services, models), `src/app/shared/` (components, pipes, directives), `src/app/features/` (access, sales, inventory, reporting), `src/app/layout/` (main-layout, sidebar, topbar)
    - Crear `src/environments/environment.ts` (apiUrl: http://localhost:8080) y `environment.prod.ts`
    - _Requisitos: 13.1, 13.2, 13.8_

  - [~] 9.2 Implementar auth.service.ts y token-storage.service.ts
    - Crear `core/services/auth.service.ts`: login(username, password), logout(), refreshToken(), getAccessToken(), isAuthenticated() usando signals
    - Crear `core/services/token-storage.service.ts`: almacenamiento seguro de accessToken y refreshToken en memoria/sessionStorage
    - Crear `core/models/user.model.ts` y `api-response.model.ts`
    - _Requisitos: 13.3, 13.4, 13.7_

  - [~] 9.3 Implementar interceptores HTTP (auth, error, correlation)
    - Crear `core/interceptors/auth.interceptor.ts`: adjuntar JWT a requests, detectar 401 y disparar refresh, si refresh falla→logout
    - Crear `core/interceptors/error.interceptor.ts`: manejo centralizado de errores HTTP (400 validación, 403 permisos, 404, 409, 500/503/504)
    - Crear `core/interceptors/correlation.interceptor.ts`: leer X-Correlation-ID del response para debugging
    - _Requisitos: 13.3, 13.4, 13.7_

  - [~] 9.4 Implementar guards de ruta y directiva de permisos
    - Crear `core/guards/auth.guard.ts`: verificar autenticación, redirigir a login si no autenticado
    - Crear `core/guards/permission.guard.ts`: verificar permiso específico del route data, denegar acceso si falta permiso
    - Crear `shared/directives/has-permission.directive.ts`: directiva estructural `*hasPermission="'PERMISSION'"` para mostrar/ocultar elementos
    - _Requisitos: 13.5_

  - [~] 9.5 Configurar routing con lazy loading y layout principal
    - Crear `app.routes.ts` con: ruta /login (público), layout principal con children lazy-loaded (access, sales, inventory, reporting) protegidos por authGuard y permissionGuard
    - Crear `app.config.ts` con provideHttpClient(withInterceptors), provideRouter, provideAnimations
    - Crear componentes de layout: `MainLayoutComponent`, `SidebarComponent`, `TopbarComponent`
    - _Requisitos: 13.1, 13.2, 13.5_

  - [~] 9.6 Crear componentes compartidos base
    - Crear `shared/components/data-table/`: tabla genérica con paginación y ordenamiento
    - Crear `shared/components/confirm-dialog/`: diálogo de confirmación reutilizable
    - Crear `shared/components/loading-spinner/`: spinner de carga global
    - Crear `shared/components/page-header/`: header de página con título y breadcrumbs
    - Crear `shared/pipes/currency-bo.pipe.ts` (formato moneda boliviana) y `date-format.pipe.ts`
    - _Requisitos: 13.6_

- [~] 10. Checkpoint - Verificar compilación del frontend
  - Asegurar que `ng build` ejecuta sin errores en el proyecto Angular, preguntar al usuario si surgen dudas.

- [ ] 11. Tests de propiedades (property-based testing)
  - [ ]* 11.1 Configurar jqwik en drinks-common y escribir test de Property 1: Round-trip JWT
    - Agregar dependencia jqwik a drinks-common (scope test)
    - **Property 1: Round-trip de JWT (generación y validación)**
    - Generar combinaciones arbitrarias de (userId, username, branchId, listaPermisos), generar JWT y validar que los claims extraídos son idénticos
    - **Valida: Requisitos 4.1, 4.2, 4.8**

  - [ ]* 11.2 Escribir test de Property 2: Decisión de autorización por permisos
    - **Property 2: Decisión de autorización por permisos**
    - Generar JWT con lista de permisos P y verificar: si permiso requerido R ∈ P → acceso permitido, si R ∉ P → 403
    - **Valida: Requisitos 4.4, 4.11, 13.5**

  - [ ]* 11.3 Escribir test de Property 3: Rechazo de tokens inválidos
    - **Property 3: Rechazo de tokens inválidos**
    - Generar strings arbitrarios y JWTs con firma incorrecta, verificar que siempre retorna 401
    - **Valida: Requisitos 4.10**

  - [ ]* 11.4 Escribir test de Property 4: Estructura de respuesta de error
    - **Property 4: Estructura de respuesta de error**
    - Generar tipos variados de excepciones, verificar que ErrorResponse siempre contiene todos los campos obligatorios no nulos
    - **Valida: Requisitos 6.2, 6.7**

  - [ ]* 11.5 Escribir test de Property 5: Ocultamiento de detalles internos en errores 500
    - **Property 5: Ocultamiento de detalles internos en errores 500**
    - Generar excepciones con mensajes conteniendo nombres de clases Java y stack traces, verificar que la respuesta 500 solo tiene mensaje genérico
    - **Valida: Requisitos 6.6**

  - [ ]* 11.6 Escribir test de Property 6: Propagación de headers inter-servicio
    - **Property 6: Propagación de headers en comunicación inter-servicio**
    - Configurar SecurityContext y MDC con valores arbitrarios, verificar que BaseRestClient incluye headers correctos en la solicitud
    - **Valida: Requisitos 3.7, 5.5**

  - [ ]* 11.7 Escribir test de Property 7: Reintentos con backoff exponencial
    - **Property 7: Reintentos con backoff exponencial para errores de servidor**
    - Simular respuestas 5xx, verificar exactamente 2 reintentos con tiempos crecientes
    - **Valida: Requisitos 5.6**

  - [ ]* 11.8 Escribir test de Property 9: Mapeo completo de errores de validación
    - **Property 9: Mapeo completo de errores de validación**
    - Generar conjuntos de N field errors, verificar que la respuesta 400 contiene exactamente N campos en fieldErrors
    - **Valida: Requisitos 6.3**

- [ ] 12. Tests de arquitectura (ArchUnit)
  - [ ]* 12.1 Configurar ArchUnit y escribir tests de arquitectura hexagonal
    - Agregar dependencia ArchUnit a cada microservicio (scope test)
    - Crear clase `HexagonalArchitectureTest.java` con reglas:
      - domain no debe depender de infrastructure
      - domain no debe usar Spring/JPA
      - controllers solo pueden llamar a servicios de aplicación
      - adaptadores de salida implementan puertos del dominio
    - _Requisitos: 2.1, 2.4, 2.5, 2.6_

- [ ] 13. Tests de integración
  - [ ]* 13.1 Escribir tests de integración para flujo de autenticación
    - Crear test con Spring Boot Test + MockMvc: login → obtener tokens → request autenticado → refresh → verificar nuevo token
    - Verificar respuestas 401 para tokens inválidos y 403 para permisos insuficientes
    - _Requisitos: 4.1, 4.4, 4.5, 4.10, 4.11_

  - [ ]* 13.2 Escribir tests de integración para comunicación inter-servicio
    - Crear test verificando que Sales → Inventory propaga JWT y Correlation_ID correctamente
    - Verificar manejo de errores: timeout, servicio no disponible, stock insuficiente
    - _Requisitos: 5.2, 5.3, 5.4, 5.5, 5.6_

  - [ ]* 13.3 Configurar fast-check en frontend y escribir property tests de interceptores
    - Agregar dependencia fast-check al proyecto Angular
    - Property test: auth interceptor adjunta JWT para cualquier request con token presente
    - Property test: permission guard autoriza/deniega correctamente según permisos
    - _Requisitos: 13.3, 13.4, 13.5_

- [~] 14. Checkpoint final - Verificar todo el sistema
  - Asegurar que todos los tests pasan en cada módulo, preguntar al usuario si surgen dudas.

## Notas

- Las tareas marcadas con `*` son opcionales y pueden omitirse para un MVP más rápido
- Cada tarea referencia requisitos específicos para trazabilidad
- Los checkpoints aseguran validación incremental
- Los tests de propiedades validan propiedades universales de correctitud
- Los tests unitarios validan casos específicos y edge cases
- El access-service ya existe con su estructura base; las tareas 5.4 y 6.4 ajustan la estructura existente
- drinks-common debe instalarse con `mvn install` antes de compilar los otros servicios
- Los esquemas de base de datos ya fueron creados en SPEC 1 (database-design); aquí se usa ddl-auto=validate

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2", "1.5", "1.7", "1.9"] },
    { "id": 2, "tasks": ["1.3", "1.6", "1.8"] },
    { "id": 3, "tasks": ["1.4"] },
    { "id": 4, "tasks": ["1.10"] },
    { "id": 5, "tasks": ["3.1", "3.2", "3.4", "5.1", "5.2", "5.3", "5.4"] },
    { "id": 6, "tasks": ["3.3", "6.1", "6.2", "6.3", "6.4"] },
    { "id": 7, "tasks": ["7.1", "7.2"] },
    { "id": 8, "tasks": ["9.1"] },
    { "id": 9, "tasks": ["9.2", "9.5"] },
    { "id": 10, "tasks": ["9.3", "9.4", "9.6"] },
    { "id": 11, "tasks": ["11.1", "11.4", "11.5", "11.8", "12.1"] },
    { "id": 12, "tasks": ["11.2", "11.3", "11.6", "11.7"] },
    { "id": 13, "tasks": ["13.1", "13.2", "13.3"] }
  ]
}
```
