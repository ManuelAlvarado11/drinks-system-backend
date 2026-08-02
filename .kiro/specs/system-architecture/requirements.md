# Documento de Requisitos: Arquitectura General del Sistema

## Introducción

Este documento define los requisitos arquitectónicos para el Sistema de Gestión de Ventas e Inventario de Bar. El sistema está compuesto por cuatro microservicios (Access, Sales, Inventory, Reporting), un frontend Angular y una base de datos PostgreSQL compartida con esquemas separados. La arquitectura debe soportar un despliegue local con Docker inicialmente, con diseño preparado para migración futura a la nube.

## Glosario

- **Sistema**: El Sistema de Gestión de Ventas e Inventario de Bar en su conjunto
- **Access_Service**: Microservicio responsable de autenticación, autorización, usuarios, roles, permisos, configuración de seguridad y parámetros del sistema
- **Sales_Service**: Microservicio responsable de cuentas/comandas, ventas, facturación, cierre de cuentas e impresión de tickets
- **Inventory_Service**: Microservicio responsable de productos, categorías, movimientos de inventario, stock, proveedores y compras
- **Reporting_Service**: Microservicio responsable de dashboards, estadísticas y exportación de reportes
- **API_Gateway**: Punto de entrada único (reverse proxy) que enruta las solicitudes del frontend a los microservicios correspondientes
- **Frontend**: Aplicación Angular 17 que consume los servicios backend a través del API_Gateway
- **JWT**: JSON Web Token utilizado para autenticación stateless entre servicios
- **Refresh_Token**: Token de larga duración almacenado en base de datos para renovar JWTs expirados
- **Correlation_ID**: Identificador único propagado en todas las llamadas de una solicitud para trazabilidad
- **Hexagonal_Architecture**: Patrón arquitectónico que separa la lógica de negocio de los adaptadores de infraestructura
- **Docker_Compose**: Herramienta de orquestación para definir y ejecutar aplicaciones multi-contenedor
- **Connection_Pool**: Conjunto de conexiones reutilizables a la base de datos para optimizar el rendimiento

---

## Requisitos

### Requisito 1: Estructura de Microservicios

**Historia de Usuario:** Como arquitecto del sistema, quiero que cada microservicio tenga responsabilidades claramente definidas y separadas, para que el sistema sea mantenible y pueda evolucionar independientemente.

#### Criterios de Aceptación

1. THE Sistema SHALL estar compuesto por exactamente cuatro microservicios backend: Access_Service (puerto 8081), Sales_Service (puerto 8082), Inventory_Service (puerto 8083) y Reporting_Service (puerto 8084)
2. THE Access_Service SHALL gestionar autenticación, autorización, usuarios, roles, permisos, sucursales, parámetros del sistema y catálogos, operando exclusivamente sobre el esquema `access`
3. THE Sales_Service SHALL gestionar cuentas, ventas, detalles de venta, facturación, cajas y clientes, operando exclusivamente sobre el esquema `sales`
4. THE Inventory_Service SHALL gestionar productos, categorías, stock, movimientos de inventario, proveedores y órdenes de compra, operando exclusivamente sobre el esquema `inventory`
5. THE Reporting_Service SHALL gestionar dashboards, estadísticas y exportación de reportes en Excel y PDF, operando sobre el esquema `reporting` con acceso de lectura a los demás esquemas
6. WHEN un servicio necesite datos de otro dominio, THE servicio consumidor SHALL realizar una llamada HTTP REST al servicio propietario de esos datos en lugar de acceder directamente a su esquema
7. THE Access_Service SHALL ser el único servicio con permisos de escritura para ejecutar migraciones Flyway sobre todos los esquemas

---

### Requisito 2: Arquitectura Hexagonal por Servicio

**Historia de Usuario:** Como desarrollador, quiero que cada microservicio siga la arquitectura hexagonal, para que la lógica de negocio esté desacoplada de la infraestructura y sea fácil de testear.

#### Criterios de Aceptación

