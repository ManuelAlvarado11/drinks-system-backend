# Plan de Implementación: Access Service

## Visión General

Implementación bottom-up del Access Service siguiendo arquitectura hexagonal: modelos de dominio → puertos → entidades JPA → repositorios → DTOs → mappers → adaptadores → servicios → configuración → controladores → auditoría → tests. Prioriza el módulo de autenticación por dependencia del frontend.

## Tareas

- [x] 1. Modelos de dominio y enums
  - [x] 1.1 Crear enums del dominio (AuditAction, AuditModule, DataType, NotificationType)
    - Crear los 4 enums en `domain/model/enums/`
    - AuditAction: CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    - AuditModule: ACCESS, SALES, INVENTORY, REPORTING
    - DataType: STRING, INTEGER, DECIMAL, BOOLEAN, JSON
    - NotificationType: STOCK_BAJO, ALERTA_SISTEMA, INFO
    - _Requerimientos: 8.8, 11.1_

  - [x] 1.2 Crear modelos de dominio (records Java)
    - Crear los 10 records en `domain/model/`: User, Role, Permission, Branch, RefreshToken, SystemMenuOption, SystemParameter, Catalog, AuditLog, Notification
    - Usar tipos inmutables (records) sin anotaciones de framework
    - Incluir listas de relaciones donde corresponda (User → roles, branches; Role → permissions)
    - _Requerimientos: 1.2, 4.1, 6.1, 7.1, 8.1, 9.1, 10.1, 11.1, 12.1_

- [ ] 2. Puertos de entrada y salida
  - [x] 2.1 Crear puertos de entrada (Use Case interfaces)
    - Crear las 10 interfaces en `domain/port/in/`: AuthUseCase, UserUseCase, RoleUseCase, BranchUseCase, SystemParameterUseCase, CatalogUseCase, MenuOptionUseCase, AuditUseCase, NotificationUseCase, PermissionUseCase
    - Definir firmas según el documento de diseño
    - _Requerimientos: 1.1, 2.1, 3.1, 4.1-4.11, 5.1-5.2, 6.1-6.7, 7.1-7.5, 8.1-8.6, 9.1-9.6, 10.1-10.6, 11.5, 12.1-12.7, 13.1-13.2, 14.1_

  - [x] 2.2 Crear puertos de salida (Repository Port interfaces)
    - Crear las 10 interfaces en `domain/port/out/`: UserRepositoryPort, RoleRepositoryPort, PermissionRepositoryPort, BranchRepositoryPort, RefreshTokenRepositoryPort, MenuOptionRepositoryPort, SystemParameterRepositoryPort, CatalogRepositoryPort, AuditLogRepositoryPort, NotificationRepositoryPort
    - Definir firmas según el documento de diseño
    - _Requerimientos: 1.3, 1.4, 2.1-2.4, 4.2, 6.2, 7.2, 8.2, 9.2, 10.2, 11.5, 12.1_

- [x] 3. Entidades JPA
  - [x] 3.1 Crear entidades JPA principales (UserEntity, RoleEntity, PermissionEntity, BranchEntity)
    - Crear en `infrastructure/adapter/out/persistence/entity/`
    - Incluir anotaciones @Entity, @Table(schema="access"), @Id, @GeneratedValue
    - Implementar @PrePersist y @PreUpdate para timestamps
    - UserEntity con relaciones @OneToMany a UserRoleEntity y UserBranchEntity (LAZY)
    - RoleEntity con relación @OneToMany a RolePermissionEntity (LAZY)
    - _Requerimientos: 4.1, 6.1, 7.1, 13.1_

  - [x] 3.2 Crear entidades JPA de autenticación y tokens (RefreshTokenEntity)
    - Crear RefreshTokenEntity con campos: id, userId, tokenHash, expiresAt, isRevoked, deviceInfo, createdAt
    - _Requerimientos: 1.4, 2.1, 2.2_

  - [x] 3.3 Crear entidades JPA de relación N:M (UserRoleEntity, UserBranchEntity, RolePermissionEntity)
    - Crear las 3 entidades de relación con @ManyToOne y @JoinColumn
    - _Requerimientos: 4.8-4.11, 6.7_

  - [x] 3.4 Crear entidades JPA de configuración (SystemMenuOptionEntity, SystemParameterEntity, CatalogEntity)
    - Crear las 3 entidades con todos sus campos según diseño
    - _Requerimientos: 8.1, 9.1, 10.1_

  - [x] 3.5 Crear entidades JPA de auditoría y notificaciones (AuditLogEntity, NotificationEntity)
    - AuditLogEntity con campos JSONB (old_values, new_values) usando @JdbcTypeCode(SqlTypes.JSON)
    - NotificationEntity con campos is_read y read_at
    - _Requerimientos: 11.1, 12.1_

