# sistemas-distribuidos-2026-2
Repo para la asignatura Sistemas Distribuidos

## Backend de microservicios

Los servicios cubren usuarios, clientes, proveedores, catálogo CSV, ventas, reportes, inventario, pagos simulados y notificaciones. Incluye gateway, configuración central y PostgreSQL por dominio.

```powershell
./scripts/setup-env.ps1
docker compose up --build -d
```

Consulta [inicio, APIs y ejemplos del backend](docs/BACKEND.md) y la [correspondencia con el PDF](docs/REQUISITOS_BACKEND.md). Las pantallas existentes todavía requieren integración con estas APIs.

Pruebas: `./services/mvnw.cmd -B -f services/pom.xml clean verify` (Java 17+, `JAVA_HOME` configurado).

Para iniciar el servicio de front-end, ejecutar los siguientes comandos:
``` bash
cd frontend\tienda-frontend;
npm start
```
