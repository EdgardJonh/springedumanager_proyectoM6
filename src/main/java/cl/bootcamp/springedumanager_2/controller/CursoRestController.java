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
import cl.bootcamp.springedumanager_2.model.Curso;
import cl.bootcamp.springedumanager_2.service.CursoService;

/*
 * =====================================================
 * API REST - CURSOS (Leccion 5)
 * =====================================================
 *
 * Igual que EstudianteRestController: reutiliza
 * CursoService (misma capa de negocio que la version
 * MVC en CursoController), y responde JSON en lugar
 * de vistas Thymeleaf.
 *
 * La creacion/edicion/eliminacion de cursos por esta
 * via tambien esta restringida al rol ADMIN, igual
 * que en la version MVC (ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoRestController {

    private final CursoService cursoService;

    public CursoRestController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    /*
     * GET /api/cursos
     */
    @GetMapping
    public List<Curso> listar() {
        return cursoService.listar();
    }

    /*
     * GET /api/cursos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable int id) {

        return cursoService.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(
                                "El curso con id " + id + " no existe."))
                );
    }

    /*
     * POST /api/cursos
     *
     * Solo ADMIN (ver SecurityConfig).
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Curso curso) {

        curso.setId(0);

        try {

            Curso creado = cursoService.guardar(curso);

            URI ubicacion = URI.create("/api/cursos/" + creado.getId());

            return ResponseEntity.created(ubicacion).body(creado);

        } catch (ReglaNegocioException e) {

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        }
    }

    /*
     * PUT /api/cursos/{id}
     *
     * Solo ADMIN (ver SecurityConfig).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable int id,
            @RequestBody Curso curso) {

        if (cursoService.obtenerPorId(id).isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "El curso con id " + id + " no existe."));
        }

        curso.setId(id);

        try {

            return ResponseEntity.ok(cursoService.guardar(curso));

        } catch (ReglaNegocioException e) {

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));

        }
    }

    /*
     * DELETE /api/cursos/{id}
     *
     * Solo ADMIN (ver SecurityConfig).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {

        if (cursoService.obtenerPorId(id).isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "El curso con id " + id + " no existe."));
        }

        try {

            cursoService.eliminar(id);

            return ResponseEntity.noContent().build();

        } catch (ReglaNegocioException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));

        }
    }
}