- [ ] 4. Repositorios Spring Data JPA
  - [x] 4.1 Crear repositorios JPA principales (UserJpaRepository, RoleJpaRepository, PermissionJpaRepository, BranchJpaRepository)
    - Crear interfaces en `infrastructure/adapter/out/persistence/repository/`
    - Extender JpaRepository con queries personalizados (@Query) para búsqueda y filtrado
    - UserJpaRepository: findByUsername, existsByUsername, existsByEmail, búsqueda con paginación
    - RoleJpaRepository: existsByCode, findByUserId
    - PermissionJpaRepository: findByRoleIds, findByIds
    - BranchJpaRepository: filtrado por isActive
    - _Requerimientos: 4.2, 4.6, 6.2, 6.6, 7.2, 13.1, 16.1-16.4_

  - [x] 4.2 Crear repositorios JPA de tokens y relaciones (RefreshTokenJpaRepository, UserRoleJpaRepository, UserBranchJpaRepository, RolePermissionJpaRepository)
    - RefreshTokenJpaRepository: findByTokenHash, revokeAllByUserId, updateIsRevoked
    - UserRoleJpaRepository: deleteByUserIdAndRoleId, findByUserId
    - UserBranchJpaRepository: deleteByUserIdAndBranchId
    - RolePermissionJpaRepository: deleteByRoleId, findByRoleId
    - _Requerimientos: 1.4, 2.1-2.4, 4.8-4.11, 6.7_

  - [x] 4.3 Crear repositorios JPA de configuración y auditoría (SystemMenuOptionJpaRepository, SystemParameterJpaRepository, CatalogJpaRepository, AuditLogJpaRepository, NotificationJpaRepository)
    - SystemParameterJpaRepository: findByParameterKey, existsByParameterKey, filtrado por module/isActive
    - CatalogJpaRepository: findByCatalogType, existsByCatalogTypeAndCode, findDistinctTypes
    - SystemMenuOptionJpaRepository: findActiveByPermissionIds, findActiveWithoutPermission
    - AuditLogJpaRepository: filtrado por userId, module, entityName, rango de fechas
    - NotificationJpaRepository: findByUserId con filtros, countUnreadByUserId, markAllAsRead
    - _Requerimientos: 8.2, 8.4, 8.7, 9.2, 9.3, 9.7, 10.2, 10.6, 11.5, 12.1-12.4_

- [ ] 5. DTOs (Request y Response)
  - [x] 5.1 Crear DTOs de request de autenticación y usuarios
    - Crear records en `application/dto/request/`: LoginRequest, RefreshTokenRequest, LogoutRequest, CreateUserRequest, UpdateUserRequest, ChangeOwnPasswordRequest, AdminChangePasswordRequest, AssignRolesRequest, AssignBranchesRequest
    - Incluir anotaciones de Bean Validation (@NotBlank, @Email, @Size, @NotNull, @NotEmpty)
    - _Requerimientos: 1.1, 2.1, 3.1, 4.1, 4.4, 4.8, 4.10, 5.1-5.2, 15.1_

  - [x] 5.2 Crear DTOs de request de roles, sucursales y configuración
    - Crear records en `application/dto/request/`: CreateRoleRequest, UpdateRoleRequest, AssignPermissionsRequest, CreateBranchRequest, UpdateBranchRequest, BranchStatusRequest, CreateSystemParameterRequest, UpdateSystemParameterRequest, CreateCatalogRequest, UpdateCatalogRequest, CreateMenuOptionRequest, UpdateMenuOptionRequest, CreateNotificationRequest
    - Incluir anotaciones de Bean Validation
    - _Requerimientos: 6.1, 6.4, 6.7, 7.1, 7.4, 7.5, 8.1, 8.5, 9.1, 9.5, 10.1, 10.4, 12.7, 15.1_

  - [~] 5.3 Crear DTOs de response
    - Crear records en `application/dto/response/`: AuthResponse, UserResponse, UserDetailResponse, UserProfileResponse, RoleResponse, RoleDetailResponse, PermissionResponse, PermissionsByModuleResponse, BranchResponse, SystemParameterResponse, CatalogResponse, MenuOptionResponse, MenuTreeResponse, AuditLogResponse, NotificationResponse, UnreadCountResponse
    - MenuTreeResponse con campo recursivo `List<MenuTreeResponse> children`
    - _Requerimientos: 1.1, 4.2-4.3, 6.2-6.3, 7.2-7.3, 8.2-8.3, 9.2-9.4, 10.2-10.3, 10.6, 11.5, 12.1-12.2, 13.1-13.2, 14.1_

