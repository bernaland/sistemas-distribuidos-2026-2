# -*- coding: utf-8 -*-
"""
Script to generate ANALISIS/14_fase5_ejecucion_verificacion.md (Fase 5: Ejecución y Verificación).
"""
import os

content = """# Auditoría Técnica - Fase 5: Ejecución y Verificación Empírica

**Auditor Senior:** Antigravity  
**Fecha:** 2026-10-18  
**Entorno de Ejecución:**  
- **SO:** Windows 11  
- **Java:** OpenJDK 21.0.12.1 (HotSpot)  
- **Node.js:** v24.19.0 / npm 11.2.0  
- **Build Tool Backend:** Apache Maven Wrapper (Spring Boot 3.1.3 / Spring Cloud 4.0.4)  
- **Build Tool Frontend:** Angular CLI 16.2.0  

---

## 1. Resumen de Pruebas y Compilaciones Reales

Todas las verificaciones reportadas a continuación fueron ejecutadas en vivo en el entorno de desarrollo real, sin suposiciones ni emulaciones sintéticas.

| Componente / Microservicio | Comando Ejecutado | Resultado | Tests Exitosos / Fallidos | Tiempo |
|---|---|---|---|---|
| **`platform-common`** | `./services/mvnw.cmd -f services/pom.xml -pl platform-common test` | **BUILD SUCCESS** | 2 exitosos / 0 fallidos | 9.79 s |
| **`customer-service`** | `./services/mvnw.cmd -f services/pom.xml -pl customer-service -am test` | **BUILD SUCCESS** | 4 exitosos / 0 fallidos | 32.96 s |
| **`catalog-service`** | `./services/mvnw.cmd -f services/pom.xml -pl catalog-service -am test` | **BUILD SUCCESS** | 21 exitosos / 0 fallidos | 35.10 s |
| **`order-service`** | `./services/mvnw.cmd -f services/pom.xml -pl order-service -am test` | **BUILD SUCCESS** | 27 exitosos / 0 fallidos | 33.55 s |
| **`user-service`** | `./services/mvnw.cmd -f services/pom.xml -pl user-service -am test-compile` | **BUILD FAILURE** | 0 tests (Fallo de compilación) | 4.75 s |
| **`tienda-frontend` (Build)** | `npm.cmd run build` | **BUILD SUCCESS** | Bundle generado (651.76 kB) | 10.81 s |
| **`tienda-frontend` (Tests)** | `npm.cmd test -- --watch=false` | **INHIBIDO** | Falta ejecutable Chrome en PATH | - |

---

## 2. Detalle de Ejecución por Módulo

### 2.1 `platform-common`
- **Comando:** `services\mvnw.cmd -f services/pom.xml -pl platform-common test`
- **Resultado:** `BUILD SUCCESS` en 9.793 s.
- **Evidencia del Log:**
  ```text
  [INFO] Running com.uelbosque.platformcommon.ApiSecurityTest
  [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.312 s
  [INFO] BUILD SUCCESS
  ```
- **Conclusión:** La biblioteca común de seguridad JWT y configuración web filtra peticiones no autenticadas correctamente y permite acceso anónimo a Actuator.

### 2.2 `customer-service`
- **Comando:** `services\mvnw.cmd -f services/pom.xml -pl customer-service -am test`
- **Resultado:** `BUILD SUCCESS` en 32.960 s.
- **Evidencia del Log:**
  ```text
  [INFO] Running com.uelbosque.customerservice.CustomerApiTest
  [INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 17.532 s
  [INFO] Reactor Summary for platform-common 0.0.1-SNAPSHOT:
  [INFO] platform-common ......... SUCCESS [ 8.758 s]
  [INFO] customer-service ........ SUCCESS [23.481 s]
  ```
- **Conclusión:** CRUD completo de clientes validado sobre base de datos H2 en memoria. Rechazo de cédulas duplicadas verificado.

### 2.3 `catalog-service`
- **Comando:** `services\mvnw.cmd -f services/pom.xml -pl catalog-service -am test`
- **Resultado:** `BUILD SUCCESS` en 35.100 s.
- **Evidencia del Log:**
  ```text
  [INFO] Test Source directory: services/catalog-service/test added.
  [INFO] Running com.uelbosque.catalogservice.controller.ProductControllerTest
  [INFO] Running com.uelbosque.catalogservice.CsvConcurrencyTest
  [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 11.108 s
  [INFO] Running com.uelbosque.catalogservice.CsvImportTest
  [INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.586 s
  [INFO] Running com.uelbosque.catalogservice.service.ProductServiceTest
  [INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.315 s
  [INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
  ```
- **Conclusión:** Se verifica empíricamente el comportamiento del cerrojo pesimista `CatalogMutex` en `CsvConcurrencyTest`. Los 21 tests pasan satisfactoriamente.

### 2.4 `order-service`
- **Comando:** `services\mvnw.cmd -f services/pom.xml -pl order-service -am test`
- **Resultado:** `BUILD SUCCESS` en 33.549 s.
- **Evidencia del Log:**
  ```text
  [INFO] Test Source directory: services/order-service/test added.
  [INFO] Running com.uelbosque.orderservice.controller.OrderControllerTest
  [INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.uelbosque.orderservice.repository.OrderRepositoryTest
  [INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.uelbosque.orderservice.SalesApiTest
  [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.uelbosque.orderservice.SaleServiceTest
  [INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.uelbosque.orderservice.SalesPersistenceTest
  [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running com.uelbosque.orderservice.service.OrderServiceTest
  [INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
  ```
- **Conclusión:** Valida tanto el flujo e-commerce (`Order`) como el flujo de ventas transaccionales (`Sale`) con persistencia e idempotencia (`SalesPersistenceTest`).

### 2.5 `user-service` (Error Fatal Verificado)
- **Comando:** `services\mvnw.cmd -f services/pom.xml -pl user-service -am test-compile`
- **Resultado:** `BUILD FAILURE` en 4.747 s.
- **Log de Error Exacto:**
  ```text
  [ERROR] COMPILATION ERROR : 
  [ERROR] /services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java:[24,26] 
          no suitable constructor found for User(java.lang.String,java.lang.String,java.lang.String)
      constructor com.uelbosque.userservice.model.User.User() is not applicable
        (actual and formal argument lists differ in length)
      constructor com.uelbosque.userservice.model.User.User(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String) is not applicable
        (actual and formal argument lists differ in length)
  [INFO] 1 error
  [INFO] BUILD FAILURE
  ```
- **Conclusión:** Confirma de forma irrefutable que el repositorio tiene rota la compilación de `user-service`. La solución requerida es eliminar el archivo obsoleto `services/user-service/src/main/java/com/uelbosque/userservice/DataInitializer.java` y mantener únicamente la versión válida ubicada en `.../config/DataInitializer.java`.

### 2.6 `frontend/tienda-frontend` (Compilación de Producción)
- **Comando:** `npm.cmd run build` en `frontend/tienda-frontend`
- **Resultado:** `BUILD SUCCESS` en 10.806 s.
- **Artefactos Producidos:**
  - `main.79f26ef1fecd8280.js`: 312.07 kB (Transfer: 76.87 kB)
  - `styles.5f559e9074ee53e4.css`: 305.77 kB (Transfer: 33.17 kB)
  - `polyfills.3beeb475c8cf0a64.js`: 33.04 kB (Transfer: 10.63 kB)
  - `runtime.02cef59d5cbbda0a.js`: 908 bytes (Transfer: 519 bytes)
  - Tamaño inicial total: 651.76 kB (Transfer: 121.18 kB).
- **Advertencias detectadas durante el build:**
  `1 rules skipped due to selector errors: .form-floating>~label -> Did not expect successive traversals.` (Error de sintaxis en regla CSS de Bootstrap/styles).

---

## 3. Resumen Consolidado de Calidad de la Suite de Pruebas

- **Total de Pruebas Unitarias/Integración Ejecutadas Exitosamente en Backend:** 54 pruebas (2 en `platform-common`, 4 en `customer-service`, 21 en `catalog-service`, 27 en `order-service`).
- **Tasa de Aprobación en Módulos Funcionales del Backend:** 100% (54/54).
- **Servicios Bloqueados:** 1 (`user-service` bloqueado por error de sintaxis/constructores).
- **Pruebas en Frontend:** 1 spec declarada (`app.component.spec.ts`), con ejecución headless inhibida por ausencia de Chrome en PATH.
"""

with open('ANALISIS/14_fase5_ejecucion_verificacion.md', 'w', encoding='utf-8') as f:
    f.write(content.strip() + '\n')

print("Created ANALISIS/14_fase5_ejecucion_verificacion.md successfully.")
