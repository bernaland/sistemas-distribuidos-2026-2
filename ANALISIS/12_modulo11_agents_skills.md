# Auditoría Técnica - Módulo 11: Personalizaciones de Agentes (`.agents/`)

**Auditor Senior:** Antigravity  
**Fecha:** 2026-10-18  
**Total de Archivos Auditados en este Módulo:** 56  

---

## 1. Resumen Ejecutivo del Módulo

El directorio `.agents/skills/` contiene las capacidades especializadas ("skills") configuradas en el workspace para guiar a los agentes de IA (Antigravity / Codex) en tareas de desarrollo, auditoría, pruebas y diseño arquitectónico:
1. **`angular-developer` (41 archivos):** Base de conocimiento exhaustiva sobre Angular moderno (v17-v19). Contiene guías de reactividad con Signals (`signals-overview.md`, `linked-signal.md`, `resource.md`), inyección de dependencias (`di-fundamentals.md`, `injection-context.md`), enrutamiento, formularios y mejores prácticas. **Contraste clave:** Mientras esta skill promueve estándares modernos de Angular (v18+, Signals, standalone components), el frontend real de la aplicación permanece estancado en Angular 16.2.0 con NgModules y clases clásicas.
2. **`angular-testing` (1 archivo):** Skill declarada obsoleta (`DEPRECATED`) en su encabezado (`SKILL.md:4`), recomendando utilizar las skills oficiales de Angular.
3. **`code-review` (2 archivos):** Skill de revisión de código automatizada en dos ejes (Estándares y Especificación) con configuración para agentes OpenAI (`openai.yaml`).
4. **`frontend-design` (2 archivos):** Guía de diseño visual distintivo y licencia (`LICENSE.txt`).
5. **`image-to-code` (1 archivo, 1228 líneas):** Skill avanzada para transformar conceptos visuales e imágenes en código de interfaz limpio y sin anidamiento excesivo.
6. **`java-junit` (1 archivo):** Guía de mejores prácticas para pruebas unitarias con JUnit 5, pruebas parametrizadas y aserciones.
7. **`java-microservices` (7 archivos):** Marco arquitectónico para microservicios Java/Spring Cloud. Contiene catálogo de patrones (`PATTERNS.md`: Saga, Circuit Breaker, Outbox, API Gateway), guía de implementación (`GUIDE.md`), esquemas JSON (`schema.json`), listas de chequeo YAML (`microservices-checklist.yaml`) y un script de validación Python (`validate.py`).
8. **`java-springboot` (1 archivo):** Buenas prácticas de desarrollo Spring Boot, inyección de dependencias, configuración externa y observabilidad.

---

## 2. Inventario y Análisis Detallado Archivo por Archivo (56 Archivos)

