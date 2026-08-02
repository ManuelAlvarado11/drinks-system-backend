# Documento de Requerimientos

## Introducción

El Access Service es el servicio responsable de la autenticación, autorización y gestión de acceso del Sistema de Gestión de Ventas e Inventario de Bar. Opera sobre el esquema `access` de la base de datos PostgreSQL `drinks_db` y expone endpoints REST bajo el prefijo `/api/access/v1/`. Este servicio implementa lógica de negocio para login/logout con JWT, gestión de usuarios, roles, permisos, sucursales, parámetros del sistema, catálogos, opciones de menú, auditoría y notificaciones. Utiliza arquitectura hexagonal con Spring Boot 4.1, Java 17 y Spring Security stateless.

## Glosario

- **Access_Service**: El microservicio Spring Boot que gestiona autenticación, autorización y configuración del sistema, escuchando en el puerto 8081
- **Autenticación**: El proceso de verificar la identidad de un usuario mediante credenciales (username/password) y generar tokens JWT
- **JWT (JSON Web Token)**: Token firmado con HMAC-SHA256 que contiene claims (sub, username, branchId, permissions) para autenticación stateless
- **Refresh_Token**: Token opaco de larga duración (7 días por defecto) almacenado como hash SHA-256 en base de datos, usado para renovar el JWT sin re-autenticación
- **UserPrincipal**: Objeto que representa al usuario autenticado extraído de los claims del JWT, disponible en el SecurityContext
- **Permiso**: Código granular (ej. USERS_CREATE, SALES_READ) asignado a roles que autoriza operaciones específicas
- **Rol**: Agrupación nombrada de permisos (ej. ADMINISTRADOR_SISTEMA, GERENTE_SUCURSAL, CAJERO)
- **Sucursal (Branch)**: Ubicación física del bar; los datos operacionales se asocian a una sucursal
- **Audit_Log**: Registro cronológico en tabla particionada que captura todas las operaciones de escritura con valores anteriores/posteriores en JSONB
- **Catálogo**: Tabla de valores enumerables configurables agrupados por tipo (métodos de pago, tipos de movimiento, etc.)
- **Parámetro_Sistema**: Par clave-valor configurable que controla comportamiento del sistema sin requerir cambios de código
- **Opción_Menú**: Entrada del menú de navegación del sistema vinculada a un permiso y organizada jerárquicamente
- **Notificación**: Mensaje dirigido a un usuario específico con tipo, título, contenido y estado de lectura
- **PageResponse**: Wrapper de paginación estándar de drinks-common que encapsula contenido, página actual, tamaño y total de elementos
- **ErrorResponse**: Estructura estándar de error de drinks-common con código, mensaje y timestamp
- **@RequiresPermission**: Anotación de drinks-common que verifica permisos del usuario antes de ejecutar un método

## Requerimientos

### Requerimiento 1: Login de Usuario