- [ ] 6. Mappers
  - [~] 6.1 Crear mappers de autenticación y usuarios (UserMapper)
    - Crear en `application/mapper/`
    - Mapear UserEntity ↔ User (dominio) y User → UserResponse, UserDetailResponse, UserProfileResponse
    - _Requerimientos: 4.1-4.3, 14.1_

  - [~] 6.2 Crear mappers de roles, permisos y sucursales (RoleMapper, PermissionMapper, BranchMapper)
    - RoleMapper: RoleEntity ↔ Role, Role → RoleResponse, RoleDetailResponse
    - PermissionMapper: PermissionEntity ↔ Permission, Permission → PermissionResponse, PermissionsByModuleResponse
    - BranchMapper: BranchEntity ↔ Branch, Branch → BranchResponse
    - _Requerimientos: 6.1-6.3, 7.1-7.3, 13.1-13.2_

  - [~] 6.3 Crear mappers de configuración, auditoría y notificaciones (SystemParameterMapper, CatalogMapper, MenuOptionMapper, AuditLogMapper, NotificationMapper)
    - Mapear cada Entity ↔ Dominio ↔ Response según diseño
    - MenuOptionMapper incluye lógica para construir MenuTreeResponse
    - AuditLogMapper mapea AuditEvent → AuditLog (dominio) y AuditLog → AuditLogResponse
    - _Requerimientos: 8.1-8.3, 9.1-9.4, 10.1-10.6, 11.1, 11.5, 12.1_

- [~] 7. Checkpoint - Verificar compilación de la capa de datos
  - Asegurar que compilan correctamente: modelos de dominio, puertos, entidades JPA, repositorios, DTOs y mappers. Preguntar al usuario si surgen dudas.

- [ ] 8. Adaptadores de repositorio (conectan JPA repos con puertos de dominio)
  - [~] 8.1 Crear adaptadores de repositorio principales (UserRepositoryAdapter, RoleRepositoryAdapter, PermissionRepositoryAdapter, BranchRepositoryAdapter)
    - Crear en `infrastructure/adapter/out/persistence/adapter/`
    - Implementar cada RepositoryPort inyectando el JpaRepository correspondiente
    - Usar mappers para traducir Entity ↔ Domain model
    - Marcar con @Component o @Repository
    - _Requerimientos: 4.1-4.6, 6.1-6.6, 7.1-7.5, 13.1_

  - [~] 8.2 Crear adaptadores de repositorio de tokens y relaciones (RefreshTokenRepositoryAdapter)
    - Implementar RefreshTokenRepositoryPort
    - Métodos: findByTokenHash, save, revokeByTokenHash, revokeAllByUserId
    - _Requerimientos: 1.4, 2.1-2.5, 5.5_

  - [~] 8.3 Crear adaptadores de repositorio de configuración (MenuOptionRepositoryAdapter, SystemParameterRepositoryAdapter, CatalogRepositoryAdapter)
    - Implementar cada RepositoryPort con lógica de mapeo
    - _Requerimientos: 8.1-8.7, 9.1-9.7, 10.1-10.6_

  - [~] 8.4 Crear adaptadores de repositorio de auditoría y notificaciones (AuditLogRepositoryAdapter, NotificationRepositoryAdapter)
    - AuditLogRepositoryAdapter: save y findAll con filtros
    - NotificationRepositoryAdapter: findByUserId, countUnread, markAllAsRead, save
    - _Requerimientos: 11.1, 11.5, 12.1-12.4, 12.7_