### 2.1 Skill: `angular-developer` (41 archivos)
1. `.agents/skills/angular-developer/SKILL.md` (144 líneas): Manifiesto principal que indexa todas las referencias técnicas.
2. `references/angular-animations.md` (160 líneas): Animaciones con `@angular/animations`, transiciones y triggers.
3. `references/angular-aria.md` (597 líneas): Guía integral de accesibilidad ARIA para componentes Angular.
4. `references/cli.md` (86 lines): Referencia de comandos Angular CLI (`ng new`, `ng generate`, `ng build`).
5. `references/component-harnesses.md` (57 líneas): Testing con CDK Component Harnesses.
6. `references/component-styling.md` (91 líneas): Encapsulamiento de estilos CSS (`Emulated`, `None`, `ShadowDom`).
7. `references/components.md` (135 líneas): Creación de componentes modernos (Standalone vs NgModules).
8. `references/creating-services.md` (93 líneas): Servicios inyectables con `@Injectable({ providedIn: 'root' })`.
9. `references/data-resolvers.md` (69 líneas): Resolvers de rutas funcionales con `ResolveFn`.
10. `references/define-routes.md` (85 líneas): Definición moderna de rutas con `provideRouter` y lazy loading.
11. `references/defining-providers.md` (72 líneas): Proveedores de dependencias (`useClass`, `useValue`, `useFactory`).
12. `references/di-fundamentals.md` (116 líneas): Fundamentos de inyección con la función `inject()`.
13. `references/e2e-testing.md` (39 líneas): Directrices para pruebas end-to-end con Playwright/Cypress.
14. `references/effects.md` (83 líneas): Efectos reactivos con `effect()` en el contexto de inyección.
15. `references/environment-configuration.md` (137 líneas): Manejo de configuraciones por ambiente (`environments`).
16. `references/hierarchical-injectors.md` (43 líneas): Árbol de inyectores jerárquicos (ElementInjector vs EnvironmentInjector).
17. `references/host-elements.md` (80 líneas): Vinculación de atributos y clases al elemento host con `host:` o `HostBinding`.
18. `references/http-client.md` (108 líneas): Comunicación HTTP reactiva con `provideHttpClient`, interceptores funcionales y `httpResource`.
19. `references/injection-context.md` (63 líneas): Reglas sobre cuándo y dónde es válido invocar `inject()`.
20. `references/inputs.md` (101 líneas): Entradas de componentes mediante Signal Inputs (`input()`, `input.required()`).
21. `references/linked-signal.md` (59 líneas): Uso de `linkedSignal()` para estado derivado mutable (introducido en Angular 19).
22. `references/loading-strategies.md` (63 líneas): Estrategias de carga diferida de bloques `@defer`.
23. `references/mcp.md` (96 líneas): Integración con Model Context Protocol para Angular CLI.
24. `references/migrations.md` (30 líneas): Guía de migración con esquemáticos (`ng generate @angular/core:...`).
25. `references/naming-conventions.md` (76 líneas): Convenciones de nomenclatura (kebab-case para archivos, PascalCase para clases).
26. `references/navigate-to-routes.md` (69 líneas): Navegación imperativa con `Router.navigate` y directiva `routerLink`.
27. `references/outputs.md` (86 líneas): Emisión de eventos con la función `output()`.
28. `references/pipes.md` (145 líneas): Creación y optimización de pipes puros e impuros.
29. `references/reactive-forms.md` (118 líneas): Formularios reactivos estructurados (`FormGroup`, `FormControl`, `Validators`).
30. `references/rendering-strategies.md` (44 líneas): Estrategias de renderizado (SSR, SSG, Hydration).
31. `references/resource.md` (74 líneas): API `resource()` para carga asíncrona de datos con Signals (Angular 19).
32. `references/route-animations.md` (56 líneas): Transiciones animadas entre vistas del enrutador.
33. `references/route-guards.md` (52 líneas): Protección de rutas con guards funcionales (`CanActivateFn`).
34. `references/router-lifecycle.md` (45 líneas): Eventos del ciclo de vida del router (`NavigationStart`, `NavigationEnd`).
35. `references/router-testing.md` (88 líneas): Pruebas de enrutamiento con `RouterTestingHarness`.
36. `references/show-routes-with-outlets.md` (68 líneas): Vistas con múltiples outlets y rutas hijas.
37. `references/signal-forms.md` (905 líneas): Especificación detallada y patrones experimentales de formularios basados en Signals.
38. `references/signals-overview.md` (94 líneas): Visión general del modelo de reactividad con Signals (`signal`, `computed`).
39. `references/tailwind-css.md` (69 líneas): Configuración e integración con TailwindCSS.
40. `references/template-driven-forms.md` (114 líneas): Formularios dirigidos por plantilla con `ngModel`.
41. `references/testing-fundamentals.md` (63 líneas): Principios fundamentales de pruebas con TestBed y Vitest/Jasmine.

---

### 2.2 Skill: `angular-testing` (1 archivo)
42. `.agents/skills/angular-testing/SKILL.md` (20 líneas): Documento deprecado que remite al repositorio oficial de Angular Skills.

---

