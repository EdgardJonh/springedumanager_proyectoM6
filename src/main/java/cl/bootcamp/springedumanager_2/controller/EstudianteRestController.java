package cl.bootcamp.springedumanager_2.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.bootcamp.springedumanager_2.dto.ErrorResponse;
import cl.bootcamp.springedumanager_2.exception.ReglaNegocioException;
import cl.bootcamp.springedumanager_2.model.Estudiante;
import cl.bootcamp.springedumanager_2.service.EstudianteService;

/*
 * =====================================================
 * API REST - ESTUDIANTES (Leccion 5)
 * =====================================================
 *
 * @RestController = @Controller + @ResponseBody
 *
 * Cada metodo devuelve directamente el objeto,
 * y Spring lo serializa automaticamente a JSON
 * (usando Jackson, incluido por defecto).
 *
 * A diferencia de EstudianteController (MVC), esta
 * clase no devuelve vistas Thymeleaf ni hace redirect:
 * responde datos crudos pensados para ser consumidos
 * por Postman, RestTemplate u otro cliente externo.
 *
 * Reutiliza EstudianteService, la misma capa de
 * negocio que ya usa la version MVC, por lo que las
 * reglas de negocio (correo obligatorio, correo
 * duplicado, no eliminar con inscripciones, etc.)
 * se aplican igual desde ambos canales.
 */
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteRestController {

    private final EstudianteService estudianteService;

    public EstudianteRestController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    /*
     * GET /api/estudiantes
     *
     * Devuelve la lista completa en formato JSON.
     */
    @GetMapping
    public List<Estudiante> listar() {
        return estudianteService.listar();
    }

    /*
     * GET /api/estudiantes/{id}
     *
     * 200 OK        + el estudiante, si existe.
     * 404 Not Found + mensaje, si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable int id) {

        return estudianteService.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(
                                "El estudiante con id " + id + " no existe."))
                );
    }

    /*
     * POST /api/estudiantes
     *
     * 201 Created + el estudiante creado (con su nuevo id).
     * 400 Bad Request + mensaje, si viola una regla de negocio
     * (nombre/correo vacio, correo duplicado).
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Estudiante estudiante) {

        // Un alta siempre debe generar un id nuevo,
        // sin importar lo que envie el cliente.
        estudiante.setId(0);

        try {

            Estudiante creado = estudianteService.guardar(estudiante);

            URI ubicacion = URI.create("/api/estudiantes/" + creado.getId());

            return ResponseEntity.created(ubicacion).body(creado);

        } catch (ReglaNegocioException e) {

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        }
    }

    /*
     * PUT /api/estudiantes/{id}
     *
     * 200 OK           + el estudiante actualizado.
     * 404 Not Found     si el id no existe.
     * 400 Bad Request   si viola una regla de negocio.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable int id,
            @RequestBody Estudiante estudiante) {

        if (estudianteService.obtenerPorId(id).isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "El estudiante con id " + id + " no existe."));
        }

        // El id de la URL manda, sin importar lo que
        // venga en el cuerpo del request.
        estudiante.setId(id);

        try {

            return ResponseEntity.ok(estudianteService.guardar(estudiante));

        } catch (ReglaNegocioException e) {

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        }
    }

    /*
     * DELETE /api/estudiantes/{id}
     *
     * 204 No Content    si se elimino correctamente.
     * 404 Not Found     si el id no existe.
     * 409 Conflict      si tiene inscripciones/calificaciones
     *                   registradas (regla de negocio).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {

        if (estudianteService.obtenerPorId(id).isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "El estudiante con id " + id + " no existe."));
        }

        try {

            estudianteService.eliminar(id);

            return ResponseEntity.noContent().build();

        } catch (ReglaNegocioException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));

        }
    }
}