- [ ] 9. Configuración adicional
  - [~] 9.1 Crear PasswordEncoderConfig y AsyncConfig
    - PasswordEncoderConfig: bean BCryptPasswordEncoder con factor de costo 10
    - AsyncConfig: @EnableAsync para habilitar procesamiento asíncrono de eventos
    - Crear en `config/`
    - _Requerimientos: 1.3, 11.3_

- [ ] 10. Servicios de aplicación - Módulo de Autenticación
  - [~] 10.1 Implementar AuthServiceImpl (login, refresh, logout)
    - Implementar AuthUseCase
    - Login: validar credenciales con BCrypt, recopilar permisos (roles → permisos sin duplicados), generar JWT via JwtTokenProvider, generar refresh token (UUID + SHA-256), almacenar hash, actualizar lastLogin
    - Refresh: buscar por tokenHash, verificar expiración/revocación, detección de robo (revocar todos si token ya revocado), rotación normal (revocar anterior + crear nuevo)
    - Logout: revocar refresh token (idempotente)
    - Publicar AuditEvent para login exitoso y logout
    - _Requerimientos: 1.1-1.7, 2.1-2.5, 3.1-3.3_

  - [ ]* 10.2 Escribir test de propiedad para permisos efectivos en JWT
    - **Propiedad 1: Permisos efectivos en JWT reflejan la unión de roles**
    - Generar usuarios con conjuntos aleatorios de roles y permisos, verificar que el JWT contiene exactamente la unión sin duplicados
    - **Valida: Requerimiento 1.2**

  - [ ]* 10.3 Escribir tests de propiedad para refresh token (round-trip, rotación, detección de robo)
    - **Propiedad 2: Round-trip de hashing de refresh token**
    - **Propiedad 3: Rotación de refresh token**
    - **Propiedad 4: Detección de robo revoca toda la familia de tokens**
    - **Valida: Requerimientos 1.4, 2.1, 2.2, 2.4, 2.5**

- [ ] 11. Servicios de aplicación - Módulo de Usuarios
  - [~] 11.1 Implementar UserServiceImpl (CRUD, asignación de roles/sucursales, cambio de contraseña, perfil)
    - Implementar UserUseCase
    - Create: validar duplicados (username, email), hashear password con BCrypt, asignar sucursal, publicar AuditEvent
    - FindAll: paginación con filtros (isActive, branchId, search)
    - FindById: incluir roles y sucursales
    - Update: actualizar campos modificables, publicar AuditEvent
    - Delete: soft delete (isActive=false, deletedAt=now()), publicar AuditEvent
    - AssignRoles/RemoveRole: gestionar relaciones user_roles
    - AssignBranches/RemoveBranch: gestionar relaciones user_branches
    - ChangeOwnPassword: verificar currentPassword, hashear nuevo, revocar todos los refresh tokens
    - AdminChangePassword: hashear nuevo, revocar todos los refresh tokens
    - GetProfile: retornar datos con roles y permisos efectivos
    - _Requerimientos: 4.1-4.11, 5.1-5.5, 14.1-14.3_

  - [ ]* 11.2 Escribir test de propiedad para cambio de contraseña
    - **Propiedad 5: Cambio de contraseña invalida todas las sesiones**
    - Generar usuarios con N refresh tokens activos, verificar que todos quedan revocados tras cambio de password
    - **Valida: Requerimiento 5.5**

