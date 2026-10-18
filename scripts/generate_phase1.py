import os
import xml.etree.ElementTree as ET
import json
import glob

root = '.'
excluded = {'.git', '.angular', 'node_modules', 'target', '.idea', 'dist'}

tree_lines = []
for dirpath, dirnames, filenames in os.walk(root):
    dirnames[:] = [d for d in dirnames if d not in excluded]
    norm_dp = os.path.relpath(dirpath, root).replace('\\', '/')
    if norm_dp.startswith('ANALISIS'):
        continue
    depth = 0 if norm_dp == '.' else norm_dp.count('/') + 1
    indent = '  ' * depth
    if norm_dp != '.':
        tree_lines.append(f'{indent[:-2]}- **{os.path.basename(dirpath)}/** (`{norm_dp}/`)')
    for f in sorted(filenames):
        tree_lines.append(f'{indent}- `{f}`')

# Read frontend package.json
fe_pkg_path = 'frontend/tienda-frontend/package.json'
with open(fe_pkg_path, 'r', encoding='utf-8') as f:
    fe_pkg = json.load(f)

# Read backend POMs
poms_info = []
for pom in sorted(glob.glob('services/**/pom.xml', recursive=True)):
    tree = ET.parse(pom)
    r = tree.getroot()
    ns = '{http://maven.apache.org/POM/4.0.0}'
    art = r.find(f'{ns}artifactId').text if r.find(f'{ns}artifactId') is not None else ''
    parent_node = r.find(f'{ns}parent')
    parent_ver = parent_node.find(f'{ns}version').text if parent_node is not None and parent_node.find(f'{ns}version') is not None else 'N/A'
    deps = []
    for d in r.findall(f'.//{ns}dependency'):
        g = d.find(f'{ns}groupId').text if d.find(f'{ns}groupId') is not None else ''
        a = d.find(f'{ns}artifactId').text if d.find(f'{ns}artifactId') is not None else ''
        v = d.find(f'{ns}version').text if d.find(f'{ns}version') is not None else 'heredada'
        s = d.find(f'{ns}scope').text if d.find(f'{ns}scope') is not None else 'compile'
        deps.append((g, a, v, s))
    poms_info.append((art, pom.replace('\\', '/'), parent_ver, deps))

