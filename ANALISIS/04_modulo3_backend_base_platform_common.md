# Módulo 3 – Backend Base, Construcción y Platform Common

## Análisis Detallado Archivo por Archivo

### 1. `services/Dockerfile`
- **Ruta:** `services/Dockerfile`
- **Responsabilidad:** Construcción y empaquetado multi-etapa de contenedores Docker para los microservicios de Spring Boot.
- **Líneas clave:** 
  - L1-4: Etapa de build con imagen `maven:3.9.9-eclipse-temurin-17`, copiando `services/` y ejecutando `mvn -B -f services/pom.xml package -DskipTests`.
  - L5-11: Etapa final con `eclipse-temurin:17-jre`, argumento `ARG SERVICE`, usuario no privilegiado `USER 10001` y configuración de memoria `ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60"`.
- **Dependencias:** Docker, Maven, Eclipse Temurin 17.
- **Problemas detectados:** Buena práctica de seguridad ejecutando como usuario no-root (`UID 10001`) y limitando heap al 60% de la RAM del contenedor.

### 2. `services/mvnw`
- **Ruta:** `services/mvnw`
- **Responsabilidad:** Wrapper oficial de Maven para entornos POSIX/Linux/macOS que descarga la versión configurada de Maven si no está en el sistema.
- **Líneas clave:** L1-280 (script bash estándar de Apache Maven Wrapper).
- **Problemas detectados:** Ninguno.

### 3. `services/mvnw.cmd`
- **Ruta:** `services/mvnw.cmd`
- **Responsabilidad:** Wrapper oficial de Maven para entornos Windows CMD/PowerShell.
- **Líneas clave:** L1-180 (script batch estándar de Apache Maven Wrapper).
- **Problemas detectados:** Ninguno.

### 4. `services/pom.xml`
- **Ruta:** `services/pom.xml`
- **Responsabilidad:** POM agregador raíz que orquesta la construcción de los 13 submódulos del backend distribuido.
- **Líneas clave:** L1 (definición de módulos: `platform-common`, `user-service`, `customer-service`, `supplier-service`, `catalog-service`, `cart-service`, `order-service`, `inventory-service`, `payment-service`, `notification-service`, `admin-service`, `config-server`, `gateway`).
- **Problemas detectados:** El archivo está compactado en 2 líneas. No define una sección `<dependencyManagement>` centralizada para fijar versiones de Spring Boot o Spring Cloud de forma unificada; en su lugar, cada submódulo hereda de `spring-boot-starter-parent:3.1.3` individualmente.

### 5. `services/.mvn/wrapper/maven-wrapper.properties`
- **Ruta:** `services/.mvn/wrapper/maven-wrapper.properties`
- **Responsabilidad:** Configuración de la URL de descarga y versión del binario de Maven.
- **Líneas clave:** L1 (`wrapperVersion=3.3.4`), L3 (`distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip`).
- **Problemas detectados:** Ninguno.

### 6. `services/platform-common/pom.xml`
- **Ruta:** `services/platform-common/pom.xml`
- **Responsabilidad:** Configuración Maven de la biblioteca compartida entre microservicios.
- **Líneas clave:** L2 (`spring-boot-starter-parent: 3.1.3`), L3 (dependencias: `spring-boot-starter-web`, `oauth2-resource-server`, `validation`, `data-jpa`, `test`, `spring-security-test`), L4 (`<testSourceDirectory>test</testSourceDirectory>`).
- **Problemas detectados:** Ninguno crítico.

### 7. `services/platform-common/README.md`
- **Ruta:** `services/platform-common/README.md`
- **Responsabilidad:** Descripción de la librería `platform-common`.
- **Líneas clave:** L5 (comando para compilar módulo aislado), L7 (aclaración sobre persistencia H2 para tests y ausencia de base de datos propia para este módulo).
- **Problemas detectados:** Ninguno.