**User Story:** Como usuario del sistema, quiero autenticarme con mis credenciales, para obtener un token de acceso que me permita usar los servicios protegidos.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/auth/login` con username y password válidos, THE Access_Service SHALL responder con un objeto conteniendo accessToken (JWT), refreshToken, expiresIn (segundos) y tokenType ("Bearer")
2. WHEN el Access_Service genera un JWT, THE Access_Service SHALL incluir los claims: sub (userId como string), username, branchId (sucursal principal del usuario) y permissions (lista de códigos de permisos derivados de los roles activos del usuario)
3. WHEN el Access_Service valida las credenciales, THE Access_Service SHALL comparar el password proporcionado contra el password_hash almacenado usando BCrypt con factor de costo mínimo 10
4. WHEN el login es exitoso, THE Access_Service SHALL generar un refresh token opaco, almacenar su hash SHA-256 en la tabla access.refresh_tokens con la fecha de expiración configurada, y retornarlo en la respuesta
5. WHEN el login es exitoso, THE Access_Service SHALL actualizar el campo last_login del usuario con la fecha y hora actual
6. IF el username no existe o el password es incorrecto, THEN THE Access_Service SHALL responder con HTTP 401 y un ErrorResponse sin revelar cuál campo es inválido
7. IF el usuario existe pero tiene is_active = false, THEN THE Access_Service SHALL responder con HTTP 401 indicando que la cuenta está desactivada

### Requerimiento 2: Renovación de Token (Refresh)

**User Story:** Como usuario autenticado, quiero renovar mi token de acceso sin re-ingresar credenciales, para mantener mi sesión activa de forma transparente.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/auth/refresh` con un refreshToken válido, THE Access_Service SHALL generar un nuevo JWT y un nuevo refresh token, revocar el token anterior, y retornar la misma estructura que el login (accessToken, refreshToken, expiresIn, tokenType)
2. WHEN se rota el refresh token, THE Access_Service SHALL marcar el token anterior como is_revoked = true y crear un nuevo registro con un nuevo token_hash
3. IF el refresh token proporcionado no existe en la base de datos o está expirado, THEN THE Access_Service SHALL responder con HTTP 401
4. IF el refresh token proporcionado ya fue revocado (is_revoked = true), THEN THE Access_Service SHALL revocar todos los refresh tokens activos del usuario asociado y responder con HTTP 401, implementando detección de robo de token
5. WHEN se genera un nuevo refresh token, THE Access_Service SHALL aplicar la misma lógica de hashing SHA-256 y almacenamiento que en el login

### Requerimiento 3: Logout

**User Story:** Como usuario autenticado, quiero cerrar mi sesión, para invalidar mi refresh token y prevenir uso no autorizado.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/auth/logout` con un refreshToken válido, THE Access_Service SHALL revocar el refresh token (marcar is_revoked = true) y responder con HTTP 200
2. IF el refresh token proporcionado no existe o ya está revocado, THEN THE Access_Service SHALL responder con HTTP 200 sin error, para evitar filtración de información
3. THE Access_Service SHALL requerir que el endpoint de logout sea accesible solo para usuarios autenticados (requiere JWT válido en el header Authorization)

### Requerimiento 4: Gestión de Usuarios (CRUD)

**User Story:** Como administrador, quiero gestionar los usuarios del sistema, para controlar quién tiene acceso y con qué configuración.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/users` con datos válidos (username, password, email, full_name, branch_id), THE Access_Service SHALL crear el usuario con password_hash generado por BCrypt (costo 10+), asignar la sucursal indicada, y responder con HTTP 201 y los datos del usuario creado (sin password_hash)
2. WHEN el Access_Service recibe un GET en `/api/access/v1/users` con parámetros de paginación, THE Access_Service SHALL retornar un PageResponse con la lista de usuarios filtrable por is_active, branch_id y búsqueda por nombre/username
3. WHEN el Access_Service recibe un GET en `/api/access/v1/users/{id}`, THE Access_Service SHALL retornar los datos del usuario incluyendo sus roles y sucursales asignadas
4. WHEN el Access_Service recibe un PUT en `/api/access/v1/users/{id}` con datos actualizados, THE Access_Service SHALL actualizar los campos modificables (email, full_name, branch_id) y responder con HTTP 200
5. WHEN el Access_Service recibe un DELETE en `/api/access/v1/users/{id}`, THE Access_Service SHALL realizar un soft delete (is_active = false, deleted_at = now()) y responder con HTTP 204
6. IF se intenta crear un usuario con username o email duplicado, THEN THE Access_Service SHALL responder con HTTP 409 indicando el campo conflictivo
7. THE Access_Service SHALL requerir el permiso USERS_CREATE para crear usuarios, USERS_READ para listar/ver, USERS_UPDATE para actualizar, y USERS_DELETE para eliminar
8. WHEN el Access_Service recibe un POST en `/api/access/v1/users/{id}/roles` con una lista de role_ids, THE Access_Service SHALL asignar los roles indicados al usuario
9. WHEN el Access_Service recibe un DELETE en `/api/access/v1/users/{id}/roles/{roleId}`, THE Access_Service SHALL remover la asignación del rol al usuario
10. WHEN el Access_Service recibe un POST en `/api/access/v1/users/{id}/branches` con una lista de branch_ids, THE Access_Service SHALL asignar las sucursales indicadas al usuario en la tabla user_branches
11. WHEN el Access_Service recibe un DELETE en `/api/access/v1/users/{id}/branches/{branchId}`, THE Access_Service SHALL remover la asignación de la sucursal al usuario