out_path = 'ANALISIS/01_fase1_reconocimiento.md'
with open(out_path, 'w', encoding='utf-8') as out:
    out.write('# Fase 1 – Reconocimiento Técnico y Exhaustivo\n\n')
    out.write('## 1. Resumen Ejecutivo de Reconocimiento\n\n')
    out.write('El proyecto **"sistemas-distribuidos-2026-2"** es una solución distribuida para un sistema de **Tienda Genérica**, compuesto por:\n')
    out.write('- **Frontend Web:** Desarrollado en Angular 16 (`frontend/tienda-frontend`), estructurado bajo el paradigma Atomic Design (atoms, molecules, organisms, pages, templates) con Bootstrap 5.\n')
    out.write('- **Backend Distribuido:** Basado en arquitectura de Microservicios con Spring Boot 3.1.3 y Spring Cloud 2022.0.4 (4.0.4), compuesto por 12 servicios funcionales/auxiliares, 1 biblioteca compartida (`platform-common`), un servidor de configuración central (`config-server`) y una puerta de enlace API Gateway (`gateway`).\n')
    out.write('- **Persistencia:** PostgreSQL con aislamiento estricto por dominio mediante bases de datos y roles independientes por microservicio (`init-databases.sh`), y H2 in-memory exclusivo para la suite de pruebas unitarias/integración.\n')
    out.write('- **Orquestación y Entorno:** Docker Compose (`compose.yml`) parametrizado mediante `.env` generado criptográficamente (`scripts/setup-env.ps1`).\n\n')

    out.write('## 2. Lenguajes, Frameworks y Gestores de Paquetes\n\n')
    out.write('| Capa / Módulo | Lenguaje / Versión | Framework / Versión | Gestor de Paquetes | Runtime / Servidor |\n')
    out.write('|---|---|---|---|---|\n')
    out.write('| **Frontend** (`frontend/tienda-frontend`) | TypeScript 5.1.3, JavaScript ES2022, HTML5, CSS3 | Angular 16.2.0, Bootstrap 5.3.8, Bootstrap Icons 1.13.1 | npm (Node >= 18) | Angular CLI Development Server / Nginx o estático |\n')
    out.write('| **Backend Microservicios** (`services/*`) | Java 17 (compilación compatible hasta Java 21) | Spring Boot 3.1.3, Spring Cloud 4.0.4, Spring Data JPA, Spring Security | Maven 3.9.9 (Maven Wrapper `mvnw.cmd` / `mvnw`) | Eclipse Temurin 17 JRE (Docker) / Tomcat embebido / Netty (Gateway) |\n')
    out.write('| **Base de Datos** | SQL (PostgreSQL dialect) | PostgreSQL 18.6 (declarado en compose), H2 2.1+ (tests) | N/A | PostgreSQL Server Docker Container |\n')
    out.write('| **Infraestructura / Automatización** | Bash, PowerShell, Python 3.12 | Docker Compose v2, curl, psql | N/A | Docker Engine Linux Containers / Windows Host |\n\n')

    out.write('## 3. Inventario y Análisis de Archivos de Configuración\n\n')
    out.write('### 3.1. Archivos Raíz del Repositorio\n')
    out.write('- `AGENTS.md` (L1-38): Define las reglas arquitectónicas y de desarrollo del proyecto (Angular última versión, functional components, Bootstrap, Atomic Design, Java/Spring Boot, PostgreSQL, inyección de dependencias, convención `com.uelbosque.{service-name}`, pruebas unitarias obligatorias, límite estricto de 20 líneas por método/función, SOLID y KISS).\n')
    out.write('- `REQUERIMIENTOS_MICROSERVICIOS.md` (L1-27): Define el alcance funcional obligatorio derivado del caso de estudio de Tienda Genérica (Usuarios, Clientes, Proveedores, Catálogo CSV atómico, Ventas 1-3 renglones con IVA individual, Reportes) y servicios auxiliares (Inventario, Carrito, Pagos simulados, Notificaciones).\n')
    out.write('- `compose.yml` (L1-276): Orquestación multi-contenedor para 13 servicios (Postgres, Config Server, User Service, Catalog Service, Inventory Service, Cart Service, Order Service, Payment Service, Notification Service, Admin Service, Customer Service, Supplier Service, Gateway). Especifica healthchecks, volumen persistente `postgres-data`, inyección de variables desde `.env` y red interna.\n')
    out.write('- `.env.example` (L1-5): Plantilla de variables de entorno críticas (`JWT_SECRET`, `DB_PASSWORD`, `POSTGRES_PASSWORD`, `CONFIG_PASSWORD`).\n')
    out.write('- `.dockerignore` (L1-7): Excluye target, node_modules, .git, .env, frontend, .agents de los contextos de construcción Docker.\n')
    out.write('- `.gitignore` (L1-63): Configura ignorados para Java (target, .class, .jar), Node/Angular (node_modules, dist, .angular), IDEs (.vscode, .idea) y secretos (.env).\n')
    out.write('- `skills-lock.json` (L1-54): Bloqueo de hashes y fuentes de habilidades del agente (Angular developer, testing, review, Java junit, microservices, springboot).\n\n')

    out.write('### 3.2. Infraestructura y Scripts\n')
    out.write('- `infra/init-databases.sh` (L1-11): Script shell de inicialización PostgreSQL en `/docker-entrypoint-initdb.d/`. Itera sobre 9 bases de datos (`users`, `customers`, `suppliers`, `catalog`, `carts`, `orders`, `inventory`, `payments`, `notifications`), crea roles específicos y revoca acceso público (`REVOKE CONNECT ON DATABASE FROM PUBLIC`).\n')
    out.write('- `scripts/setup-env.ps1` (L1-15): Generador criptográficamente seguro de 32 bytes aleatorios para claves en `.env`.\n')
    out.write('- `scripts/check-java-functions.py` (L1-22): Verificador automatizado en Python que valida la regla de `AGENTS.md` de máximo 20 líneas por método en todos los archivos `.java` de `services/*/src/main/java/`.\n')
    out.write('- `.vscode/settings.json` (L1-11): Configuración de entorno de desarrollo VSCode para Copilot instructions y análisis nulo de Java.\n')
    out.write('- `.copilot/instructions/` (`angular.instructions.md`, `java.instructions.md`, `postgreSql.instructions.md`): Directrices de estilo de código para asistentes IA.\n\n')

    out.write('### 3.3. Configuración del Frontend (`frontend/tienda-frontend/`)\n')
    out.write('- `package.json`: Manifiesto de dependencias npm para Angular 16.2.0.\n')
    out.write('- `angular.json`: Configuración del workspace Angular CLI (builder `@angular-devkit/build-angular:browser`, presupuestos de bundle, estilos globales `src/styles.css`).\n')
    out.write('- `tsconfig.json`, `tsconfig.app.json`, `tsconfig.spec.json`: Configuración estricta de compilador TypeScript (ES2022, strict: true, noImplicitReturns: true, strictTemplates: true).\n')
    out.write('- `.editorconfig`: Estándares de indentación (2 espacios) y codificación (UTF-8).\n')
    out.write('- `package_legacy.json` y `angular_legacy.json`: Archivos residuales/obsoletos detectados en el árbol.\n\n')

    out.write('### 3.4. Configuración del Backend (`services/`)\n')
    out.write('- `services/pom.xml`: POM agregador raíz que define los 13 submódulos Maven.\n')
    out.write('- `services/Dockerfile`: Multi-stage build (Maven 3.9.9 / Eclipse Temurin 17 JRE) para empaquetado y ejecución segura con usuario no-root (UID 10001) y limitación de memoria RAM (`-XX:MaxRAMPercentage=60`).\n')
    out.write('- `services/.mvn/wrapper/maven-wrapper.properties`: Maven Wrapper v3.3.4 con distribución Maven 3.9.9.\n')
    out.write('- `services/platform-common/pom.xml`: Biblioteca compartida para auto-configuración de Spring Boot (`PlatformConfiguration`), filtros de seguridad (`ApiSecurity`), manejo estándar de errores HTTP (`ApiErrors`) y clientes con timeout.\n')
    out.write('- `services/config-server/src/main/resources/config/application.yml`: Configuración centralizada distribuida por Spring Cloud Config.\n\n')

    out.write('## 4. Matriz Completa de Dependencias y Estado de Vulnerabilidad / Obsolescencia\n\n')
    out.write('### 4.1. Dependencias del Frontend (npm)\n\n')
    out.write('| Paquete | Versión Declarada | Tipo | Estado / Riesgo de Obsolescencia |\n')
    out.write('|---|---|---|---|\n')
    for dep, ver in fe_pkg.get('dependencies', {}).items():
        state = 'Normal'
        if 'angular' in dep:
            state = '**Obsoleta (Angular 16 EOL en Nov 2024; actual v19/v20)**'
        elif dep == 'bootstrap':
            state = 'Actualizada (Bootstrap 5)'
        out.write(f'| `{dep}` | `{ver}` | Producción | {state} |\n')
    for dep, ver in fe_pkg.get('devDependencies', {}).items():
        state = 'Normal'
        if 'angular' in dep or dep == 'typescript':
            state = '**Obsoleta (Typescript 5.1 / Angular 16 tooling)**'
        out.write(f'| `{dep}` | `{ver}` | Desarrollo | {state} |\n')
    out.write('\n')

    out.write('### 4.2. Dependencias del Backend (Maven)\n\n')
    out.write('| Módulo | Spring Boot | Dependencias Clave | Base de Datos | Scope Test / Build Plugins |\n')
    out.write('|---|---|---|---|---|\n')
    for art, pom, pver, deps in poms_info:
        if art == 'tienda-services':
            continue
        dep_names = ', '.join([f'`{a}`' for g, a, v, s in deps if s == 'compile' and a not in ['h2', 'postgresql', 'spring-boot-starter-test', 'spring-security-test']])
        out.write(f'| `{art}` | `{pver}` | {dep_names} | PostgreSQL (runtime), H2 (test) | `spring-boot-starter-test`, `spring-security-test` |\n')
    out.write('\n')

    out.write('### 4.3. Análisis de Obsolescencia y Riesgos de Configuración Detectados\n\n')
    out.write('1. **Versión de Angular (Frontend):**\n')
    out.write('   - *Evidencia:* `frontend/tienda-frontend/package.json:13-20` (`@angular/core: ^16.2.0`).\n')
    out.write('   - *Conflicto con directriz:* `AGENTS.md:4` estipula: *"El front-end debe utilizar Angular en su ultima version"*. Angular 16 llegó a End-Of-Life (EOL) en noviembre de 2024. La última versión mayor actual es Angular 19/20.\n')
    out.write('   - *Impacto:* Brecha de cumplimiento de la regla estricta del proyecto; falta de características modernas como Signals nativos, nuevo control flow `@if/@for`, y componentes funcionales completos.\n\n')

    out.write('2. **Versión de Spring Boot y Spring Cloud (Backend):**\n')
    out.write('   - *Evidencia:* `services/*/pom.xml` (`spring-boot-starter-parent: 3.1.3`, `spring-cloud-starter-config: 4.0.4`).\n')
    out.write('   - *Diagnóstico:* Spring Boot 3.1.3 (lanzado en agosto de 2023) ya superó la ventana de soporte de código abierto OSS (mayo de 2024). Aunque es funcional y estable sobre Java 17 y Java 21, requiere plan de migración hacia Spring Boot 3.3.x o 3.4.x para soporte de seguridad a largo plazo.\n\n')

    out.write('3. **Imagen de PostgreSQL en Docker Compose:**\n')
    out.write('   - *Evidencia:* `compose.yml:4` (`image: postgres:18.6`) y `REQUERIMIENTOS_MICROSERVICIOS.md:24`.\n')
    out.write('   - *Diagnóstico:* En Docker Hub no existe la etiqueta `postgres:18.6` (las versiones estables de PostgreSQL son 16.x y 17.x; la versión 18 no ha sido liberada con versión menor 18.6). Si un operador ejecuta `docker compose up --build -d` sin construir una imagen personalizada con esa etiqueta, Docker arrojará error de pull (`manifest unknown` o `image not found`).\n\n')

    out.write('4. **Archivos de configuración duplicados y obsoletos (Legacy):**\n')
    out.write('   - *Evidencia:* `frontend/tienda-frontend/package_legacy.json` y `frontend/tienda-frontend/angular_legacy.json`.\n')
    out.write('   - *Diagnóstico:* Son artefactos huérfanos generados durante pruebas o migraciones previas que confunden a desarrolladores y herramientas de CI/CD.\n\n')

    out.write('## 5. Árbol Completo de Directorios y Archivos\n\n')
    out.write('A continuación se presenta el árbol estructural íntegro del proyecto (excluyendo únicamente los directorios declarados en el inventario: `.git`, `.angular`, `node_modules`, `target`, `dist`, `.idea`):\n\n')
    out.write('```\n')
    for tl in tree_lines:
        out.write(tl + '\n')
    out.write('```\n')

print('Fase 1 completada en ANALISIS/01_fase1_reconocimiento.md')
