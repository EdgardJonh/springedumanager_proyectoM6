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
| 4 | Spring Security (roles, login/logout) | 🔲 Pendiente |
| 5 | API REST + interoperabilidad | 🔲 Pendiente |

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

## Cómo ejecutar el proyecto

```bash
./mvnw clean install
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Estructura del proyecto

```
src/main/java/cl/bootcamp/springedumanager_2/
├── controller/     # Controladores MVC
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

## Pendientes

- Autenticación y autorización con Spring Security (roles ADMIN/USER)
- Exposición de API REST (CRUD vía `@RestController`)
- Gestión de Calificaciones (entidad y repositorio ya definidos)
- Manejo global de excepciones (`@ControllerAdvice`)
- Cobertura de tests

## Autor

Proyecto desarrollado como evaluación del Módulo 6 — Bootcamp Talento Digital (Full Stack Java).
