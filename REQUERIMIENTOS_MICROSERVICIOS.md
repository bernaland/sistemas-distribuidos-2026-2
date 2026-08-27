# Especificación de Requerimientos — Arquitectura de Microservicios

> Basado en el documento "Proyecto Desarrollo Software - Tienda Genérica Virtual.pdf" (contenido considerado hasta la página 25). Se propone una arquitectura de microservicios donde cada módulo es un microservicio implementado con Spring Boot y el front-end con Angular.

## 1. Introducción

Este documento describe los requerimientos funcionales y no funcionales para la implementación de la Tienda Genérica Virtual utilizando una arquitectura de microservicios. El objetivo es definir los módulos, responsabilidades, APIs y requisitos de calidad necesarios para el desarrollo, despliegue y operación del sistema.

Nota: Solo se consideró el contenido del PDF hasta la página 25; cualquier información posterior fue ignorada según la instrucción del cliente.

## 2. Alcance

- Crear una plataforma de comercio electrónico básica que permita a los usuarios navegar catálogos, gestionar un carrito, realizar pedidos y pagos.
- Cada módulo o dominio se implementará como un microservicio independiente con Spring Boot.
- El front-end será una aplicación SPA desarrollada con Angular que consuma las APIs expuestas por los microservicios.
- Infraestructura mínima: API Gateway, Service Discovery, Config Server, base de datos por servicio (según necesidad), mensajería para comunicación asíncrona.

## 3. Requerimientos funcionales (por microservicio)

Se definen los microservicios principales y sus responsabilidades:

1. Servicio de Gestión de Usuarios (user-service)
   - Registro de usuarios (nombre, correo, contraseña, rol).
   - Autenticación y autorización (login, refresco de tokens).
   - Recuperación y actualización de perfil.
   - Gestión de roles y permisos (usuario, administrador).
   - API REST y soporte para JWT (token-based auth).

2. Servicio de Catálogo (catalog-service)
   - CRUD de productos (nombre, descripción, precio, categoría, imágenes, atributos).
   - Búsqueda y filtrado por categoría, precio y atributos.
   - Gestión de categorías y relaciones.
   - Exponer APIs para consulta pública (GET) y gestión (POST/PUT/DELETE) por roles autorizados.

3. Servicio de Inventario (inventory-service)
   - Gestión de stock por SKU/producto.
   - Reserva de stock al crear órdenes (para evitar overselling).
   - Ajustes de inventario (entrada/salida, reposiciones).
   - Eventos de inventario publicados cuando cambian niveles (mensajería).

4. Servicio de Carrito (cart-service)
   - Gestión del carrito de compras por usuario (agregar, quitar, actualizar cantidades).
   - Persistencia temporal del carrito (puede almacenarse en DB o cache Redis).
   - Cálculo de totales, impuestos y descuentos aplicables.

5. Servicio de Pedido (order-service)
   - Creación de órdenes a partir del carrito.
   - Integración con inventory-service para confirmación de stock.
   - Gestión de estados de pedido (pendiente, confirmado, enviado, entregado, cancelado).
   - Historial de pedidos por usuario.

6. Servicio de Pago (payment-service)
   - Integración con pasarelas de pago externas (simulación o integración real según alcance).
   - Validación y procesamiento de pagos.
   - Registro y actualización del estado del pago (autorizado, capturado, fallido).
   - Eventos sobre pagos finalizados.

7. Servicio de Notificaciones (notification-service)
   - Envío de correos electrónicos y/o notificaciones (confirmación de pedido, cambios de estado).
   - Plantillas de mensajes y colas para envío asíncrono.

8. Servicio de Administración (admin-service)
   - Operaciones administrativas: dashboards, gestión de productos, gestión de usuarios y pedidos.
   - APIs protegidas para usuarios con rol administrador.

9. API Gateway (gateway)
   - Punto de entrada único para el front-end.
   - Enrutamiento a microservicios, autenticación centralizada, rate limiting y agregación básica.

10. Service Discovery (e.g., Eureka) y Config Server
   - Registro y descubrimiento dinámico de microservicios.
   - Configuración centralizada para entornos (dev, test, prod).

11. Observabilidad
   - Servicio (o stack) de logging centralizado (ELK/EFK o similar).
   - Métricas y monitoreo (Prometheus + Grafana o similar).
   - Tracing distribuido (Zipkin/Jaeger) para seguimiento de solicitudes.

## 4. Requerimientos no funcionales

- Escalabilidad: cada microservicio debe poder escalar horizontalmente.
- Disponibilidad: diseñar para tolerancia a fallos parcial; degradación controlada.
- Rendimiento: latencia de APIs críticas < 300ms en condiciones normales (SLA interno que puede ajustarse).
- Seguridad:
  - Autenticación: JWT y roles.
  - Comunicación segura: TLS entre componentes en producción.
  - Gestión de secretos: uso de store seguro (Vault, Secrets Manager o similar).