### Requerimiento 5: Cambio de Contraseña

**User Story:** Como usuario, quiero cambiar mi contraseña, y como administrador quiero forzar el cambio de contraseña de otros usuarios, para mantener la seguridad de las cuentas.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un PUT en `/api/access/v1/users/me/password` con currentPassword y newPassword, THE Access_Service SHALL verificar que currentPassword coincida con el hash almacenado, actualizar el password_hash con el nuevo valor hasheado, y responder con HTTP 200
2. WHEN el Access_Service recibe un PUT en `/api/access/v1/users/{id}/password` con newPassword (sin currentPassword), THE Access_Service SHALL actualizar el password_hash del usuario indicado y responder con HTTP 200
3. THE Access_Service SHALL requerir autenticación para el cambio de contraseña propio y permiso USERS_UPDATE para el cambio forzado por administrador
4. IF el currentPassword no coincide con el hash almacenado en el cambio propio, THEN THE Access_Service SHALL responder con HTTP 400 indicando contraseña actual incorrecta
5. WHEN se cambia la contraseña exitosamente, THE Access_Service SHALL revocar todos los refresh tokens activos del usuario para forzar re-autenticación en otros dispositivos

### Requerimiento 6: Gestión de Roles (CRUD)

**User Story:** Como administrador del sistema, quiero gestionar los roles y sus permisos asociados, para configurar los niveles de acceso del sistema.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/roles` con code, name y description, THE Access_Service SHALL crear el rol y responder con HTTP 201
2. WHEN el Access_Service recibe un GET en `/api/access/v1/roles`, THE Access_Service SHALL retornar un PageResponse con la lista de roles incluyendo la cantidad de permisos y usuarios asignados a cada uno
3. WHEN el Access_Service recibe un GET en `/api/access/v1/roles/{id}`, THE Access_Service SHALL retornar el rol con su lista completa de permisos asignados
4. WHEN el Access_Service recibe un PUT en `/api/access/v1/roles/{id}` con datos actualizados, THE Access_Service SHALL actualizar name y description del rol (code es inmutable) y responder con HTTP 200
5. WHEN el Access_Service recibe un DELETE en `/api/access/v1/roles/{id}`, THE Access_Service SHALL desactivar el rol (is_active = false) y responder con HTTP 204
6. IF se intenta crear un rol con un code duplicado, THEN THE Access_Service SHALL responder con HTTP 409
7. WHEN el Access_Service recibe un PUT en `/api/access/v1/roles/{id}/permissions` con una lista de permission_ids, THE Access_Service SHALL reemplazar los permisos actuales del rol con la nueva lista proporcionada
8. THE Access_Service SHALL requerir el permiso CONFIG_PARAMS para todas las operaciones de gestión de roles

### Requerimiento 7: Gestión de Sucursales (CRUD)

**User Story:** Como administrador, quiero gestionar las sucursales del sistema, para agregar nuevas ubicaciones o desactivar las que ya no operan.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/branches` con name, address, phone y email, THE Access_Service SHALL crear la sucursal y responder con HTTP 201
2. WHEN el Access_Service recibe un GET en `/api/access/v1/branches`, THE Access_Service SHALL retornar un PageResponse con la lista de sucursales filtrable por is_active
3. WHEN el Access_Service recibe un GET en `/api/access/v1/branches/{id}`, THE Access_Service SHALL retornar los datos completos de la sucursal
4. WHEN el Access_Service recibe un PUT en `/api/access/v1/branches/{id}` con datos actualizados, THE Access_Service SHALL actualizar los campos de la sucursal y responder con HTTP 200
5. WHEN el Access_Service recibe un PATCH en `/api/access/v1/branches/{id}/status` con is_active, THE Access_Service SHALL activar o desactivar la sucursal y responder con HTTP 200
6. THE Access_Service SHALL requerir el permiso BRANCHES_CREATE para crear, BRANCHES_READ para listar/ver, y BRANCHES_UPDATE para actualizar y cambiar estado