1. THE cada microservicio SHALL organizar su código en tres capas: dominio (domain), aplicación (application) y infraestructura (infrastructure)
2. THE capa de dominio SHALL contener entidades, value objects, excepciones de dominio e interfaces de puertos (repositorios y servicios externos)
3. THE capa de aplicación SHALL contener casos de uso (servicios de aplicación), DTOs de entrada/salida y mappers
4. THE capa de infraestructura SHALL contener adaptadores de entrada (controllers REST), adaptadores de salida (implementaciones JPA de repositorios, clientes HTTP) y configuraciones
5. THE capa de dominio SHALL ser independiente de frameworks y bibliotecas externas, sin importaciones de Spring, JPA u otras dependencias de infraestructura
6. WHEN la capa de aplicación necesite interactuar con infraestructura, THE capa de aplicación SHALL depender de interfaces (puertos) definidas en la capa de dominio

---

### Requisito 3: API Gateway / Reverse Proxy

**Historia de Usuario:** Como desarrollador frontend, quiero un punto de entrada único para todos los servicios backend, para que no necesite conocer las direcciones individuales de cada microservicio.

#### Criterios de Aceptación

1. THE API_Gateway SHALL exponer un único punto de entrada en el puerto 8080 para todas las solicitudes del Frontend
2. THE API_Gateway SHALL enrutar solicitudes con prefijo `/api/access/**` al Access_Service
3. THE API_Gateway SHALL enrutar solicitudes con prefijo `/api/sales/**` al Sales_Service
4. THE API_Gateway SHALL enrutar solicitudes con prefijo `/api/inventory/**` al Inventory_Service
5. THE API_Gateway SHALL enrutar solicitudes con prefijo `/api/reporting/**` al Reporting_Service
6. THE API_Gateway SHALL configurar CORS para permitir solicitudes únicamente desde el origen del Frontend
7. THE API_Gateway SHALL propagar los headers de autorización (JWT) a los servicios destino sin modificación
8. THE API_Gateway SHALL generar un Correlation_ID único para cada solicitud entrante y propagarlo a los servicios destino
9. IF el servicio destino no responde dentro de 30 segundos, THEN THE API_Gateway SHALL retornar un error HTTP 504 Gateway Timeout al cliente

---

### Requisito 4: Seguridad y Autenticación

**Historia de Usuario:** Como administrador del sistema, quiero un mecanismo de seguridad robusto basado en JWT con autorización por permisos, para que solo usuarios autorizados accedan a los recursos correspondientes.

#### Criterios de Aceptación

1. WHEN un usuario envía credenciales válidas al endpoint de login, THE Access_Service SHALL generar un JWT con los permisos del usuario y un Refresh_Token, retornando ambos al cliente
2. THE JWT SHALL contener como claims: user_id, username, branch_id, lista de permisos y tiempo de expiración de 15 minutos
3. THE Refresh_Token SHALL almacenarse en la base de datos con una expiración de 7 días y asociarse al usuario y dispositivo
4. WHEN un cliente presenta un JWT válido y no expirado, THE servicio receptor SHALL autorizar la solicitud verificando que el usuario posee el permiso requerido para el endpoint
5. WHEN un JWT ha expirado, THE Access_Service SHALL permitir la renovación mediante un Refresh_Token válido, generando un nuevo JWT y rotando el Refresh_Token
6. WHEN se rota un Refresh_Token, THE Access_Service SHALL invalidar el token anterior para prevenir reutilización
7. IF un Refresh_Token ya invalidado es presentado para renovación, THEN THE Access_Service SHALL invalidar todos los Refresh_Tokens del usuario (detección de robo de token)
8. THE cada microservicio SHALL validar el JWT localmente utilizando la misma clave secreta compartida, sin necesidad de consultar al Access_Service en cada solicitud
9. THE Sistema SHALL hashear todas las contraseñas con BCrypt utilizando un factor de costo mínimo de 10
10. IF una solicitud no incluye JWT o el JWT es inválido, THEN THE servicio receptor SHALL retornar HTTP 401 Unauthorized
11. IF el JWT es válido pero el usuario no posee el permiso requerido, THEN THE servicio receptor SHALL retornar HTTP 403 Forbidden

