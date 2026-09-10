# user-service

Microservicio de Tienda Genérica. Consulta [contratos, configuración, permisos y ejemplos](../../docs/BACKEND.md).

Compilar desde la raíz con: `./services/mvnw.cmd -f services/pom.xml -pl user-service -am verify`.

PostgreSQL es la persistencia de ejecución; H2 se reserva para pruebas. Las excepciones son gateway, config-server, admin-service y platform-common, que no necesitan base de datos propia.
