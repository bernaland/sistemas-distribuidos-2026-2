# AGENTS.md

## Stack tecnologico
- El front-end debe utilizar Angular en su ultima version
- El lenguaje de programación para el front-end es Typescript
- Para los servicios del backend se debe utilizar Java y Springboot como framework web
- Todos los proyectos deben tener su equivalente de pruebas unitarias
- La base de datos se va a manejar con PostgreSQL en su ultima version

## Convenciones de código
- En el front-end, usar functional components, nunca clases
- En el front-end, nombrar archivos en kebab-case
- El front-end debe seguir el paradigma ATOMIC Design para diseñar las interfaces de usuario
- El front-end debe utilizar Bootstrap como framework para crear el contenido
- En Typescript, se deben utilizar 'barrel' files para exportar los componentes existentes en una carpeta
- En Typescript, utilizar const o let en lugar de var
- Las funciones en cualquiera de los lenguajes utilizados no deben superar las 20 lineas
- En Springboot, utilizar inyección de dependencias para registrar y utilizar todas las dependencias necesarias
- En Java, los nombres de los paquetes deben iniciar con 'com.uelbosque.{service-name}'
- Siempre que sea posible y necesario, se deben utilizar patrones de diseño
- Se debe seguir de manera obligatoria el principio KISS
- El uso de los principios SOLID, especialmente en el backend, es obligatorio

## Estructura del proyecto
frontend/
    tienda-frontend/        # Código fuente del front-end
        src/
            app/
                atoms/      # Componentes mas pequeños que deben ser reutilizados por componentes de mayor jerarquia
                molecules/  # Componentes que agrupan unicamente atoms
                organisms/  # Componentes que agrupan molecules y atoms cuando es necesario
                pages/      # Componentes que implementan la logica de negocio sobre un template
                templates/  # Plantillas que deben ser utilizadas para crear las paginas, definen el esqueleto y utilizan organisms y molecules
services/
    {service-name}/
        src/                # Codigo fuente del servicio