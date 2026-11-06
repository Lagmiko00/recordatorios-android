package cl.curso.recordatorios.datos

import cl.curso.recordatorios.modelo.Categoria
import cl.curso.recordatorios.modelo.EstadoPantalla
import cl.curso.recordatorios.modelo.EstadoRecordatorio
import cl.curso.recordatorios.modelo.Prioridad
import cl.curso.recordatorios.modelo.Recordatorio

object DatosSimulados {
    private val estudio = Categoria("estudio", "Estudio")
    private val personal = Categoria("personal", "Personal")
    private val trabajo = Categoria("trabajo", "Trabajo")

    val etiquetasPorCategoria: Map<Categoria, String> = mapOf(
        estudio to "Estudio",
        personal to "Personal",
        trabajo to "Trabajo",
    )

    val recordatorios: List<Recordatorio> = listOf(
        Recordatorio(1, "Preparar examen", estudio, Prioridad.Alta,
            EstadoRecordatorio.Pendiente, "Repasar los temas de la próxima evaluación."),
        Recordatorio(2, "Comprar materiales", personal, Prioridad.Media,
            EstadoRecordatorio.Pendiente),
        Recordatorio(3, "Leer apuntes de clase", estudio, Prioridad.Baja,
            EstadoRecordatorio.Completado, "Revisar las notas de la última sesión."),
        Recordatorio(4, "Preparar reunión", trabajo, Prioridad.Alta,
            EstadoRecordatorio.Pendiente, "Anotar los temas que debemos revisar."),
        Recordatorio(5, "Ordenar documentos", personal, Prioridad.Baja,
            EstadoRecordatorio.Pendiente),
        Recordatorio(6, "Confirmar horario", trabajo, Prioridad.Media,
            EstadoRecordatorio.Completado),
    )

    val categorias: Set<Categoria> = recordatorios.map { it.categoria }.toSet()
}

fun prepararEstado(recordatorios: List<Recordatorio>): EstadoPantalla = when {
    recordatorios.map { it.id }.toSet().size != recordatorios.size ->
        EstadoPantalla.Error("Hay identificadores de recordatorio repetidos.")
    recordatorios.isEmpty() -> EstadoPantalla.Vacio
    else -> EstadoPantalla.Contenido(recordatorios)
}

fun filtrarRecordatorios(
    recordatorios: List<Recordatorio>,
    estado: String? = null,
    prioridad: String? = null,
): List<Recordatorio> = recordatorios.filter {
    (estado == null || it.estado.etiqueta == estado) &&
        (prioridad == null || it.prioridad.etiqueta == prioridad)
}
