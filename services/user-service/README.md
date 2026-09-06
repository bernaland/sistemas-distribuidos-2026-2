# user-service

Microservicio responsable de la gestión de usuarios, registro, perfiles y autenticación con tokens JWT.

## Requisitos
- JDK 17
- Apache Maven 3.8+

## Configuración y Puertos
- **Puerto:** `8081`
- **Base de datos:** H2 en memoria para desarrollo local (`jdbc:h2:mem:userdb`) y consola en `/h2-console` (usuario `sa`, sin contraseña). Soporta PostgreSQL en producción (`org.postgresql:postgresql` incluido en `pom.xml`).

## Usuarios Iniciales Sembrados
- **Administrador:**
  - Username: `admininicial`
  - Password: `admin123456`
  - Roles: `ROLE_ADMIN,ROLE_USER`
  - Email: `admin@uelbosque.edu.co`
- **Usuario estándar:**
  - Username: `userinicial`
  - Password: `user123456`
  - Roles: `ROLE_USER`
  - Email: `user@uelbosque.edu.co`

## Endpoints REST (`/api/users`)

| Método | Endpoint | Descripción | Autenticación |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/users` | Registrar nuevo usuario | Pública |
| `POST` | `/api/users/login` | Login y obtención de token JWT | Pública |
| `GET` | `/api/users` | Listar todos los usuarios | Requiere Token |
| `GET` | `/api/users/{id}` | Consultar usuario por ID | Requiere Token |
| `GET` | `/api/users/username/{username}` | Consultar usuario por username | Requiere Token |
| `PUT` | `/api/users/{id}` | Actualizar datos del usuario | Requiere Token |
| `DELETE` | `/api/users/{id}` | Eliminar usuario | Requiere Token |

## Ejecución y Pruebas

```powershell
# Ejecutar pruebas unitarias y de integración
mvn clean test -f services/user-service/pom.xml

# Iniciar el microservicio
mvn spring-boot:run -f services/user-service/pom.xml
```