---

### Requisito 5: Comunicación entre Servicios

**Historia de Usuario:** Como arquitecto del sistema, quiero patrones de comunicación claros entre servicios, para que las operaciones cross-domain mantengan la consistencia de datos.

#### Criterios de Aceptación

1. THE comunicación entre Frontend y servicios backend SHALL utilizar exclusivamente REST sobre HTTP/JSON
2. THE comunicación entre microservicios SHALL utilizar llamadas HTTP REST síncronas para operaciones que requieran consistencia inmediata
3. WHEN se registra una venta en Sales_Service, THE Sales_Service SHALL invocar al Inventory_Service de forma síncrona para descontar el stock antes de confirmar la venta
4. IF la llamada al Inventory_Service falla durante el registro de una venta, THEN THE Sales_Service SHALL rechazar la venta completa y retornar un error al cliente
5. WHEN un servicio invoca a otro servicio, THE servicio invocante SHALL propagar el JWT y el Correlation_ID del request original
6. IF un servicio destino retorna un error HTTP 5xx, THEN THE servicio invocante SHALL reintentar la operación un máximo de 2 veces con backoff exponencial antes de fallar
7. THE cada servicio SHALL exponer sus APIs con versionado en la URL utilizando el formato `/api/v1/`

---

### Requisito 6: Manejo Global de Excepciones

**Historia de Usuario:** Como desarrollador frontend, quiero que todos los servicios retornen errores en un formato consistente, para que pueda manejarlos de forma uniforme en la interfaz de usuario.

#### Criterios de Aceptación

1. THE cada microservicio SHALL implementar un manejador global de excepciones que capture todas las excepciones no controladas
2. THE formato de respuesta de error SHALL seguir la estructura: `{ "timestamp", "status", "error", "message", "path", "correlationId" }`
3. WHEN ocurre una excepción de validación, THE servicio SHALL retornar HTTP 400 con la lista de campos inválidos y sus mensajes de validación
4. WHEN ocurre una excepción de recurso no encontrado, THE servicio SHALL retornar HTTP 404 con el identificador del recurso solicitado
5. WHEN ocurre una excepción de conflicto de negocio, THE servicio SHALL retornar HTTP 409 con la descripción del conflicto
6. IF ocurre una excepción inesperada, THEN THE servicio SHALL retornar HTTP 500 con un mensaje genérico sin exponer detalles internos de implementación
7. THE cada respuesta de error SHALL incluir el Correlation_ID para facilitar la trazabilidad

---

### Requisito 7: Logging y Auditoría

**Historia de Usuario:** Como administrador del sistema, quiero un sistema de logging estructurado y auditoría de acciones, para que pueda diagnosticar problemas y mantener un registro de las operaciones del sistema.

#### Criterios de Aceptación

1. THE cada microservicio SHALL generar logs en formato JSON estructurado con los campos: timestamp, level, service, correlationId, userId, message y context
2. THE cada microservicio SHALL incluir el Correlation_ID en todos los mensajes de log de una solicitud para permitir trazabilidad end-to-end
3. WHEN un usuario realiza una operación de escritura (crear, actualizar, eliminar), THE servicio correspondiente SHALL registrar un evento de auditoría con: usuario, acción, entidad, valores anteriores y valores nuevos
4. THE nivel de logging por defecto para producción SHALL ser INFO, con la capacidad de configurar DEBUG por paquete sin reiniciar el servicio
5. IF una operación entre servicios falla, THEN THE servicio invocante SHALL registrar un log de nivel ERROR con el Correlation_ID, servicio destino, endpoint y código de respuesta

---

### Requisito 8: Estructura del Proyecto Backend

**Historia de Usuario:** Como desarrollador, quiero una estructura de proyecto consistente y convenciones claras, para que pueda navegar y contribuir a cualquier microservicio de forma eficiente.

#### Criterios de Aceptación

