import os

content = """# Módulo 5 – User Service (`services/user-service`)

## Análisis Detallado Archivo por Archivo (31 archivos)

### 1. `services/user-service/Dockerfile`
- **Ruta:** `services/user-service/Dockerfile`
- **Responsabilidad:** Construcción en contenedor Docker multi-stage del servicio `user-service`.
- **Líneas clave:** L5 (`mvn package -pl user-service`), L9 (`USER 10001`).
- **Problemas detectados:** Ninguno.

### 2. `services/user-service/README.md`
- **Ruta:** `services/user-service/README.md`
- **Responsabilidad:** Guía de ejecución y compilación de `user-service`.
- **Líneas clave:** L5 (compilación individual con `./services/mvnw.cmd`), L7 (PostgreSQL en ejecución, H2 en pruebas).
- **Problemas detectados:** Ninguno.

### 3. `services/user-service/pom.xml`
- **Ruta:** `services/user-service/pom.xml`
- **Responsabilidad:** Descriptor Maven del servicio de usuarios y autenticación.
- **Líneas clave:** L13 (`spring-boot-starter-parent:3.1.3`), L21-38 (dependencias: `spring-cloud-starter-config`, `spring-boot-starter-web`, `security`, `data-jpa`, `validation`, `nimbus-jose-jwt:9.31`, `h2`, `postgresql`), L66-70 (`build-helper-maven-plugin` agregando `test/`).
- **Problemas detectados:** Ninguno.

### 4. `services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java`
- **Responsabilidad:** (Antigua / Obsoleta) Inicialización de usuario administrador inicial.
- **Líneas clave:** L21-27 (`init` con `@EventListener(ApplicationReadyEvent.class)`).
- **Problemas detectados (CRÍTICO):**
  - **Error de compilación real:** En la línea 24 invoca `new User(defaultUsername, encoder.encode("admin123456"), "ROLE_ADMIN")`. Dicho constructor de 3 argumentos NO existe en `User.java` (solo existen el constructor vacío y el constructor de 5 argumentos `User(username, password, name, email, roles)`).
  - Como resultado, la compilación de Maven falla con: `[ERROR] no suitable constructor found for User(java.lang.String,java.lang.String,java.lang.String)`.
  - Este archivo es un remanente huérfano que fue sustituido por `com.uelbosque.userservice.config.DataInitializer.java`. Debe ser eliminado inmediatamente.

### 5. `services/user-service/src/main/java/com/uelbosque/userservice/UserServiceApplication.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/UserServiceApplication.java`
- **Responsabilidad:** Clase principal con método `main` para arrancar Spring Boot en `user-service`.
- **Líneas clave:** L6 (`@SpringBootApplication`), L10 (`SpringApplication.run`).
- **Problemas detectados:** Ninguno.

### 6. `services/user-service/src/main/java/com/uelbosque/userservice/config/DataInitializer.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/config/DataInitializer.java`
- **Responsabilidad:** Inicializador oficial y canónico del administrador inicial de la tienda.
- **Líneas clave:** L15-18 (`if (users.count() != 0) return; User admin = new User("admininicial", encoder.encode("admin123456"), "Administrador inicial", "admin@uelbosque.edu.co", "ROLE_ADMIN,ROLE_USER"); admin.setCedula("0"); users.save(admin);`).
- **Problemas detectados:** Ninguno; implementa adecuadamente los requerimientos (cédula "0", roles combinados, idempotencia al verificar `count() != 0`).

### 7. `services/user-service/src/main/java/com/uelbosque/userservice/config/SecurityConfig.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/config/SecurityConfig.java`
- **Responsabilidad:** Configuración de Spring Security local para `user-service`.
- **Líneas clave:** 
  - L31-33: CSRF deshabilitado, sesión STATELESS, frameOptions deshabilitado.
  - L35-38: `/api/users/login` público, `/api/users/**` autenticado para GET, `/actuator/health` público, cualquier otra petición requiere `ROLE_ADMIN`.
  - L41: Registra `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`.
  - L46-48: Bean `BCryptPasswordEncoder`.
- **Problemas detectados:** Configuración sólida y bien delimitada.

### 8. `services/user-service/src/main/java/com/uelbosque/userservice/controller/UserController.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/controller/UserController.java`
- **Responsabilidad:** Controlador REST para CRUD de usuarios y login.
- **Líneas clave:** 
  - L34-38: `POST /api/users` (Creación).
  - L40-44: `POST /api/users/login` (Autenticación y retorno de token JWT).
  - L46-49: `GET /api/users` (Listado).
  - L51-52: `GET /api/users/cedula/{cedula}` (Consulta por cédula para otros microservicios).
  - L54-57: `GET /api/users/{id}` (Consulta por ID).
  - L59-62: `GET /api/users/username/{username}`.
  - L64-67: `PUT /api/users/{id}` (Actualización).
  - L69-72: `DELETE /api/users/{id}` (Eliminación protegida).
- **Problemas detectados:** Métodos breves y delegación limpia al servicio.

### 9. `services/user-service/src/main/java/com/uelbosque/userservice/dto/AuthResponse.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/dto/AuthResponse.java`
- **Responsabilidad:** DTO de respuesta para login exitoso conteniendo token JWT y datos públicos del usuario.
- **Líneas clave:** L5-7 (`token`, `tokenType = "Bearer"`, `user`).
- **Problemas detectados:** Ninguno.

### 10. `services/user-service/src/main/java/com/uelbosque/userservice/dto/CreateUserRequest.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/dto/CreateUserRequest.java`
- **Responsabilidad:** DTO de entrada para alta de usuarios con validaciones Bean Validation.
- **Líneas clave:** L8 (`@NotBlank @Pattern(regexp="[0-9]{1,20}") String cedula`), L12-14 (`username` 4-50 chars), L16-18 (`password` min 6 chars), L20-22 (`name`), L24-26 (`@Email email`), L28-29 (patrón de roles).
- **Problemas detectados:** Excelente validación contra inyección y formatos inválidos.

### 11. `services/user-service/src/main/java/com/uelbosque/userservice/dto/LoginRequest.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/dto/LoginRequest.java`
- **Responsabilidad:** DTO de credenciales para login (`username` y `password`).
- **Líneas clave:** L7-11 (`@NotBlank` en ambos campos).
- **Problemas detectados:** Ninguno.

### 12. `services/user-service/src/main/java/com/uelbosque/userservice/dto/UpdateUserRequest.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/dto/UpdateUserRequest.java`
- **Responsabilidad:** DTO para actualizar usuarios (campos opcionales).
- **Líneas clave:** L7 (`username`), L11 (`name`), L14 (`email`), L17 (`password`), L20 (`roles`), L23 (`enabled`).
- **Problemas detectados:** No permite modificar la cédula (diseño intencional e inmutable).

### 13. `services/user-service/src/main/java/com/uelbosque/userservice/dto/UserResponse.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/dto/UserResponse.java`
- **Responsabilidad:** DTO de salida de usuario para clientes y reportes.
- **Líneas clave:** L4 (`cedula`), L8-13 (`id`, `username`, `name`, `email`, `roles`, `enabled`).
- **Problemas detectados:** Nunca incluye el campo `password`, previniendo fugas de hash.

### 14. `services/user-service/src/main/java/com/uelbosque/userservice/exception/DuplicateResourceException.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/exception/DuplicateResourceException.java`
- **Responsabilidad:** Excepción lanzada cuando un recurso único (cédula, username, email) ya existe, o cuando una regla de negocio de unicidad/integridad se viola.
- **Líneas clave:** L3 (`extends RuntimeException`).
- **Problemas detectados:** Ninguno.

### 15. `services/user-service/src/main/java/com/uelbosque/userservice/exception/GlobalExceptionHandler.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/exception/GlobalExceptionHandler.java`
- **Responsabilidad:** Captura y formateo de errores REST para `user-service`.
- **Líneas clave:** L18-25 (validation 400), L27-30 (duplicate 409), L32-35 (not found 404), L37-40 (bad credentials 401), L42-45 (illegal argument 400).
- **Problemas detectados:** Mapeo correcto de códigos de estado HTTP.

### 16. `services/user-service/src/main/java/com/uelbosque/userservice/exception/ResourceNotFoundException.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/exception/ResourceNotFoundException.java`
- **Responsabilidad:** Excepción para entidades no encontradas (mapeada a HTTP 404).
- **Líneas clave:** L3 (`extends RuntimeException`).
- **Problemas detectados:** Ninguno.

### 17. `services/user-service/src/main/java/com/uelbosque/userservice/model/User.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/model/User.java`
- **Responsabilidad:** Entidad JPA representativa de la tabla `users`.
- **Líneas clave:** L10-12 (`@Entity @Table(name="users")`), L13 (`@Column(unique=true) private String cedula`), L17-19 (`@Id @GeneratedValue IDENTITY`), L21-37 (`username`, `password`, `name`, `email`, `roles`, `enabled = true`).
- **Problemas detectados:** El campo `cedula` fue agregado arriba en L13 sin actualizar el constructor de 3 parámetros que esperaba el antiguo `DataInitializer`.

### 18. `services/user-service/src/main/java/com/uelbosque/userservice/repository/UserRepository.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/repository/UserRepository.java`
- **Responsabilidad:** Repositorio Spring Data JPA para la entidad `User`.
- **Líneas clave:** L10-17 (`findByCedula`, `existsByCedula`, `findByUsername`, `existsByUsername`, `findByEmail`, `existsByEmail`).
- **Problemas detectados:** Índices y métodos derivados claros.

### 19. `services/user-service/src/main/java/com/uelbosque/userservice/security/JwtAuthenticationFilter.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/security/JwtAuthenticationFilter.java`
- **Responsabilidad:** Filtro `OncePerRequestFilter` para autenticar tokens JWT entrantes.
- **Líneas clave:** L32-39 (`doFilterInternal`), L41-55 (`authenticate`: verifica token y comprueba en base de datos `users.findByUsername` que el usuario exista y esté `isEnabled()`).
- **Problemas detectados:** Excelente mecanismo de seguridad con verificación activa de estado.

### 20. `services/user-service/src/main/java/com/uelbosque/userservice/security/JwtTokenProvider.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/security/JwtTokenProvider.java`
- **Responsabilidad:** Generación, firma y validación de tokens JWT mediante Nimbus JOSE JWT con algoritmo HS256.
- **Líneas clave:** L30-48 (`generateToken`), L50-62 (`validateToken`), L64-70 (`getUsernameFromToken`), L72-79 (`getRolesFromToken`).
- **Problemas detectados:** Ninguno.

### 21. `services/user-service/src/main/java/com/uelbosque/userservice/service/DatabaseUserDetailsService.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/service/DatabaseUserDetailsService.java`
- **Responsabilidad:** Implementación de `UserDetailsService` de Spring Security consultando la base de datos PostgreSQL.
- **Líneas clave:** L25-38 (`loadUserByUsername`: busca usuario, parsea roles separados por coma a `SimpleGrantedAuthority`, construye `UserDetails`).
- **Problemas detectados:** Ninguno.

### 22. `services/user-service/src/main/java/com/uelbosque/userservice/service/UserService.java`
- **Ruta:** `services/user-service/src/main/java/com/uelbosque/userservice/service/UserService.java`
- **Responsabilidad:** Lógica de negocio de usuarios (registro, autenticación, protección del último administrador, validación de cédulas, actualización y borrado).
- **Líneas clave:** 
  - L35-47: `createUser` (valida cédula, username, email únicos; encripta password).
  - L49-60: `authenticate` (valida password con BCrypt, verifica cuenta habilitada, emite token JWT).
  - L81-87: `updateUser`.
  - L107-114: `deleteUser`.
  - L116-119: `validateCedula` (formato numérico 1-20 dígitos y unicidad).
  - L131-138: `protectLastAdmin` (impide eliminar o degradar al último administrador activo del sistema).
- **Problemas detectados:** Cumple con todas las reglas de negocio descritas en la especificación.

### 23. `services/user-service/src/main/resources/application-distributed.yml`
- **Ruta:** `services/user-service/src/main/resources/application-distributed.yml`
- **Responsabilidad:** Conexión opcional con Config Server bajo el perfil `distributed`.
- **Líneas clave:** L3 (`optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}`).
- **Problemas detectados:** Ninguno.

### 24. `services/user-service/src/main/resources/application.yml`
- **Ruta:** `services/user-service/src/main/resources/application.yml`
- **Responsabilidad:** Configuración principal de `user-service` (puerto 8081, datasource PostgreSQL, expiración JWT de 900.000 ms = 15 min).
- **Líneas clave:** L2 (`port: 8081`), L10 (`jdbc:postgresql://localhost:5432/users`), L24 (`expiration-ms: 900000`).
- **Problemas detectados:** Configuración correcta.

### 25. `services/user-service/src/test/java/com/uelbosque/userservice/controller/UserControllerTest.java`
- **Ruta:** `services/user-service/src/test/java/com/uelbosque/userservice/controller/UserControllerTest.java`
- **Responsabilidad:** Pruebas unitarias de capa web con `@WebMvcTest(UserController.class)` y `MockMvc`.
- **Líneas clave:** L54-60 (registro exitoso 201), validación de campos obligatorios, login y endpoints de consulta.
- **Problemas detectados:** Pruebas aisladas y completas.

### 26. `services/user-service/src/test/java/com/uelbosque/userservice/repository/UserRepositoryTest.java`
- **Ruta:** `services/user-service/src/test/java/com/uelbosque/userservice/repository/UserRepositoryTest.java`
- **Responsabilidad:** Pruebas de integración de capa de datos con `@DataJpaTest` sobre H2.
- **Líneas clave:** L28-55 (búsqueda por username, email, existsByUsername, existsByEmail).
- **Problemas detectados:** Ninguno.

### 27. `services/user-service/src/test/java/com/uelbosque/userservice/security/JwtTokenProviderTest.java`
- **Ruta:** `services/user-service/src/test/java/com/uelbosque/userservice/security/JwtTokenProviderTest.java`
- **Responsabilidad:** Pruebas unitarias para generación, extracción de claims y validación de tokens JWT.
- **Líneas clave:** L22-27 (generación y validación), L30-36 (extracción de claims), L39-43 (rechazo de tokens corruptos o nulos).
- **Problemas detectados:** Ninguno.

### 28. `services/user-service/src/test/java/com/uelbosque/userservice/service/DatabaseUserDetailsServiceTest.java`
- **Ruta:** `services/user-service/src/test/java/com/uelbosque/userservice/service/DatabaseUserDetailsServiceTest.java`
- **Responsabilidad:** Pruebas unitarias con Mockito para `DatabaseUserDetailsService`.
- **Líneas clave:** L29-39 (carga exitosa y mapeo de authorities), L42-47 (excepción si no existe).
- **Problemas detectados:** Ninguno.

### 29. `services/user-service/src/test/java/com/uelbosque/userservice/service/UserServiceTest.java`
- **Ruta:** `services/user-service/src/test/java/com/uelbosque/userservice/service/UserServiceTest.java`
- **Responsabilidad:** Pruebas unitarias completas de la lógica de negocio de `UserService`.
- **Líneas clave:** Creación, login, búsqueda, actualización y manejo de duplicados.
- **Problemas detectados:** Pruebas rigurosas.

### 30. `services/user-service/src/test/resources/application.properties`
- **Ruta:** `services/user-service/src/test/resources/application.properties`
- **Responsabilidad:** Configuración de base de datos H2 en memoria y clave JWT dummy para tests.
- **Líneas clave:** L1 (`jdbc:h2:mem:users`), L6 (`app.jwt.secret`).
- **Problemas detectados:** Ninguno.

### 31. `services/user-service/test/com/uelbosque/userservice/UserSecurityTest.java`
- **Ruta:** `services/user-service/test/com/uelbosque/userservice/UserSecurityTest.java`
- **Responsabilidad:** Pruebas de integración de seguridad de endpoints.
- **Líneas clave:** 
  - L14-16: Anónimo no puede crear administradores (401).
  - L17-19: Operador con rol `USER` no puede gestionar usuarios (403).
  - L20-23: Bootstrap inicial tiene cédula "0" y no expone password.
  - L24-27: Contraseña inválida es rechazada con 401.
- **Problemas detectados:** Excelente cobertura de requisitos de seguridad.
"""

with open('ANALISIS/06_modulo5_user_service.md', 'w', encoding='utf-8') as f:
    f.write(content)

print('Módulo 5 guardado en ANALISIS/06_modulo5_user_service.md')
