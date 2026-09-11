# SpringEduManager

Aplicación web de gestión académica desarrollada para el **Módulo 6: Desarrollo de aplicaciones JEE con Spring Framework** (Talento Digital / Alkemy).

Permite administrar estudiantes, cursos, inscripciones y evaluaciones desde una única plataforma, reemplazando el uso de hojas de cálculo y formularios aislados.

## Tecnologías utilizadas

- **Java 25**
- **Spring Boot 4.1.1**
- **Spring MVC** — controladores y vistas
- **Spring Data JPA** — persistencia de datos
- **MySQL** — base de datos
- **Thymeleaf** — motor de plantillas
- **Bean Validation** (`spring-boot-starter-validation`)
- **Spring Security** — autenticación y autorización basada en roles
- **Maven** — gestor de dependencias y ciclo de vida
- **Bootstrap 5 + SweetAlert2** — interfaz de usuario

## Arquitectura

El proyecto sigue una arquitectura por capas (MVC):

```
Controller → Service → Repository (Spring Data JPA) → MySQL
```

- **`controller/`** — controladores Spring MVC (`@Controller`), manejan rutas y vistas Thymeleaf.
- **`service/`** — lógica de negocio y validaciones (excepción de negocio custom `ReglaNegocioException`).
- **`repository/`** — interfaces `JpaRepository` con queries derivadas y JPQL.
- **`model/`** — entidades JPA: `Estudiante`, `Curso`, `Inscripcion`, `Evaluacion`, `Calificacion`.
- **`templates/`** — vistas Thymeleaf organizadas por módulo.

## Estado del proyecto por lección

| Lección | Contenido | Estado |
|---|---|---|
| 1 | Gestor de proyectos (Maven) | ✅ Completo |
| 2 | Spring MVC (entidades, controladores, vistas) | ✅ Completo |
| 3 | Acceso a datos (JPA, repositorios, servicios) | ✅ Completo |
| 4 | Spring Security (roles, login/logout) | ✅ Completo |
| 5 | API REST + interoperabilidad | ✅ Completo (JWT opcional pendiente) |

## Requisitos previos

- JDK 25
- Maven (o usar el wrapper `mvnw` incluido)
- MySQL 8+ corriendo localmente

## Configuración

La aplicación lee la configuración de base de datos desde variables de entorno (no hay credenciales hardcodeadas en el repositorio):

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL de conexión JDBC | `jdbc:mysql://localhost:3306/springedumanager` |
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | *(sin valor por defecto, obligatoria)* |

Antes de crear la base de datos, asegúrate de tenerla creada en MySQL:

```sql
CREATE DATABASE springedumanager;
```

### Definir las variables de entorno

**PowerShell (sesión actual):**
```powershell
$env:DB_PASSWORD = "tu_password"
```

**Bash / Git Bash:**
```bash
export DB_PASSWORD=tu_password
```

**Desde el IDE (Eclipse/IntelliJ):** configura `DB_PASSWORD` (y opcionalmente `DB_USERNAME`, `DB_URL`) en la configuración de ejecución (Run Configuration) de la aplicación.

## Autenticación y roles

La aplicación está protegida con Spring Security. Todas las rutas requieren haber iniciado sesión, excepto `/login` y los recursos estáticos.

| Usuario | Contraseña | Rol | Permisos |
|---|---|---|---|
| `admin` | `admin123` | `ADMIN` | Acceso total, incluyendo crear/editar/eliminar cursos |
| `usuario` | `user123` | `USER` | Solo lectura/navegación (no puede crear, editar ni eliminar cursos) |

Los usuarios se definen en memoria en `SecurityConfig`, leyendo sus credenciales desde `application.properties` (`app.security.admin.*` / `app.security.user.*`). Las contraseñas se almacenan cifradas con BCrypt.

La ruta `POST /cursos/guardar` y `POST /cursos/eliminar/**` están restringidas al rol `ADMIN`; el botón "Nuevo Curso" y las acciones de editar/eliminar se ocultan automáticamente en la vista para el rol `USER` (vía `sec:authorize`).

## API REST