- [ ] 12. Servicios de aplicación - Módulo de Roles y Permisos
  - [~] 12.1 Implementar RoleServiceImpl (CRUD, asignación de permisos)
    - Implementar RoleUseCase
    - Create: validar code duplicado, crear rol
    - FindAll: paginación con conteo de permisos y usuarios por rol
    - FindById: incluir lista completa de permisos
    - Update: actualizar name y description (code inmutable)
    - Delete: soft delete (isActive=false)
    - AssignPermissions: reemplazar permisos actuales con la nueva lista (delete all + insert)
    - Publicar AuditEvent en operaciones de escritura
    - _Requerimientos: 6.1-6.8_

  - [ ]* 12.2 Escribir test de propiedad para reemplazo de permisos
    - **Propiedad 6: Reemplazo de permisos de un rol es exacto**
    - Generar roles con permisos previos aleatorios, asignar nuevo subconjunto, verificar exactitud
    - **Valida: Requerimiento 6.7**

  - [~] 12.3 Implementar PermissionServiceImpl
    - Implementar PermissionUseCase
    - FindAll: retornar permisos activos
    - FindGroupedByModule: agrupar permisos por campo module
    - _Requerimientos: 13.1-13.2_

  - [ ]* 12.4 Escribir test de propiedad para agrupación de permisos por módulo
    - **Propiedad 9: Agrupación de permisos por módulo es correcta**
    - Generar conjuntos aleatorios de permisos con módulos variados, verificar que cada grupo contiene exactamente los permisos de su módulo
    - **Valida: Requerimiento 13.1**

- [ ] 13. Servicios de aplicación - Módulo de Sucursales y Configuración
  - [~] 13.1 Implementar BranchServiceImpl (CRUD, cambio de estado)
    - Implementar BranchUseCase
    - Create, FindAll (filtro isActive), FindById, Update, UpdateStatus
    - Publicar AuditEvent en operaciones de escritura
    - _Requerimientos: 7.1-7.6_

  - [~] 13.2 Implementar SystemParameterServiceImpl (CRUD, búsqueda por clave)
    - Implementar SystemParameterUseCase
    - Create: validar parameterKey duplicado, validar dataType válido (STRING, INTEGER, DECIMAL, BOOLEAN, JSON)
    - FindAll (filtros module, isActive), FindById, FindByKey, Update, Delete (soft)
    - Publicar AuditEvent en operaciones de escritura
    - _Requerimientos: 8.1-8.9_

  - [~] 13.3 Implementar CatalogServiceImpl (CRUD, tipos)
    - Implementar CatalogUseCase
    - Create: validar combinación catalogType+code duplicada
    - FindByType (ordenado por sortOrder), FindDistinctTypes, FindById, Update, Delete (soft)
    - Publicar AuditEvent en operaciones de escritura
    - _Requerimientos: 9.1-9.8_

  - [~] 13.4 Implementar MenuOptionServiceImpl (CRUD, menú personalizado)
    - Implementar MenuOptionUseCase
    - Create, FindAll (plano), FindById, Update, Delete (soft)
    - GetMyMenu: obtener permissionIds del usuario, consultar opciones activas por permisos + sin permiso, construir árbol jerárquico por parentId ordenado por sortOrder
    - Publicar AuditEvent en operaciones de escritura
    - _Requerimientos: 10.1-10.8_

  - [ ]* 13.5 Escribir test de propiedad para árbol de menú
    - **Propiedad 7: Árbol de menú contiene solo opciones autorizadas**
    - Generar conjuntos aleatorios de opciones de menú y permisos de usuario, verificar que el árbol solo contiene opciones cuyo permissionId está en el conjunto del usuario o es NULL
    - **Valida: Requerimientos 10.6, 10.8**

- [ ] 14. Servicios de aplicación - Auditoría y Notificaciones
  - [~] 14.1 Implementar AuditServiceImpl y AuditEventListener
    - AuditServiceImpl: implementar AuditUseCase con findAll (paginado con filtros por userId, module, entityName, dateFrom, dateTo)
    - AuditEventListener: @TransactionalEventListener(phase=AFTER_COMMIT) + @Async
    - Mapear AuditEvent → AuditLog → save con try/catch (log.error sin propagar)
    - _Requerimientos: 11.1-11.6_

  - [ ]* 14.2 Escribir test de propiedad para mapeo de AuditEvent
    - **Propiedad 8: Mapeo de AuditEvent preserva todos los campos**
    - Generar AuditEvents con campos aleatorios, verificar que el AuditLog persistido contiene exactamente los mismos valores
    - **Valida: Requerimiento 11.1**

  - [~] 14.3 Implementar NotificationServiceImpl
    - Implementar NotificationUseCase
    - FindByUser (paginado con filtros isRead, type), GetUnreadCount, MarkAsRead (verificar ownership → 403 si no es propia), MarkAllAsRead, Create
    - _Requerimientos: 12.1-12.7_

