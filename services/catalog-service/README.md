# catalog-service

Microservicio responsable de la gestión de productos y categorías de la Tienda Genérica Virtual.

## Requisitos
- Java 17
- Maven 3.8+

## Ejecución
```bash
mvn spring-boot:run
```
El servicio iniciará en el puerto `8082`.
Consola H2 disponible en `http://localhost:8082/h2-console` (JDBC URL: `jdbc:h2:mem:catalogdb`, usuario: `sa`, sin contraseña).

## Endpoints de la API REST (`/api/products`)

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/products` | Lista todos los productos activos. Soporta query params: `?query=nombre` y `?category=categoria`. |
| `GET` | `/api/products/{id}` | Obtiene el detalle de un producto por su ID numérico. |
| `GET` | `/api/products/code/{code}` | Obtiene el detalle de un producto por su código único (ej: `PROD-001`). |
| `GET` | `/api/products/categories` | Obtiene la lista única de categorías de productos activos. |
| `POST` | `/api/products` | Crea un nuevo producto. |
| `PUT` | `/api/products/{id}` | Actualiza un producto existente. |
| `DELETE` | `/api/products/{id}` | Desactiva (soft delete) un producto por su ID. |

### Estructura del Payload (JSON)
```json
{
  "code": "PROD-005",
  "name": "Pan Tajado Bimbo 600g",
  "description": "Pan tajado blanco tradicional",
  "purchasePrice": 4500.00,
  "salePrice": 6200.00,
  "ivaRate": 0.00,
  "category": "Panadería",
  "imageUrl": "https://via.placeholder.com/150"
}
```

## Pruebas Unitarias
```bash
mvn test
```