Además de las vistas Thymeleaf, el sistema expone una API REST en JSON para Estudiantes y Cursos, pensada para ser consumida por Postman, `RestTemplate` u otro cliente externo.

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `GET` | `/api/estudiantes` | Lista todos los estudiantes | Autenticado |
| `GET` | `/api/estudiantes/{id}` | Obtiene un estudiante por id | Autenticado |
| `POST` | `/api/estudiantes` | Crea un estudiante | Autenticado |
| `PUT` | `/api/estudiantes/{id}` | Actualiza un estudiante | Autenticado |
| `DELETE` | `/api/estudiantes/{id}` | Elimina un estudiante | Autenticado |
| `GET` | `/api/cursos` | Lista todos los cursos | Autenticado |
| `GET` | `/api/cursos/{id}` | Obtiene un curso por id | Autenticado |
| `POST` | `/api/cursos` | Crea un curso | **Solo ADMIN** |
| `PUT` | `/api/cursos/{id}` | Actualiza un curso | **Solo ADMIN** |
| `DELETE` | `/api/cursos/{id}` | Elimina un curso | **Solo ADMIN** |

**Autenticación:** los endpoints `/api/**` usan HTTP Basic (usuario/clave de la tabla de arriba), no requieren sesión de navegador ni token CSRF. Una petición sin credenciales responde `401 Unauthorized` en JSON.

**Códigos de respuesta:** `200` OK, `201` Created (con header `Location`), `204` No Content (delete), `400` Bad Request (regla de negocio violada, ej. correo duplicado), `403` Forbidden (rol insuficiente), `404` Not Found, `409` Conflict (ej. intentar eliminar un curso con inscripciones).

### Probar con curl

```bash
# Listar cursos
curl -u admin:admin123 http://localhost:8080/api/cursos

# Crear un curso (solo ADMIN)
curl -u admin:admin123 -H "Content-Type: application/json" \
  -d '{"nombre":"Java Básico","descripcion":"Introducción a Java"}' \
  http://localhost:8080/api/cursos

# Actualizar
curl -X PUT -u admin:admin123 -H "Content-Type: application/json" \
  -d '{"nombre":"Java Básico","descripcion":"Actualizado"}' \
  http://localhost:8080/api/cursos/1

# Eliminar
curl -X DELETE -u admin:admin123 http://localhost:8080/api/cursos/1
```

### Probar con Postman

1. En la pestaña **Authorization** de cada request, selecciona **Basic Auth** e ingresa `admin` / `admin123` (o `usuario` / `user123`).
2. Para `POST`/`PUT`, en **Body** selecciona `raw` + `JSON` y envía, por ejemplo: `{"nombre": "...", "descripcion": "..."}` (cursos) o `{"nombre": "...", "email": "..."}` (estudiantes).

### Pendiente (plus opcional)

- Asegurar los endpoints REST con JWT en lugar de HTTP Basic.

## Cómo ejecutar el proyecto

```bash
./mvnw clean install
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Estructura del proyecto

```
src/main/java/cl/bootcamp/springedumanager_2/
├── config/         # Configuración de Spring Security
├── controller/     # Controladores MVC + REST (@RestController)
├── dto/            # DTOs de la API REST (ej. ErrorResponse)
├── exception/      # Excepciones de negocio
├── model/          # Entidades JPA
├── repository/     # Repositorios Spring Data JPA
└── service/        # Lógica de negocio

src/main/resources/
├── static/         # JS y assets estáticos
├── templates/       # Vistas Thymeleaf
└── application.properties
```

## Funcionalidades implementadas

- CRUD de Estudiantes
- CRUD de Cursos
- Inscripción de estudiantes en cursos
- Gestión de Evaluaciones (con control de ponderación máxima de 100% por curso)
- Autenticación y autorización con Spring Security (roles ADMIN/USER)
- API REST para Estudiantes y Cursos (`@RestController`, CRUD completo, HTTP Basic)

## Pendientes

- Asegurar la API REST con JWT (plus opcional)
- Gestión de Calificaciones (entidad y repositorio ya definidos)
- Manejo global de excepciones (`@ControllerAdvice`) para las vistas MVC
- Cobertura de tests

## Autor

Proyecto desarrollado como evaluación del Módulo 6 — Bootcamp Talento Digital (Full Stack Java).
