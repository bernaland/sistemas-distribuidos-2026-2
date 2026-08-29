# user-service

Microservicio responsable de la gestión de usuarios y proveedor de identidad OAuth2/OpenID Connect.

Arranque rápido:
- Requisitos: JDK 17, Maven
- Ejecutar: mvn spring-boot:run

Usuario inicial:
- username: `admininicial`
- password: `admin123456`

OAuth2/OIDC (Identity Provider):
- Metadata OIDC: `/.well-known/openid-configuration`
- JWK Set: `/oauth2/jwks`
- Token endpoint: `/oauth2/token`
- Authorization endpoint: `/oauth2/authorize`

Clientes registrados por defecto:
- `user-service-client` (client_credentials, scopes: `users.read`, `users.write`)
- `user-service-web-client` (authorization_code + refresh_token, scopes: `openid`, `profile`, `users.read`, `users.write`)

Endpoints protegidos (Bearer JWT):
- `GET /api/users` requiere scope `users.read`
- `GET /api/users/{username}` requiere scope `users.read`
- `POST /api/users` requiere scope `users.write`
