# Módulo 4 – Config Server y API Gateway

## Análisis Detallado Archivo por Archivo

### Submódulo: `services/config-server` (8 archivos)

#### 1. `services/config-server/Dockerfile`
- **Ruta:** `services/config-server/Dockerfile`
- **Responsabilidad:** Construcción en dos etapas del contenedor del servidor de configuración.
- **Líneas clave:** L2-5 (etapa de compilación Maven), L6-10 (etapa de ejecución JRE 17 con usuario 10001).
- **Problemas detectados:** Ninguno.

#### 2. `services/config-server/README.md`
- **Ruta:** `services/config-server/README.md`
- **Responsabilidad:** Instrucciones para compilar y ejecutar `config-server`.
- **Líneas clave:** L5 (orden de verificación con Maven), L7 (aclara que no utiliza base de datos relacional).
- **Problemas detectados:** Ninguno.

#### 3. `services/config-server/pom.xml`
- **Ruta:** `services/config-server/pom.xml`
- **Responsabilidad:** Configuración Maven para el servidor de configuración central.
- **Líneas clave:** L2 (`spring-boot-starter-parent:3.1.3`), L3 (`spring-cloud-config-server:4.0.4`, `spring-boot-starter-security`, `spring-boot-starter-actuator`), L4 (`<testSourceDirectory>test</testSourceDirectory>`).
- **Problemas detectados:** Ninguno.

#### 4. `services/config-server/src/main/java/com/uelbosque/configserver/ConfigSecurity.java`
- **Ruta:** `services/config-server/src/main/java/com/uelbosque/configserver/ConfigSecurity.java`
- **Responsabilidad:** Asegurar los endpoints del Config Server mediante HTTP Basic Authentication.
- **Líneas clave:** L8-11: CSRF deshabilitado, `/actuator/health` público, cualquier otra solicitud requiere autenticación HTTP Basic.
- **Problemas detectados:** Utiliza HTTP Basic. Adecuado para comunicación interna entre microservicios protegidos en la red privada de Docker.

#### 5. `services/config-server/src/main/java/com/uelbosque/configserver/ConfigServerApplication.java`
- **Ruta:** `services/config-server/src/main/java/com/uelbosque/configserver/ConfigServerApplication.java`
- **Responsabilidad:** Clase principal de arranque de Spring Cloud Config Server.
- **Líneas clave:** L6 (`@SpringBootApplication @EnableConfigServer`), L8 (`main`).
- **Problemas detectados:** Cumple con la directriz de brevedad y simplicidad.

#### 6. `services/config-server/src/main/resources/application.yml`
- **Ruta:** `services/config-server/src/main/resources/application.yml`
- **Responsabilidad:** Configuración del servidor Config Server.
- **Líneas clave:** L2 (`port: 8888`), L6-7 (`profiles.active: native`), L10-12 (`server.native.search-locations: classpath:/config`), L13-16 (`security.user.name: config`, `password: ${CONFIG_PASSWORD}`).
- **Problemas detectados:** Usa perfil `native` (archivos YAML locales en classpath) en lugar de un repositorio Git externo. Para este proyecto es la opción adecuada por simplicidad y autonomía local (KISS).

#### 7. `services/config-server/src/main/resources/config/application.yml`
- **Ruta:** `services/config-server/src/main/resources/config/application.yml`
- **Responsabilidad:** Propiedades globales compartidas que se distribuyen a todos los clientes que se conectan al Config Server.
- **Líneas clave:** L1-5 (exposición de actuator health e info), L6-8 (`spring.jpa.open-in-view: false`).
- **Problemas detectados:** Muy buena práctica desactivar Open Session In View (`open-in-view: false`) para evitar anti-patrones N+1 en microservicios.

#### 8. `services/config-server/test/com/uelbosque/configserver/ConfigServerTest.java`
- **Ruta:** `services/config-server/test/com/uelbosque/configserver/ConfigServerTest.java`
- **Responsabilidad:** Pruebas de integración del servidor de configuración.
- **Líneas clave:** L14-17 (verifica entrega de propiedades con usuario autenticado), L18 (verifica rechazo 401 si no hay credenciales).
- **Problemas detectados:** Ninguno.

---

### Submódulo: `services/gateway` (12 archivos)

#### 9. `services/gateway/Dockerfile`
- **Ruta:** `services/gateway/Dockerfile`
- **Responsabilidad:** Imagen Docker multi-etapa para API Gateway.
- **Líneas clave:** L5 (`mvn package -pl gateway`), L9 (`USER 10001`).
- **Problemas detectados:** Ninguno.

#### 10. `services/gateway/README.md`
- **Ruta:** `services/gateway/README.md`
- **Responsabilidad:** Documentación de ejecución del Gateway.
- **Líneas clave:** L5 (compilación aislada), L7 (aclara ausencia de BD propia).
- **Problemas detectados:** Ninguno.

#### 11. `services/gateway/pom.xml`
- **Ruta:** `services/gateway/pom.xml`
- **Responsabilidad:** Configuración Maven para Spring Cloud Gateway reactivo.
- **Líneas clave:** L24-26 (`spring-cloud-starter-gateway`), L28-30 (`spring-boot-starter-actuator`), L38-48 (`spring-cloud-dependencies:2022.0.4`).
- **Problemas detectados:** No incluye `spring-boot-starter-web` porque Spring Cloud Gateway utiliza WebFlux / Netty y colisionaría con el Tomcat bloqueante tradicional.

