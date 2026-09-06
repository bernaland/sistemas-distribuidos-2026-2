# API Gateway Service

Punto único de entrada (Single Point of Contact) para el front-end y clientes externos, implementado con **Spring Cloud Gateway** (arquitectura reactiva no bloqueante sobre Netty).

## Tecnologías
- Java 17
- Spring Boot 3.1.3
- Spring Cloud Gateway 4.0.7 (Spring Cloud 2022.0.4)
- JUnit 5 / Spring Boot Test / WebTestClient

## Puerto
- **Puerto:** `8080`

## Rutas Enrutadas hacia Microservicios
| Microservicio | Ruta Expuesta en Gateway | Destino |
|---|---|---|
| `user-service` | `/api/users/**` | `http://localhost:8081` |
| `catalog-service` | `/api/products/**` | `http://localhost:8082` |
| `cart-service` | `/api/cart/**` | `http://localhost:8084` |
| `order-service` | `/api/orders/**` | `http://localhost:8085` |

## Endpoints Propios del Gateway
- `GET /api/gateway/health`: Chequeo de estado y catálogo de rutas disponibles del Gateway.
- `GET /fallback`: Respuesta de fallback estándar (`503 Service Unavailable`) en caso de caída de microservicios dependientes.
- `GET /actuator/health`: Chequeo de salud del actuator.

## Configuración de CORS
Configuración reactiva mediante `CorsWebFilter` (`com.uelbosque.gateway.config.CorsConfig`):
- **Orígenes permitidos:** `http://localhost:4200` (Frontend Angular)
- **Métodos:** `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`, `HEAD`
- **Cabeceras:** `*`
- **Credenciales:** Habilitadas (`allowCredentials: true`)
- **Max Age:** 3600 segundos

## Pruebas Unitarias e Integración
Para ejecutar la suite de pruebas:
```bash
mvn clean test -f services/gateway/pom.xml
```
Pruebas implementadas:
1. `GatewayApplicationTests`: Inicialización del contexto Spring Boot.
2. `GatewayRoutesTest`: Verificación de las 4 rutas registradas, endpoint de salud `/api/gateway/health` y `/fallback`.
3. `GatewayCorsTest`: Verificación de cabeceras CORS y preflight OPTIONS para `http://localhost:4200`.