### 8. `services/platform-common/src/main/java/com/uelbosque/platformcommon/ApiErrors.java`
- **Ruta:** `services/platform-common/src/main/java/com/uelbosque/platformcommon/ApiErrors.java`
- **Responsabilidad:** Manejo global de excepciones para todos los microservicios que consumen la librería.
- **Líneas clave:** 
  - L12-13: `@RestControllerAdvice` con `@Order(Ordered.HIGHEST_PRECEDENCE)`.
  - L15-18: Manejo de `ResponseStatusException`.
  - L19-25: Manejo de errores de validación y deserialización (`MethodArgumentNotValidException`, `IllegalArgumentException`, `HttpMessageNotReadableException`, etc.) devolviendo HTTP 400.
  - L26-29: Manejo de integridad de datos (`DataIntegrityViolationException`, `OptimisticLockingFailureException`) devolviendo HTTP 409 Conflicto.
  - L30-33: Manejo de fallos en llamadas HTTP dependientes (`RestClientException`) devolviendo HTTP 503 Servicio No Disponible.
- **Problemas detectados:** Excelente implementación de mapeo uniforme de códigos de estado HTTP para sistemas distribuidos. Cumple estrictamente la regla de <= 20 líneas por método.

### 9. `services/platform-common/src/main/java/com/uelbosque/platformcommon/ApiSecurity.java`
- **Ruta:** `services/platform-common/src/main/java/com/uelbosque/platformcommon/ApiSecurity.java`
- **Responsabilidad:** Configuración transversal de seguridad Spring Security con decodificador de tokens JWT (HS256) y filtros de autorización basados en roles.
- **Líneas clave:** 
  - L18-21: `JwtDecoder` bean con clave simétrica `HmacSHA256` (`app.jwt.secret`).
  - L23: CSRF deshabilitado y sesiones sin estado (`SessionCreationPolicy.STATELESS`).
  - L24-28: Autorizaciones: `/actuator/health` público, `POST /api/orders` explícitamente denegado (bloqueo de endpoint vulnerable heredado), `GET /api/**` para ADMIN/USER, `POST/PUT` en ventas, carrito y pagos para ADMIN/USER, y el resto para ADMIN.
  - L32-38: Conversor de roles desde el claim `roles` (separados por coma) a `SimpleGrantedAuthority`.
- **Problemas detectados:** 
  - Al usar algoritmo simétrico HS256 (`HmacSHA256`), todos los microservicios deben compartir exactamente el mismo secreto (`JWT_SECRET`). Si la clave se compromete en un servicio, se compromete en toda la malla. (En arquitecturas más complejas se recomienda par de claves asimétricas RSA/EC RS256, aunque para este proyecto académico HS256 cumple los requisitos de centralización).

### 10. `services/platform-common/src/main/java/com/uelbosque/platformcommon/PlatformConfiguration.java`
- **Ruta:** `services/platform-common/src/main/java/com/uelbosque/platformcommon/PlatformConfiguration.java`
- **Responsabilidad:** Auto-configuración de Spring Boot que expone `RestTemplate` con timeouts y propagación automática de Bearer JWT.
- **Líneas clave:** 
  - L11: `@AutoConfiguration`.
  - L12: `@Import({ApiErrors.class, ApiSecurity.class})`.
  - L14-23: Bean `RestTemplate` con timeout de conexión de 3s, timeout de lectura de 5s, e interceptor que extrae el token JWT del `SecurityContextHolder` y lo inyecta en el encabezado `Authorization` de las peticiones salientes.
- **Problemas detectados:** Muy buena implementación para propagación de identidad distribuida.

### 11. `services/platform-common/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- **Ruta:** `services/platform-common/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- **Responsabilidad:** Registro de auto-configuración para Spring Boot 3.x (reemplazo de `spring.factories`).
- **Líneas clave:** L1 (`com.uelbosque.platformcommon.PlatformConfiguration`).
- **Problemas detectados:** Cumple con el estándar moderno de Spring Boot 3.

### 12. `services/platform-common/test/com/uelbosque/platformcommon/ApiSecurityTest.java`
- **Ruta:** `services/platform-common/test/com/uelbosque/platformcommon/ApiSecurityTest.java`
- **Responsabilidad:** Suite de pruebas unitarias/integración para validar el comportamiento de seguridad JWT y los filtros de acceso.
- **Líneas clave:** 
  - L28-32: `verifiesSignatureAndExpiration` (valida token válido 200, expirado 401, y malformado 401).
  - L33-36: `requiresAdminForManagement` (valida que POST requiere `ROLE_ADMIN`, rechazando `ROLE_USER` con 403).
  - L37-42: Generador de tokens JWT de prueba con Nimbus JOSE.
- **Problemas detectados:** Pruebas rigurosas y efectivas.