1. THE cada microservicio SHALL ser un proyecto Maven independiente con su propio `pom.xml` y ciclo de vida de build independiente
2. THE convención de paquetes base SHALL seguir el formato `drinks.system.{nombre_servicio}` (ejemplo: `drinks.system.accessservice`, `drinks.system.salesservice`)
3. THE estructura de paquetes dentro de cada servicio SHALL seguir: `{base}.domain.model`, `{base}.domain.port`, `{base}.application.service`, `{base}.application.dto`, `{base}.application.mapper`, `{base}.infrastructure.adapter.in.rest`, `{base}.infrastructure.adapter.out.persistence`, `{base}.infrastructure.config`
4. THE Sistema SHALL incluir una biblioteca compartida (`drinks-common`) que contenga: excepciones base, DTOs comunes, filtros de seguridad JWT, utilidades de logging y configuración base de manejo de errores
5. THE cada microservicio SHALL declarar `drinks-common` como dependencia Maven
6. THE cada microservicio SHALL utilizar Spring Boot 4.1+ con Java 17+ como versión mínima

---

### Requisito 9: Configuración y Perfiles

**Historia de Usuario:** Como DevOps, quiero que la configuración sea externalizable y específica por entorno, para que pueda desplegar el mismo artefacto en diferentes ambientes sin recompilar.

#### Criterios de Aceptación

1. THE cada microservicio SHALL externalizar toda configuración sensible (credenciales, URLs, puertos) mediante variables de entorno
2. THE cada microservicio SHALL definir perfiles de Spring para al menos dos entornos: `dev` (desarrollo local) y `prod` (producción)
3. THE perfil `dev` SHALL utilizar valores por defecto embebidos que permitan ejecutar el servicio sin configuración externa
4. THE perfil `prod` SHALL requerir que todas las variables sensibles sean provistas externamente, fallando al iniciar si alguna falta
5. THE Sistema SHALL centralizar las variables de entorno compartidas en un único archivo `.env` utilizado por Docker_Compose
6. IF una variable de entorno requerida en perfil `prod` no está definida, THEN THE servicio SHALL fallar al iniciar con un mensaje claro indicando la variable faltante

---

### Requisito 10: Despliegue con Docker

**Historia de Usuario:** Como DevOps, quiero que todo el sistema se despliegue con un solo comando Docker Compose, para que la puesta en marcha sea reproducible y simple.

#### Criterios de Aceptación

1. THE Docker_Compose SHALL definir contenedores para: PostgreSQL, Access_Service, Sales_Service, Inventory_Service, Reporting_Service y API_Gateway
2. THE cada microservicio SHALL empaquetarse como una imagen Docker utilizando un Dockerfile multi-stage (compilación Maven + runtime JRE mínimo)
3. THE Docker_Compose SHALL definir una red interna donde los servicios se comuniquen por nombre de servicio (DNS interno de Docker)
4. THE Docker_Compose SHALL exponer únicamente el puerto del API_Gateway (8080) y opcionalmente el puerto de PostgreSQL (5432) al host
5. THE Docker_Compose SHALL definir dependencias de inicio con health checks para que los microservicios arranquen solo cuando PostgreSQL esté disponible
6. THE contenedor de PostgreSQL SHALL utilizar un volumen nombrado para persistir los datos entre reinicios
7. WHEN se ejecute `docker compose up`, THE Sistema SHALL arrancar completamente sin intervención manual adicional

---

### Requisito 11: Health Checks y Monitoreo

**Historia de Usuario:** Como operador del sistema, quiero endpoints de salud en cada servicio, para que pueda verificar el estado del sistema y que Docker pueda gestionar contenedores caídos.

#### Criterios de Aceptación

1. THE cada microservicio SHALL exponer un endpoint `/actuator/health` que retorne el estado del servicio y sus dependencias (base de datos)
2. THE Docker_Compose SHALL configurar health checks para cada contenedor de microservicio utilizando el endpoint `/actuator/health`
3. WHEN la conexión a la base de datos se pierde, THE endpoint de salud SHALL reportar estado DOWN con detalle de la dependencia fallida
4. THE cada microservicio SHALL exponer un endpoint `/actuator/info` con la versión del servicio y timestamp de build