- [~] 15. Checkpoint - Verificar compilación de la capa de servicios
  - Asegurar que todos los servicios compilan correctamente y que las dependencias entre capas son correctas. Preguntar al usuario si surgen dudas.

- [ ] 16. Controladores REST - Módulo de Autenticación
  - [~] 16.1 Implementar AuthController (login, refresh, logout)
    - Crear en `infrastructure/adapter/in/rest/`
    - POST /api/access/v1/auth/login → público, delega a AuthUseCase.login con IP extraída
    - POST /api/access/v1/auth/refresh → público, delega a AuthUseCase.refresh
    - POST /api/access/v1/auth/logout → autenticado, delega a AuthUseCase.logout
    - Anotar con @RestController, @RequestMapping, @Valid en request bodies
    - Extraer IP de X-Forwarded-For o request.getRemoteAddr()
    - _Requerimientos: 1.1, 2.1, 3.1-3.3, 11.2, 15.5_

- [ ] 17. Controladores REST - Módulo de Usuarios
  - [~] 17.1 Implementar UserController (CRUD completo, roles, sucursales, password, perfil)
    - POST / → @RequiresPermission("USERS_CREATE"), crear usuario
    - GET / → @RequiresPermission("USERS_READ"), listar paginado con filtros
    - GET /{id} → @RequiresPermission("USERS_READ"), detalle
    - PUT /{id} → @RequiresPermission("USERS_UPDATE"), actualizar
    - DELETE /{id} → @RequiresPermission("USERS_DELETE"), soft delete
    - POST /{id}/roles → @RequiresPermission("USERS_UPDATE"), asignar roles
    - DELETE /{id}/roles/{roleId} → @RequiresPermission("USERS_UPDATE"), remover rol
    - POST /{id}/branches → @RequiresPermission("USERS_UPDATE"), asignar sucursales
    - DELETE /{id}/branches/{branchId} → @RequiresPermission("USERS_UPDATE"), remover sucursal
    - PUT /me/password → autenticado, cambiar propia contraseña
    - PUT /{id}/password → @RequiresPermission("USERS_UPDATE"), reset admin
    - GET /me → autenticado, perfil propio
    - Extraer userId del UserPrincipal (SecurityContextHolder)
    - Parámetros de paginación: page (default 0), size (default 20, max 100), sort
    - _Requerimientos: 4.1-4.11, 5.1-5.4, 14.1-14.3, 15.1-15.4, 16.1-16.4_

- [ ] 18. Controladores REST - Módulo de Roles y Permisos
  - [~] 18.1 Implementar RoleController (CRUD, asignación de permisos)
    - POST / → @RequiresPermission("CONFIG_PARAMS"), crear rol
    - GET / → @RequiresPermission("CONFIG_PARAMS"), listar paginado
    - GET /{id} → @RequiresPermission("CONFIG_PARAMS"), detalle con permisos
    - PUT /{id} → @RequiresPermission("CONFIG_PARAMS"), actualizar
    - DELETE /{id} → @RequiresPermission("CONFIG_PARAMS"), desactivar
    - PUT /{id}/permissions → @RequiresPermission("CONFIG_PARAMS"), reemplazar permisos
    - Parámetros de paginación estándar
    - _Requerimientos: 6.1-6.8, 15.1-15.4, 16.1-16.4_

  - [~] 18.2 Implementar PermissionController (listar todos, agrupar por módulo)
    - GET / → @RequiresPermission("CONFIG_PARAMS"), listar todos los permisos activos
    - GET /modules → @RequiresPermission("CONFIG_PARAMS"), permisos agrupados por módulo
    - _Requerimientos: 13.1-13.3_

