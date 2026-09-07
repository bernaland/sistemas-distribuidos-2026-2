# Validación del backend

Validación realizada con Java 21 compilando para Java 17 y Maven Wrapper 3.9.9. Se ejecutó la suite completa y se volvió a verificar order-service después de normalizar la precisión decimal de las respuestas.

| Servicio | Pruebas | Fallos | Errores | Omitidas |
|---|---:|---:|---:|---:|
| admin-service | 5 | 0 | 0 | 0 |
| cart-service | 20 | 0 | 0 | 0 |
| catalog-service | 21 | 0 | 0 | 0 |
| config-server | 2 | 0 | 0 | 0 |
| customer-service | 4 | 0 | 0 | 0 |
| gateway | 5 | 0 | 0 | 0 |
| inventory-service | 5 | 0 | 0 | 0 |
| notification-service | 4 | 0 | 0 | 0 |
| order-service | 27 | 0 | 0 | 0 |
| payment-service | 5 | 0 | 0 | 0 |
| platform-common | 2 | 0 | 0 | 0 |
| supplier-service | 4 | 0 | 0 | 0 |
| user-service | 34 | 0 | 0 | 0 |

Total: 138 pruebas, sin fallos ni errores en los resultados finales.

Además: empaquetado de todo el reactor, validación de Compose (`docker compose config --quiet`) y revisión de longitud de métodos Java.

Límite de la validación: las pruebas de persistencia utilizan H2. Docker Desktop no pudo iniciar su motor Linux en este entorno, por lo que no se ejecutó el despliegue completo con PostgreSQL ni una prueba extremo a extremo por red. CORS se verificó sobre el contexto WebFlux sin abrir un puerto del sistema.
