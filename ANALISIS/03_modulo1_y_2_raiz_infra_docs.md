# Módulo 1 y 2 – Configuración Raíz, Infraestructura, Scripts y Documentación

## Análisis Detallado Archivo por Archivo

### 1. `.dockerignore`
- **Ruta:** `.dockerignore`
- **Responsabilidad:** Excluir archivos innecesarios del contexto de build de Docker para reducir el tamaño de transferencia y evitar fuga de información.
- **Líneas clave:** L1 (`**/target`), L2 (`**/node_modules`), L3 (`.git`), L4 (`.env`), L5 (`frontend`), L6 (`.agents`).
- **Dependencias:** Docker CLI y Docker Engine.
- **Problemas detectados:** Ninguno grave. Excluye correctamente `.env` para prevenir exposición de secretos en las capas de imagen.

### 2. `.env.example`
- **Ruta:** `.env.example`
- **Responsabilidad:** Servir como plantilla de variables de entorno críticas necesarias para ejecutar el sistema distribuido.
- **Líneas clave:** L1 (`JWT_SECRET`), L2 (`DB_PASSWORD`), L3 (`POSTGRES_PASSWORD`), L4 (`CONFIG_PASSWORD`).
- **Dependencias:** Consumido por Docker Compose y `setup-env.ps1`.
- **Problemas detectados:** Las instrucciones indican generar los valores con `scripts/setup-env.ps1`. No contiene credenciales reales comprometidas.

### 3. `.gitignore`
- **Ruta:** `.gitignore`
- **Responsabilidad:** Evitar que artefactos generados, temporales, secretos y librerías externas se versionen en Git.
- **Líneas clave:** L2-4 (`*.class`, `target/`, `**/target/`), L30 (`node_modules/`), L32 (`dist/`), L35 (`.angular/`), L44 (`.env`), L48 (`.vscode/`), L49 (`.idea/`).
- **Dependencias:** Git.
- **Problemas detectados:** La línea L48 ignora `.vscode/`, pero `.vscode/settings.json` está versionado en el repositorio (fue forzado con `git add` en algún momento).

### 4. `AGENTS.md`
- **Ruta:** `AGENTS.md`
- **Responsabilidad:** Establecer las reglas canónicas de desarrollo, arquitectura y convenciones de código para desarrolladores y agentes IA.
- **Líneas clave:** 
  - L4: Front-end en Angular última versión.
  - L11: Front-end usar functional components, nunca clases.
  - L13: Front-end seguir ATOMIC Design.
  - L14: Front-end utilizar Bootstrap.
  - L17: Las funciones en cualquier lenguaje no deben superar 20 líneas.
  - L18-22: Spring Boot con inyección de dependencias, convención `com.uelbosque.{service-name}`, KISS y SOLID.
- **Problemas detectados:** 
  - Contradicción directa con el código existente: `frontend/tienda-frontend` utiliza Angular 16 (EOL) y componentes basados en clases TypeScript (`@Component export class ...`), violando L4 y L11.

### 5. `README.md`
- **Ruta:** `README.md`
- **Responsabilidad:** Guía de arranque rápido y descripción general del repositorio.
- **Líneas clave:** L8-11 (instrucciones de inicio de Docker Compose), L13 (nota explícita: *"Las pantallas existentes todavía requieren integración con estas APIs"*), L15 (orden de pruebas Maven).
- **Problemas detectados:** Reconoce la falta de integración entre frontend y backend.

### 6. `REQUERIMIENTOS_MICROSERVICIOS.md`
- **Ruta:** `REQUERIMIENTOS_MICROSERVICIOS.md`
- **Responsabilidad:** Especificación de requerimientos funcionales del sistema basados en el caso de estudio de Tienda Genérica.
- **Líneas clave:** L7-12 (dominios obligatorios: usuarios, clientes, proveedores, catálogo CSV, ventas 1-3 renglones con IVA, reportes), L16 (bases de datos independientes por dominio), L24 (PostgreSQL 18.6, H2 en pruebas).
- **Problemas detectados:** L24 menciona PostgreSQL 18.6 (versión inexistente en Docker Hub).