- [ ] 19. Controladores REST - Módulo de Sucursales y Configuración
  - [~] 19.1 Implementar BranchController (CRUD, cambio de estado)
    - POST / → @RequiresPermission("BRANCHES_CREATE")
    - GET / → @RequiresPermission("BRANCHES_READ"), filtro isActive
    - GET /{id} → @RequiresPermission("BRANCHES_READ")
    - PUT /{id} → @RequiresPermission("BRANCHES_UPDATE")
    - PATCH /{id}/status → @RequiresPermission("BRANCHES_UPDATE")
    - Parámetros de paginación estándar
    - _Requerimientos: 7.1-7.6, 15.1-15.4, 16.1-16.4_

  - [~] 19.2 Implementar SystemParameterController (CRUD, búsqueda por clave)
    - POST / → @RequiresPermission("CONFIG_PARAMS")
    - GET / → @RequiresPermission("CONFIG_PARAMS"), filtros module/isActive
    - GET /{id} → @RequiresPermission("CONFIG_PARAMS")
    - GET /key/{key} → @RequiresPermission("CONFIG_PARAMS")
    - PUT /{id} → @RequiresPermission("CONFIG_PARAMS")
    - DELETE /{id} → @RequiresPermission("CONFIG_PARAMS")
    - _Requerimientos: 8.1-8.9, 15.1-15.4, 16.1-16.4_

  - [~] 19.3 Implementar CatalogController (CRUD, tipos)
    - POST / → @RequiresPermission("CONFIG_CATALOGS")
    - GET / → @RequiresPermission("CONFIG_CATALOGS"), parámetro catalog_type
    - GET /types → @RequiresPermission("CONFIG_CATALOGS")
    - GET /{id} → @RequiresPermission("CONFIG_CATALOGS")
    - PUT /{id} → @RequiresPermission("CONFIG_CATALOGS")
    - DELETE /{id} → @RequiresPermission("CONFIG_CATALOGS")
    - _Requerimientos: 9.1-9.8, 15.1-15.4_

  - [~] 19.4 Implementar MenuOptionController (CRUD, menú personalizado)
    - POST / → @RequiresPermission("CONFIG_PARAMS")
    - GET / → @RequiresPermission("CONFIG_PARAMS")
    - GET /{id} → @RequiresPermission("CONFIG_PARAMS")
    - PUT /{id} → @RequiresPermission("CONFIG_PARAMS")
    - DELETE /{id} → @RequiresPermission("CONFIG_PARAMS")
    - GET /my-menu → autenticado (sin permiso específico), extraer permisos del UserPrincipal
    - _Requerimientos: 10.1-10.8, 15.1-15.4_

- [ ] 20. Controladores REST - Auditoría y Notificaciones
  - [~] 20.1 Implementar AuditLogController (consulta de logs)
    - GET / → @RequiresPermission("CONFIG_PARAMS"), filtros por userId, module, entityName, dateFrom, dateTo
    - Parámetros de paginación estándar
    - _Requerimientos: 11.5-11.6, 15.1-15.4, 16.1-16.4_

  - [~] 20.2 Implementar NotificationController (CRUD de notificaciones del usuario)
    - GET / → autenticado, listar propias (filtros isRead, notificationType)
    - GET /unread-count → autenticado, conteo no leídas
    - PATCH /{id}/read → autenticado, marcar como leída (verificar ownership)
    - PATCH /read-all → autenticado, marcar todas como leídas
    - POST / → @RequiresPermission("CONFIG_PARAMS"), crear notificación (admin/servicio)
    - Extraer userId del UserPrincipal
    - _Requerimientos: 12.1-12.7, 15.1-15.5_

- [~] 21. Checkpoint - Verificar compilación completa y endpoints
  - Asegurar que todos los controladores compilan, que los endpoints están correctamente mapeados y que la aplicación arranca sin errores. Preguntar al usuario si surgen dudas.