### Requerimiento 8: Gestión de Parámetros del Sistema (CRUD)

**User Story:** Como administrador del sistema, quiero gestionar parámetros configurables, para modificar el comportamiento del sistema sin cambios de código.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/system-parameters` con parameter_key, parameter_value, data_type, description y module, THE Access_Service SHALL crear el parámetro y responder con HTTP 201
2. WHEN el Access_Service recibe un GET en `/api/access/v1/system-parameters`, THE Access_Service SHALL retornar un PageResponse con la lista de parámetros filtrable por module e is_active
3. WHEN el Access_Service recibe un GET en `/api/access/v1/system-parameters/{id}`, THE Access_Service SHALL retornar los datos completos del parámetro
4. WHEN el Access_Service recibe un GET en `/api/access/v1/system-parameters/key/{key}`, THE Access_Service SHALL retornar el parámetro correspondiente a la clave proporcionada
5. WHEN el Access_Service recibe un PUT en `/api/access/v1/system-parameters/{id}` con datos actualizados, THE Access_Service SHALL actualizar el parámetro y responder con HTTP 200
6. WHEN el Access_Service recibe un DELETE en `/api/access/v1/system-parameters/{id}`, THE Access_Service SHALL desactivar el parámetro (is_active = false) y responder con HTTP 204
7. IF se intenta crear un parámetro con parameter_key duplicado, THEN THE Access_Service SHALL responder con HTTP 409
8. THE Access_Service SHALL validar que data_type sea uno de: STRING, INTEGER, DECIMAL, BOOLEAN, JSON
9. THE Access_Service SHALL requerir el permiso CONFIG_PARAMS para todas las operaciones de parámetros del sistema

### Requerimiento 9: Gestión de Catálogos (CRUD)

**User Story:** Como administrador del sistema, quiero gestionar catálogos configurables, para mantener listas de valores sin modificar código.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/catalogs` con catalog_type, code, name, description, sort_order y parent_id opcional, THE Access_Service SHALL crear el catálogo y responder con HTTP 201
2. WHEN el Access_Service recibe un GET en `/api/access/v1/catalogs` con parámetro catalog_type, THE Access_Service SHALL retornar la lista de catálogos de ese tipo ordenados por sort_order
3. WHEN el Access_Service recibe un GET en `/api/access/v1/catalogs/types`, THE Access_Service SHALL retornar la lista de tipos de catálogo distintos disponibles
4. WHEN el Access_Service recibe un GET en `/api/access/v1/catalogs/{id}`, THE Access_Service SHALL retornar los datos completos del catálogo
5. WHEN el Access_Service recibe un PUT en `/api/access/v1/catalogs/{id}` con datos actualizados, THE Access_Service SHALL actualizar el catálogo y responder con HTTP 200
6. WHEN el Access_Service recibe un DELETE en `/api/access/v1/catalogs/{id}`, THE Access_Service SHALL desactivar el catálogo (is_active = false) y responder con HTTP 204
7. IF se intenta crear un catálogo con combinación catalog_type + code duplicada, THEN THE Access_Service SHALL responder con HTTP 409
8. THE Access_Service SHALL requerir el permiso CONFIG_CATALOGS para todas las operaciones de gestión de catálogos

### Requerimiento 10: Gestión de Opciones de Menú del Sistema