### 2.3 Skill: `code-review` (2 archivos)
43. `.agents/skills/code-review/SKILL.md` (87 líneas): Instrucciones para orquestar revisiones de código en dos ramas paralelas: cumplimiento de estándares del repositorio y adecuación al requerimiento funcional.
44. `.agents/skills/code-review/agents/openai.yaml` (3 líneas): Configuración de agente compatible con la API de OpenAI.

---

### 2.4 Skill: `frontend-design` (2 archivos)
45. `.agents/skills/frontend-design/LICENSE.txt` (177 líneas): Licencia de código abierto del componente de diseño.
46. `.agents/skills/frontend-design/SKILL.md` (55 líneas): Directrices para generar interfaces con jerarquía visual distintiva, tipografía intencional y paletas de color no genéricas.

---

### 2.5 Skill: `image-to-code` (1 archivo)
47. `.agents/skills/image-to-code/SKILL.md` (1228 líneas): Documento exhaustivo de ingeniería de prompts para traducción pixel-perfect de mockups e imágenes a código web limpio, accesible y responsive.

---

### 2.6 Skill: `java-junit` (1 archivo)
48. `.agents/skills/java-junit/SKILL.md` (63 líneas): Buenas prácticas para testing con JUnit 5: ciclo de vida (`@BeforeEach`, `@AfterEach`), `@ParameterizedTest` con `@ValueSource` / `@CsvSource`, y aserciones con AssertJ.

---

### 2.7 Skill: `java-microservices` (7 archivos)
49. `.agents/skills/java-microservices/SKILL.md` (180 líneas): Guía operativa para diseño, implementación y evaluación de arquitecturas de microservicios.
50. `assets/config.yaml` (41 líneas): Configuración de validación para estándares de microservicios (cobertura mínima, puertos, tracing).
51. `assets/microservices-checklist.yaml` (6 líneas): Lista de chequeo para despliegues de microservicios.
52. `assets/schema.json` (60 líneas): Esquema JSON formal para validar la estructura y metadatos de configuración de microservicios.
53. `references/GUIDE.md` (95 líneas): Guía paso a paso para bootstrap de servicios Spring Cloud y observabilidad.
54. `references/PATTERNS.md` (87 líneas): Catálogo de patrones de arquitectura distribuida: API Gateway, Service Mesh, Saga Pattern, CQRS, Circuit Breaker con Resilience4j y Outbox Pattern.
55. `scripts/validate.py` (131 líneas): Script utilitario en Python para validar que los archivos YAML y JSON de la skill cumplan con los esquemas definidos.

---

### 2.8 Skill: `java-springboot` (1 archivo)
56. `.agents/skills/java-springboot/SKILL.md` (65 líneas): Reglas y recomendaciones para desarrollo en Spring Boot: inyección por constructor (`never field injection`), configuración externalizada con `@ConfigurationProperties`, validación con Jakarta Bean Validation y perfiles de ejecución (`application-{profile}.yml`).

---

## 3. Matriz de Hallazgos y Observaciones sobre Customizaciones de Agentes

| ID | Severidad | Archivo y Línea | Descripción | Recomendación |
|---|---|---|---|---|
| **AGT-01** | **Medio** | `.agents/skills/angular-testing/SKILL.md:4` | Skill obsoleta (`DEPRECATED`) presente en el repositorio. Provoca confusión al coexistir con `angular-developer`. | Eliminar la carpeta `angular-testing` o consolidar sus directrices útiles dentro de `angular-developer/references/testing-fundamentals.md`. |
| **AGT-02** | **Bajo** | `.agents/skills/angular-developer/references/tailwind-css.md` | La skill incluye guía de Tailwind CSS cuando las reglas del proyecto (`AGENTS.md:13`) prescriben expresamente el uso de **Bootstrap**. | Ajustar las guías de estilos para priorizar Bootstrap 5 sobre Tailwind CSS, en concordancia con `AGENTS.md`. |
| **AGT-03** | **Informativo** | `.agents/skills/java-microservices/scripts/validate.py` | Script de validación autónomo en Python; se encuentra funcional y sintácticamente correcto. | Mantener como herramienta de CI/CD para validar configuraciones de agentes. |
