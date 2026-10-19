# Tienda Genérica — Sistemas Distribuidos 2026-2

Plataforma empresarial distribuida compuesta por microservicios en **Spring Boot (Java 17)**, base de datos por dominio en **PostgreSQL 18.6** y frontend SPA reactivo en **Angular 22** con TypeScript, Signals, Bootstrap y arquitectura Atomic Design.

---

## 🚀 Arquitectura y Componentes

- **Backend (14 módulos Maven en reactor)**:
  - `gateway`: API Gateway reactivo (Spring Cloud Gateway WebFlux) en el puerto `8080`.
  - `config-server`: Servidor de configuración centralizada en el puerto `8888`.
  - `user-service`: Gestión de usuarios, autenticación JWT con BCrypt, administración de contraseñas.
  - `customer-service`: Directorio y CRUD de clientes con cédula única.
  - `supplier-service`: Gestión y CRUD de proveedores con NIT comercial.
  - `catalog-service`: Catálogo de productos, precios, cálculo de márgenes e importación masiva CSV transaccional.
  - `order-service`: Ventas comerciales con IVA individual, hasta 3 renglones, consecutivos automáticos e Idempotency-Key.
  - `admin-service`: Módulo consolidado de reportes (usuarios, clientes y ventas por cliente con totales).
  - `inventory-service`, `cart-service`, `payment-service`, `notification-service`: Servicios auxiliares de apoyo.
  - `platform-common`: Biblioteca común para seguridad JWT, DTOs y utilidades de transporte.
- **Frontend SPA (`frontend/tienda-frontend`)**:
  - Angular 22 standalone components con Signals, Reactive Control Flow (`@if`, `@for`), functional guards (`authGuard`, `roleGuard`) e interceptor funcional JWT con manejo de errores 400/401/409/503.
  - Arquitectura Atomic Design completa: atoms, molecules, 8 organisms, templates y pages.
- **Base de Datos**: PostgreSQL 18.6 multi-base (un esquema y usuario dedicado por microservicio de negocio).

---

## ⚙️ Inicio Rápido con Docker Compose

```powershell
# 1. Generar variables de entorno seguras (.env)
./scripts/setup-env.ps1

# 2. Levantar la plataforma completa (microservicios + bases PostgreSQL + frontend)
docker compose up --build -d
```

- **Frontend SPA**: [http://localhost:4200](http://localhost:4200)
- **API Gateway**: [http://localhost:8080](http://localhost:8080)
- **Config Server**: [http://localhost:8888](http://localhost:8888) (Usuario: `config`, Contraseña: en `.env`)

---

## 🧪 Pruebas Automatizadas y Calidad

### Backend (137 tests unitarios y de integración)
Todos los tests residen en `src/test/java` estándar, ejecutables mediante el reactor Maven:
```powershell
./services/mvnw.cmd -B -f services/pom.xml clean test
```
*Resultado*: **137/137 tests exitosos** (0 fallos, 0 errores, 14 módulos SUCCESS).

### Frontend (65 unit tests + Cobertura ≥ 70%)
```powershell
cd frontend/tienda-frontend
npm run test:coverage
```
*Resultado*: **65/65 unit tests exitosos** con **89.98% de cobertura en líneas** (89.55% statements, 80.68% functions, 74.82% branches).

### E2E con Playwright (5 flujos críticos contra el contrato de negocio)
```powershell
cd frontend/tienda-frontend
npm run test:e2e
```
*Escenarios probados*:
1. Autenticación y navegación al Dashboard.
2. CRUD de Cliente (Creación, Consulta, Actualización y Borrado).
3. Carga masiva de productos vía archivo CSV de 6 columnas.
4. Registro de venta con cálculo dinámico de IVA y 3 renglones.
5. Reporte consolidado de ventas por cliente con total acumulado.

---

## 📚 Documentación Técnica Detallada

- [Guía de APIs y Contratos del Backend](docs/BACKEND.md)
- [Trazabilidad de Historias de Usuario (HU-001 a HU-023)](docs/REQUISITOS_BACKEND.md)
- [Informe de Validación y Pruebas](docs/VALIDACION_BACKEND.md)
- [Informe de Cierre de Proyecto](ANALISIS/INFORME_CIERRE.md)