- [ ] 22. Tests de integración
  - [ ]* 22.1 Configurar infraestructura de testing (Testcontainers, jqwik, base de tests)
    - Agregar dependencias en pom.xml: testcontainers-postgresql, jqwik
    - Crear TestcontainersConfig con PostgreSQL shared container
    - Configurar application-test.yml con propiedades de testcontainers
    - _Requerimientos: 15.1-15.7, 16.1-16.4_

  - [ ]* 22.2 Escribir tests de integración para AuthController (login, refresh, logout)
    - Test flujo completo: login exitoso → refresh → logout
    - Test credenciales inválidas → 401
    - Test cuenta desactivada → 401
    - Test refresh con token expirado → 401
    - Test detección de robo (token revocado) → 401 + revoca todos
    - _Requerimientos: 1.1-1.7, 2.1-2.5, 3.1-3.3_

  - [ ]* 22.3 Escribir tests de integración para UserController
    - Test CRUD completo de usuarios
    - Test asignación/remoción de roles y sucursales
    - Test cambio de contraseña propio y forzado
    - Test perfil /me
    - Test validaciones (duplicados → 409, no encontrado → 404)
    - Test permisos (@RequiresPermission) → 403 sin permiso
    - _Requerimientos: 4.1-4.11, 5.1-5.5, 14.1-14.3_

  - [ ]* 22.4 Escribir tests de integración para RoleController y PermissionController
    - Test CRUD completo de roles
    - Test reemplazo de permisos
    - Test listado de permisos por módulo
    - _Requerimientos: 6.1-6.8, 13.1-13.3_

  - [ ]* 22.5 Escribir tests de integración para controladores de configuración (Branch, SystemParameter, Catalog, MenuOption)
    - Test CRUD de sucursales con cambio de estado
    - Test CRUD de parámetros con búsqueda por clave
    - Test CRUD de catálogos con filtro por tipo
    - Test CRUD de opciones de menú + árbol personalizado /my-menu
    - _Requerimientos: 7.1-7.6, 8.1-8.9, 9.1-9.8, 10.1-10.8_

  - [ ]* 22.6 Escribir tests de integración para AuditLogController y NotificationController
    - Test consulta de logs con filtros
    - Test CRUD de notificaciones + verificación de ownership
    - Test marcar como leída / marcar todas
    - _Requerimientos: 11.5-11.6, 12.1-12.7_

- [~] 23. Checkpoint final - Asegurar que todos los tests pasan
  - Ejecutar mvnw test y verificar que la suite completa pasa. Preguntar al usuario si surgen dudas.

## Notas

- Las tareas marcadas con `*` son opcionales y pueden omitirse para un MVP más rápido
- Cada tarea referencia requerimientos específicos para trazabilidad
- Los checkpoints aseguran validación incremental del proyecto
- Los tests de propiedad (jqwik) validan propiedades universales de correctitud
- Los tests de integración (Testcontainers) validan flujos end-to-end contra PostgreSQL real
- El scaffold del proyecto (pom.xml, SecurityConfig, OpenApiConfig, WebConfig, FlywayConfig, application.yml) ya existe de SPEC 2
- Se reutilizan componentes de drinks-common: JwtTokenProvider, JwtAuthenticationFilter, UserPrincipal, @RequiresPermission, GlobalExceptionHandler, CorrelationIdFilter, PageResponse, AuditEvent
- Las excepciones tipadas (UnauthorizedException, ForbiddenException, ResourceNotFoundException, BusinessConflictException, ValidationException) se asume que existen en drinks-common

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2"] },
    { "id": 1, "tasks": ["2.1", "2.2", "3.1", "3.2", "3.3", "3.4", "3.5"] },
    { "id": 2, "tasks": ["4.1", "4.2", "4.3", "5.1", "5.2", "5.3"] },
    { "id": 3, "tasks": ["6.1", "6.2", "6.3"] },
    { "id": 4, "tasks": ["8.1", "8.2", "8.3", "8.4", "9.1"] },
    { "id": 5, "tasks": ["10.1", "11.1", "12.1", "12.3", "13.1", "13.2", "13.3", "13.4"] },
    { "id": 6, "tasks": ["10.2", "10.3", "11.2", "12.2", "12.4", "13.5", "14.1", "14.3"] },
    { "id": 7, "tasks": ["14.2", "16.1", "17.1", "18.1", "18.2", "19.1", "19.2", "19.3", "19.4", "20.1", "20.2"] },
    { "id": 8, "tasks": ["22.1"] },
    { "id": 9, "tasks": ["22.2", "22.3", "22.4", "22.5", "22.6"] }
  ]
}
```