- Consistencia: modelo eventual para algunos procesos (p. ej., inventario notificado por eventos), consistencia fuerte donde sea crítico (p. ej., transacciones de pago).
- Observabilidad: logs estructurados, métricas y trazas distribuidas.
- Respaldo y recuperación: procedimientos para backup de bases de datos críticas y restauración.

## 5. Integración y comunicación entre servicios

- Comunicación síncrona: REST/HTTP para consultas y comandos que requieran respuesta inmediata.
- Comunicación asíncrona: mensajería (RabbitMQ o Kafka) para eventos (pedido creado, pago confirmado, stock reservado).
- Contratos API: OpenAPI/Swagger para cada microservicio; versiones semánticas para cambios de contrato.

## 6. Persistencia

- Patrón de persistencia: base de datos por servicio (independencia de esquema). Recomendación:
  - user-service: PostgreSQL
  - catalog-service: PostgreSQL
  - inventory-service: PostgreSQL
  - order-service: PostgreSQL
  - cart-service: Redis (opcional) o PostgreSQL
  - payment-service: PostgreSQL (registro de transacciones)
- Migrations: usar Flyway o Liquibase para esquemas gestionados por proyecto.

## 7. Front-end

- SPA con Angular (última versión estable compatible con el equipo).
- Estructura por módulos: Auth, Catálogo, Carrito, Checkout, Perfil, Admin.
- Comunicación con backend a través de API Gateway y endpoints REST.
- Manejo de estado: NgRx (opcional) para carrito y estado de sesión.
- Internacionalización: soporte básico para español (es-CO) y posibilidad de agregar otros idiomas.

## 8. Seguridad y autenticación

- Autenticación centralizada con JWT (tokens de acceso y refresh tokens).
- Roles y permisos integrados en user-service y validados en gateway y servicios.
- Validaciones input en backend y protección contra ataques comunes (CSRF, XSS en front-end, inyección SQL con uso de ORM/queries parametrizadas).
- Cifrado de datos sensibles en reposo y en tránsito.

## 9. DevOps y despliegue

- Contenerización: Docker para cada microservicio y front-end.
- Orquestación: Kubernetes para despliegue en clusters (opcional según alcance, puede iniciarse con Docker Compose para desarrollo).
- CI/CD: pipelines para build, test, security scans y despliegue automático a entornos (GitHub Actions, GitLab CI o similar).
- Entornos: dev, staging, prod con configuraciones separadas en Config Server.

## 10. Testing

- Pruebas unitarias: JUnit y Mockito para servicios Spring Boot.
- Pruebas de integración: Spring Boot Test y pruebas contractuales (Pact o Spring Contract).
- Pruebas end-to-end: Cypress o Protractor para la aplicación Angular (preferir Cypress).
- Tests de carga básicos para validar escalabilidad (JMeter o k6).

## 11. Criterios de aceptación

- Las APIs documentadas con OpenAPI y con pruebas de contrato mínimas que aseguren compatibilidad con front-end.
- Flujo de compra completo (registro/login → navegación → añadir a carrito → checkout → pago simulado → confirmación) funcionando en entorno de staging.
- Logs centralizados y métricas básicas disponibles en tablero (por ejemplo, errores 5xx, latencia promedio).
- Autenticación y autorización funcionando con roles (usuario/administrador).

## 12. Entregables mínimos

- Repositorio con microservicios de ejemplo (esqueleto) en Spring Boot para servicios clave: user, catalog, order, payment, inventory, cart, notification, admin.
- Proyecto Angular con módulos básicos y conexión a API Gateway.
- Documentación: README por servicio, OpenAPI specs y guía de despliegue (local y en Kubernetes si aplica).
- Pipeline CI/CD básico configurado.

## 13. Roadmap sugerido (fases)

1. Fase 0 — Preparación
   - Definición de APIs y contratos (OpenAPI).
   - Estructura de repositorios (monorepo vs repos separados).
2. Fase 1 — MVP
   - user-service, catalog-service, cart-service, order-service, gateway y front-end con flujo básico.
   - Base de datos y contenedores para local dev.
3. Fase 2 — Integraciones
   - payment-service (simulación/pasarela), inventory-service, notification-service.
   - Config Server y Service Discovery.
4. Fase 3 — Operación y calidad
   - Observabilidad, CI/CD, pruebas de carga, seguridad y despliegue a staging/prod.

## 14. Suposiciones y limitaciones

- Información tomada hasta la página 25 del PDF. Requisitos o detalles en páginas posteriores no fueron considerados.
- Algunas decisiones técnicas (p. ej., elección exacta de broker o base de datos) pueden ajustarse según restricciones del entorno y preferencias del equipo.

## 15. Anexos

- Fuente: "Proyecto Desarrollo Software - Tienda Genérica Virtual.pdf" (C:/repos/sistemas-distribuidos-2026-2). Contenido analizado hasta la página 25.