### 7. `compose.yml`
- **Ruta:** `compose.yml`
- **Responsabilidad:** Manifiesto de orquestación de Docker Compose para desplegar los 13 servicios y el motor de base de datos.
- **Líneas clave:** L4 (`image: postgres:18.6`), L12-13 (volumen `postgres-data` y montaje de `init-databases.sh`), L19-27 (`config-server`), L28-51 (`user-service`), L252-273 (`gateway`).
- **Problemas detectados:**
  - `image: postgres:18.6` provocará fallo de descarga si la imagen no existe en Docker Hub. Se debe corregir a una versión estable soportada como `postgres:16-alpine` o `postgres:17`.
  - Todos los microservicios dependen de PostgreSQL mediante `depends_on: postgres: condition: service_healthy`, pero no declaran dependencia explícita hacia `config-server`, por lo que al arrancar en paralelo con perfil `distributed` podrían fallar al no encontrar Config Server listo aún si no se configura retry.

### 8. `skills-lock.json`
- **Ruta:** `skills-lock.json`
- **Responsabilidad:** Registrar el bloqueo de versiones y hashes criptográficos de las habilidades configuradas en `.agents`.
- **Líneas clave:** Hashes para `angular-developer`, `angular-testing`, `code-review`, `frontend-design`, `image-to-code`, `java-junit`, `java-microservices`, `java-springboot`.
- **Problemas detectados:** Ninguno; archivo de metadatos de configuración de agente.

### 9. `infra/init-databases.sh`
- **Ruta:** `infra/init-databases.sh`
- **Responsabilidad:** Script de inicialización que corre dentro del contenedor PostgreSQL para aprovisionar las 9 bases de datos y usuarios propietarios con permisos restringidos.
- **Líneas clave:** L3 (`for database in users customers suppliers catalog carts orders inventory payments notifications; do`), L5-8 (`CREATE ROLE :"db" LOGIN PASSWORD :'dbpass'; CREATE DATABASE :"db" OWNER :"db"; REVOKE CONNECT ON DATABASE :"db" FROM PUBLIC; GRANT CONNECT ON DATABASE :"db" TO :"db";`).
- **Problemas detectados:** Excelente práctica de aislamiento por principio de mínimo privilegio.

### 10. `scripts/check-java-functions.py`
- **Ruta:** `scripts/check-java-functions.py`
- **Responsabilidad:** Verificar automáticamente que ningún método Java en `services/*/src/main/java/**/*.java` exceda las 20 líneas de cuerpo.
- **Líneas clave:** L6 (regex para detección de signatura de método), L13-16 (conteo de llaves y saltos de línea), L20-21 (salida de violaciones y código de salida).
- **Problemas detectados:** Solo analiza archivos dentro de `src/main/java`, no analiza métodos de clases en `test/` o `src/test/java`, ni archivos de TypeScript en el frontend.

### 11. `scripts/setup-env.ps1`
- **Ruta:** `scripts/setup-env.ps1`
- **Responsabilidad:** Generar de forma segura un archivo `.env` local con claves aleatorias de 32 bytes en formato hexadecimal si no existe previamente.
- **Líneas clave:** L5-11 (función `New-Secret` usando `RandomNumberGenerator`), L12 (generación de `JWT_SECRET`, `DB_PASSWORD`, `POSTGRES_PASSWORD`, `CONFIG_PASSWORD`).
- **Problemas detectados:** Ninguno. Sigue buenas prácticas de criptografía segura en PowerShell.

