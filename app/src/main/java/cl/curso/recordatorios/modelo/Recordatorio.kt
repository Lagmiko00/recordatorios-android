package cl.curso.recordatorios.modelo

data class Categoria(val id: String, val nombre: String) {
    init {
        require(id.isNotBlank()) { "La categoría necesita un identificador." }
        require(nombre.isNotBlank()) { "La categoría necesita un nombre." }
    }
}

sealed class Prioridad(val etiqueta: String) {
    data object Alta : Prioridad("Alta")
    data object Media : Prioridad("Media")
    data object Baja : Prioridad("Baja")
}

sealed class EstadoRecordatorio(val etiqueta: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object Completado : EstadoRecordatorio("Completado")
}

fun validarRecordatorio(id: Long, titulo: String): List<String> = buildList {
    if (id <= 0) add("El identificador debe ser mayor que cero.")
    if (titulo.isBlank()) add("El título es obligatorio.")
}

data class Recordatorio(
    val id: Long,
    val titulo: String,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio,
    val descripcion: String? = null,
) {
    init {
        require(validarRecordatorio(id, titulo).isEmpty()) {
            validarRecordatorio(id, titulo).joinToString(" ")
        }
    }
}

sealed class EstadoPantalla {
    data object Inicial : EstadoPantalla()
    data object Cargando : EstadoPantalla()
    data class Contenido(val recordatorios: List<Recordatorio>) : EstadoPantalla()
    data object Vacio : EstadoPantalla()
    data class Error(val mensaje: String) : EstadoPantalla()
}