**User Story:** Como administrador, quiero gestionar las opciones del menú de navegación, para que los usuarios vean solo las funcionalidades a las que tienen acceso.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un POST en `/api/access/v1/menu-options` con name, route, icon, parent_id, permission_id y sort_order, THE Access_Service SHALL crear la opción de menú y responder con HTTP 201
2. WHEN el Access_Service recibe un GET en `/api/access/v1/menu-options`, THE Access_Service SHALL retornar la lista completa de opciones de menú en estructura plana
3. WHEN el Access_Service recibe un GET en `/api/access/v1/menu-options/{id}`, THE Access_Service SHALL retornar los datos de la opción de menú
4. WHEN el Access_Service recibe un PUT en `/api/access/v1/menu-options/{id}` con datos actualizados, THE Access_Service SHALL actualizar la opción de menú y responder con HTTP 200
5. WHEN el Access_Service recibe un DELETE en `/api/access/v1/menu-options/{id}`, THE Access_Service SHALL desactivar la opción (is_active = false) y responder con HTTP 204
6. WHEN el Access_Service recibe un GET en `/api/access/v1/menu-options/my-menu`, THE Access_Service SHALL retornar el árbol de menú del usuario autenticado, incluyendo solo las opciones cuyo permission_id corresponda a un permiso que el usuario posee, organizadas jerárquicamente por parent_id y ordenadas por sort_order
7. THE Access_Service SHALL requerir el permiso CONFIG_PARAMS para operaciones CRUD de opciones de menú, y solo autenticación para consultar el menú propio
8. WHEN una opción de menú no tiene permission_id asociado, THE Access_Service SHALL incluirla en el menú de todos los usuarios autenticados

### Requerimiento 11: Registro de Auditoría

**User Story:** Como administrador del sistema, quiero que todas las operaciones de escritura generen registros de auditoría, para tener trazabilidad completa de las acciones realizadas.

#### Criterios de Aceptación

1. WHEN una operación de escritura (crear, actualizar, eliminar) se ejecuta exitosamente en cualquier entidad gestionada por el Access_Service, THE Access_Service SHALL insertar un registro en access.audit_logs con: user_id, username, action (CREATE/UPDATE/DELETE), module, entity_name, entity_id, old_values (JSONB, null en creación), new_values (JSONB, null en eliminación), ip_address y descripción
2. THE Access_Service SHALL capturar la dirección IP del cliente desde el header X-Forwarded-For o la conexión directa
3. THE Access_Service SHALL registrar la auditoría de forma asíncrona o post-commit para no afectar el tiempo de respuesta de la operación principal
4. IF la inserción del registro de auditoría falla, THEN THE Access_Service SHALL registrar el error en logs de la aplicación sin afectar la operación principal
5. WHEN el Access_Service recibe un GET en `/api/access/v1/audit-logs` con filtros opcionales (user_id, module, entity_name, fecha_desde, fecha_hasta), THE Access_Service SHALL retornar un PageResponse con los registros de auditoría ordenados por created_at descendente
6. THE Access_Service SHALL requerir el permiso CONFIG_PARAMS para consultar los registros de auditoría

### Requerimiento 12: Gestión de Notificaciones

**User Story:** Como usuario del sistema, quiero recibir y gestionar notificaciones, para estar informado sobre eventos relevantes como stock bajo o alertas del sistema.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un GET en `/api/access/v1/notifications` del usuario autenticado, THE Access_Service SHALL retornar un PageResponse con las notificaciones del usuario ordenadas por created_at descendente, filtrable por is_read y notification_type
2. WHEN el Access_Service recibe un GET en `/api/access/v1/notifications/unread-count`, THE Access_Service SHALL retornar el conteo de notificaciones no leídas del usuario autenticado
3. WHEN el Access_Service recibe un PATCH en `/api/access/v1/notifications/{id}/read`, THE Access_Service SHALL marcar la notificación como leída (is_read = true, read_at = now()) y responder con HTTP 200
4. WHEN el Access_Service recibe un PATCH en `/api/access/v1/notifications/read-all`, THE Access_Service SHALL marcar todas las notificaciones no leídas del usuario autenticado como leídas
5. IF un usuario intenta acceder a una notificación que no le pertenece, THEN THE Access_Service SHALL responder con HTTP 403
6. THE Access_Service SHALL requerir solo autenticación (sin permiso específico) para que cada usuario gestione sus propias notificaciones
7. WHEN el Access_Service recibe un POST en `/api/access/v1/notifications` con branch_id, user_id, notification_type, title y message, THE Access_Service SHALL crear la notificación y responder con HTTP 201 (uso interno entre servicios o por administradores)