### 12. `.vscode/settings.json`
- **Ruta:** `.vscode/settings.json`
- **Responsabilidad:** Configuración del editor VSCode para el workspace.
- **Líneas clave:** L6 (`.copilot/instructions`), L8-9 (análisis nulo de Java).
- **Problemas detectados:** Archivo de configuración local que idealmente debería estar ignorado según `.gitignore:48`, pero fue comiteado.

### 13. `docs/BACKEND.md`
- **Ruta:** `docs/BACKEND.md`
- **Responsabilidad:** Documentación técnica integral de la arquitectura de microservicios, contratos de API, comandos de compilación y guía de pruebas.
- **Líneas clave:** L34-48 (tabla de puertos y rutas de servicios), L51-105 (flujo reproducible paso a paso), L116-123 (detalles de seguridad JWT y expiración de 15 min).
- **Problemas detectados:** Documentación clara, precisa y alineada con la implementación real del backend.

### 14. `docs/REQUISITOS_BACKEND.md`
- **Ruta:** `docs/REQUISITOS_BACKEND.md`
- **Responsabilidad:** Matriz de trazabilidad entre las Historias de Usuario del documento académico PDF de Tienda Genérica y la implementación en microservicios.
- **Líneas clave:** L3-12 (mapeo de HU-001 a HU-023 con `user-service`, `customer-service`, `supplier-service`, `catalog-service`, `order-service` y `admin-service`).
- **Problemas detectados:** Ninguno; proporciona justificación formal de las decisiones de diseño.

### 15. `docs/VALIDACION_BACKEND.md`
- **Ruta:** `docs/VALIDACION_BACKEND.md`
- **Responsabilidad:** Registro de la validación técnica ejecutada sobre los microservicios (138 pruebas automatizadas sin fallos).
- **Líneas clave:** L5-21 (tabla de pruebas por servicio), L25 (declaración explícita de limitaciones: persistencia en H2 y ausencia de pruebas E2E con Docker en el entorno anterior).
- **Problemas detectados:** Registro de calidad transparente.

### 16. `docs/superpowers/plans/2026-09-06-microservicios.md`
- **Ruta:** `docs/superpowers/plans/2026-09-06-microservicios.md`
- **Responsabilidad:** Plan de trabajo y bitácora de diseño para la implementación de los microservicios.
- **Líneas clave:** L4-12 (fases del plan), L14 (decisiones sobre comunicación HTTP síncrona sin colas innecesarias para el alcance).
- **Problemas detectados:** Documento histórico que documenta el plan ejecutado.

### 17. `.copilot/instructions/angular.instructions.md`
- **Ruta:** `.copilot/instructions/angular.instructions.md`
- **Responsabilidad:** Instrucciones para asistentes de IA sobre convenciones de Angular.
- **Líneas clave:** L6-14 (Angular última versión, functional components, Atomic Design, Bootstrap, máximo 20 líneas).
- **Problemas detectados:** Reitera la directriz de "functional components" en Angular, la cual entra en conflicto con la naturaleza de clases de Angular 16.

### 18. `.copilot/instructions/java.instructions.md`
- **Ruta:** `.copilot/instructions/java.instructions.md`
- **Responsabilidad:** Instrucciones para desarrollo en Java/Spring Boot.
- **Líneas clave:** L6-12 (Spring Boot, inyección de dependencias, nomenclatura de paquetes `com.uelbosque`, SOLID, KISS, 20 líneas).
- **Problemas detectados:** Bien alineado con las prácticas del backend.

### 19. `.copilot/instructions/postgreSql.instructions.md`
- **Ruta:** `.copilot/instructions/postgreSql.instructions.md`
- **Responsabilidad:** Instrucciones para base de datos PostgreSQL.
- **Líneas clave:** L6-13 (última versión, transacciones, índices, vistas, stored procedures, triggers, restricciones).
- **Problemas detectados:** Menciona stored procedures y triggers que no se utilizan en este backend (la lógica reside en la capa Java Spring Boot mediante JPA/Hibernate).