#### 12. `services/gateway/src/main/java/com/uelbosque/gateway/ApiGatewayApplication.java`
- **Ruta:** `services/gateway/src/main/java/com/uelbosque/gateway/ApiGatewayApplication.java`
- **Responsabilidad:** Punto de entrada de la aplicación Spring Cloud Gateway.
- **Líneas clave:** L6 (`@SpringBootApplication`), L9-11 (`main`).
- **Problemas detectados:** Ninguno.

#### 13. `services/gateway/src/main/java/com/uelbosque/gateway/DownstreamErrorHandler.java`
- **Ruta:** `services/gateway/src/main/java/com/uelbosque/gateway/DownstreamErrorHandler.java`
- **Responsabilidad:** Manejador reactivo global de excepciones para downstream inaccesibles o en timeout.
- **Líneas clave:** L11 (`@Component @Order(-2)`), L14-20 (devuelve 503 SERVICE_UNAVAILABLE con JSON `{"message":"Servicio no disponible"}`), L21-24 (detecta recursivamente `ConnectException` y `TimeoutException`).
- **Problemas detectados:** Excelente mecanismo de resiliencia para no exponer trazas internas de red al cliente.

#### 14. `services/gateway/src/main/java/com/uelbosque/gateway/config/CorsConfig.java`
- **Ruta:** `services/gateway/src/main/java/com/uelbosque/gateway/config/CorsConfig.java`
- **Responsabilidad:** Configuración CORS reactiva para clientes web.
- **Líneas clave:** L17 (`setAllowedOrigins(List.of("http://localhost:4200"))`), L18 (métodos permitidos), L19 (`allowedHeaders: *`), L20 (`allowCredentials: true`).
- **Problemas detectados:** El origen está cableado a `http://localhost:4200`. Si el frontend se ejecuta en otro puerto o dominio en producción, las peticiones serán bloqueadas por CORS. Se recomienda parametrizar con variable de entorno.

#### 15. `services/gateway/src/main/java/com/uelbosque/gateway/controller/GatewayHealthController.java`
- **Ruta:** `services/gateway/src/main/java/com/uelbosque/gateway/controller/GatewayHealthController.java`
- **Responsabilidad:** Controladores de verificación de salud y respuesta de fallback.
- **Líneas clave:** L15-29 (endpoint `/api/gateway/health`), L31-38 (endpoint `/fallback` devolviendo 503).
- **Problemas detectados:** La respuesta de L21-26 en `/api/gateway/health` devuelve un mapa de rutas estático que solo incluye 4 servicios (`userService`, `catalogService`, `cartService`, `orderService`), omitiendo los otros 6 servicios enrutados.

#### 16. `services/gateway/src/main/resources/application-distributed.yml`
- **Ruta:** `services/gateway/src/main/resources/application-distributed.yml`
- **Responsabilidad:** Configuración para conectar el Gateway con el Config Server.
- **Líneas clave:** L3 (`import: optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}`).
- **Problemas detectados:** El modificador `optional:` permite que el Gateway arranque incluso si Config Server se encuentra temporalmente indisponible.

#### 17. `services/gateway/src/main/resources/application.yml`
- **Ruta:** `services/gateway/src/main/resources/application.yml`
- **Responsabilidad:** Definición de puertos, timeouts y las 10 rutas maestras de los microservicios.
- **Líneas clave:** 
  - L2: puerto 8080.
  - L10-12: connect-timeout 3000ms, response-timeout 10s.
  - L13-53: Rutas hacia `user-service` (`/api/users/**`), `catalog-service` (`/api/products/**`), `cart-service` (`/api/cart/**`), `order-service` (`/api/orders/**`, `/api/sales/**`), `customer-service` (`/api/customers/**`), `supplier-service` (`/api/suppliers/**`), `inventory-service` (`/api/inventory/**`), `payment-service` (`/api/payments/**`), `notification-service` (`/api/notifications/**`), `admin-service` (`/api/reports/**`, `/api/admin/**`).
- **Problemas detectados:** Cobertura de rutas completa para todos los microservicios del sistema.

#### 18. `services/gateway/src/test/java/com/uelbosque/gateway/GatewayApplicationTests.java`
- **Ruta:** `services/gateway/src/test/java/com/uelbosque/gateway/GatewayApplicationTests.java`
- **Responsabilidad:** Smoke test para verificar carga del ApplicationContext de Spring Boot.
- **Líneas clave:** L10-13 (`contextLoads`).
- **Problemas detectados:** Ninguno.

#### 19. `services/gateway/src/test/java/com/uelbosque/gateway/GatewayCorsTest.java`
- **Ruta:** `services/gateway/src/test/java/com/uelbosque/gateway/GatewayCorsTest.java`
- **Responsabilidad:** Probar el preflight y encabezados CORS usando `WebTestClient`.
- **Líneas clave:** L18-26 (verifica status 200 y `Access-Control-Allow-Origin: http://localhost:4200` ante petición OPTIONS).
- **Problemas detectados:** Prueba reactiva bien implementada sin levantar puerto físico del sistema.

#### 20. `services/gateway/src/test/java/com/uelbosque/gateway/GatewayRoutesTest.java`
- **Ruta:** `services/gateway/src/test/java/com/uelbosque/gateway/GatewayRoutesTest.java`
- **Responsabilidad:** Validar que todas las rutas esperadas estén registradas en el `RouteLocator` y probar endpoints de salud y fallback.
- **Líneas clave:** L28-39 (verifica registro de las 10 rutas), L41-54 (verifica `/api/gateway/health`), L56-65 (verifica `/fallback` con status 503).
- **Problemas detectados:** Pruebas exhaustivas y coherentes.