---

### Requisito 12: Documentación de APIs

**Historia de Usuario:** Como desarrollador frontend, quiero documentación interactiva de cada API, para que pueda explorar y probar los endpoints disponibles sin leer código fuente.

#### Criterios de Aceptación

1. THE cada microservicio SHALL generar documentación OpenAPI 3.0 automáticamente a partir de las anotaciones del código
2. THE cada microservicio SHALL exponer la interfaz Swagger UI en la ruta `/swagger-ui.html` en el perfil `dev`
3. THE documentación OpenAPI SHALL incluir para cada endpoint: descripción, parámetros, cuerpo de solicitud, respuestas posibles con ejemplos y códigos de error
4. WHILE el perfil `prod` esté activo, THE microservicio SHALL deshabilitar el acceso a Swagger UI

---

### Requisito 13: Arquitectura Frontend

**Historia de Usuario:** Como desarrollador frontend, quiero una arquitectura Angular bien definida con lazy loading y gestión de estado, para que la aplicación sea performante y mantenible.

#### Criterios de Aceptación

1. THE Frontend SHALL utilizar Angular 17 con standalone components, signals para reactividad y Angular Material como biblioteca de componentes UI
2. THE Frontend SHALL organizar el código en feature modules con lazy loading por ruta principal (acceso, ventas, inventario, reportes)
3. THE Frontend SHALL implementar un HTTP interceptor que adjunte automáticamente el JWT a todas las solicitudes salientes hacia el API_Gateway
4. THE Frontend SHALL implementar un HTTP interceptor que detecte respuestas HTTP 401 y dispare automáticamente el flujo de renovación de token con el Refresh_Token
5. THE Frontend SHALL implementar guards de ruta que verifiquen los permisos del usuario antes de permitir acceso a módulos protegidos
6. THE Frontend SHALL mantener una biblioteca de componentes compartidos (shared module) con componentes reutilizables, pipes y directivas comunes
7. IF la renovación del token falla, THEN THE Frontend SHALL redirigir al usuario a la pantalla de login limpiando todo el estado local
8. THE Frontend SHALL conectarse exclusivamente al API_Gateway como único punto de acceso al backend

---

### Requisito 14: Escalabilidad y Preparación para la Nube

**Historia de Usuario:** Como arquitecto del sistema, quiero que la arquitectura permita escalar horizontalmente en el futuro, para que el sistema pueda migrar a la nube sin rediseño mayor.

#### Criterios de Aceptación

1. THE cada microservicio SHALL ser stateless, almacenando todo estado de sesión en el JWT y estado persistente en la base de datos
2. THE cada microservicio SHALL configurar un connection pool (HikariCP) con tamaño máximo configurable por variable de entorno
3. THE diseño de servicios SHALL evitar afinidad de sesión, permitiendo que cualquier instancia atienda cualquier solicitud del mismo usuario
4. THE Docker_Compose SHALL estructurarse de forma que cada servicio pueda replicarse con `docker compose up --scale` sin conflictos de puertos
5. WHEN se configure un balanceador de carga frente a múltiples instancias de un servicio, THE servicio SHALL funcionar correctamente sin cambios en el código

---

### Requisito 15: Versionado de APIs y Compatibilidad

**Historia de Usuario:** Como arquitecto del sistema, quiero una estrategia de versionado de APIs, para que pueda evolucionar los contratos sin romper clientes existentes.

#### Criterios de Aceptación

1. THE cada microservicio SHALL versionar sus endpoints REST utilizando prefijo en la URL con formato `/api/v1/`
2. WHEN se introduzca un cambio incompatible en un endpoint, THE servicio SHALL crear una nueva versión (`/api/v2/`) manteniendo la versión anterior funcional durante un periodo de transición
3. THE cada respuesta de API SHALL incluir el header `X-API-Version` indicando la versión del endpoint que procesó la solicitud