### Requerimiento 13: Consulta de Permisos

**User Story:** Como frontend, quiero obtener la lista de permisos disponibles agrupados por módulo, para poblar los formularios de asignación de permisos a roles.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un GET en `/api/access/v1/permissions`, THE Access_Service SHALL retornar la lista completa de permisos activos agrupados por módulo
2. WHEN el Access_Service recibe un GET en `/api/access/v1/permissions/modules`, THE Access_Service SHALL retornar la lista de módulos distintos con sus permisos asociados
3. THE Access_Service SHALL requerir el permiso CONFIG_PARAMS para consultar la lista de permisos

### Requerimiento 14: Perfil del Usuario Autenticado

**User Story:** Como usuario autenticado, quiero consultar y verificar mi propia información de perfil, para confirmar mi identidad y permisos actuales.

#### Criterios de Aceptación

1. WHEN el Access_Service recibe un GET en `/api/access/v1/users/me`, THE Access_Service SHALL retornar los datos del usuario autenticado incluyendo username, email, full_name, branch_id, roles y permisos efectivos
2. THE Access_Service SHALL requerir solo autenticación (sin permiso específico) para este endpoint
3. THE Access_Service SHALL derivar el userId del claim sub del JWT para identificar al usuario

### Requerimiento 15: Validación de Entrada y Manejo de Errores

**User Story:** Como consumidor de la API, quiero respuestas de error consistentes y validaciones claras, para integrarme correctamente con el servicio.

#### Criterios de Aceptación

1. THE Access_Service SHALL validar todos los campos de entrada (no nulos donde requerido, formatos de email, longitudes máximas según esquema de BD) antes de procesar la operación
2. IF la validación de entrada falla, THEN THE Access_Service SHALL responder con HTTP 400 y un ErrorResponse que incluya la lista de campos con error y sus mensajes descriptivos
3. IF se solicita un recurso por ID que no existe, THEN THE Access_Service SHALL responder con HTTP 404 y un ErrorResponse con mensaje descriptivo
4. IF el usuario no tiene el permiso requerido para una operación, THEN THE Access_Service SHALL responder con HTTP 403 y un ErrorResponse indicando permiso insuficiente
5. IF el JWT en el header Authorization es inválido o está expirado, THEN THE Access_Service SHALL responder con HTTP 401
6. THE Access_Service SHALL utilizar el GlobalExceptionHandler de drinks-common para el manejo centralizado de excepciones
7. THE Access_Service SHALL incluir el correlation_id (del CorrelationIdFilter de drinks-common) en todas las respuestas de error para facilitar el rastreo

### Requerimiento 16: Paginación y Filtrado

**User Story:** Como frontend, quiero que los endpoints de listado soporten paginación estándar y filtros, para cargar datos de forma eficiente.

#### Criterios de Aceptación

1. THE Access_Service SHALL aceptar parámetros de paginación page (default 0) y size (default 20, máximo 100) en todos los endpoints de listado
2. THE Access_Service SHALL retornar resultados paginados usando el formato PageResponse de drinks-common con campos: content, page, size, totalElements y totalPages
3. WHEN se proporcionan parámetros de búsqueda (search, filtros por campo), THE Access_Service SHALL aplicarlos antes de la paginación
4. THE Access_Service SHALL soportar ordenamiento configurable mediante el parámetro sort (campo,dirección) en los endpoints de listado
