package cl.bootcamp.springedumanager_2.dto;

/*
 * Cuerpo JSON estandar para respuestas
 * de error de la API REST.
 *
 * Ejemplo:
 *
 * { "mensaje": "El curso no existe." }
 */
public record ErrorResponse(String mensaje) {
}
