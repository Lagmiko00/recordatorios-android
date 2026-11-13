package cl.curso.recordatorios.presentacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.curso.recordatorios.datos.DatosSimulados
import cl.curso.recordatorios.datos.RepositorioRecordatorios
import cl.curso.recordatorios.modelo.Categoria
import cl.curso.recordatorios.modelo.Prioridad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface Envio {
    data object Pendiente : Envio
    data object EnCurso : Envio
    data class Guardado(val id: Long) : Envio
    data class Error(val mensaje: String) : Envio
}

data class CreacionUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val prioridad: Prioridad? = null,
    val tituloEditado: Boolean = false,
    val envio: Envio = Envio.Pendiente,
) {
    val errorTitulo get() = if (titulo.trim().length < 4) "Escribe al menos 4 caracteres." else null
    val errorDescripcion get() = if (descripcion.length > 200) "La descripción admite hasta 200 caracteres." else null
    val errorCategoria get() = if (categoria !in DatosSimulados.etiquetasPorCategoria.keys) "Selecciona una categoría." else null
    val errorPrioridad get() = if (prioridad == null) "Selecciona una prioridad." else null
    val enviando get() = envio == Envio.EnCurso
    val puedeGuardar get() = (envio == Envio.Pendiente || envio is Envio.Error) &&
        errorTitulo == null && errorDescripcion == null && errorCategoria == null && errorPrioridad == null
}

class CreacionViewModel(private val repositorio: RepositorioRecordatorios) : ViewModel() {
    private val _uiState = MutableStateFlow(CreacionUiState())
    val uiState = _uiState.asStateFlow()
    val categorias = DatosSimulados.etiquetasPorCategoria.keys.toList()
    val prioridades = listOf(Prioridad.Alta, Prioridad.Media, Prioridad.Baja)

    fun cambiarTitulo(valor: String) = editar { it.copy(titulo = valor, tituloEditado = true) }
    fun cambiarDescripcion(valor: String) = editar { it.copy(descripcion = valor) }
    fun cambiarCategoria(valor: Categoria) = editar { it.copy(categoria = valor) }
    fun cambiarPrioridad(valor: Prioridad) = editar { it.copy(prioridad = valor) }

    private fun editar(cambio: (CreacionUiState) -> CreacionUiState) {
        _uiState.update { estado ->
            if (estado.enviando || estado.envio is Envio.Guardado) estado
            else cambio(estado).copy(envio = Envio.Pendiente)
        }
    }

    fun guardar() {
        val actual = _uiState.value
        if (!actual.puedeGuardar) return
        val categoria = actual.categoria ?: return
        val prioridad = actual.prioridad ?: return
        _uiState.update { it.copy(envio = Envio.EnCurso) }
        viewModelScope.launch {
            val resultado = try {
                val nuevo = repositorio.guardar(
                    actual.titulo.trim(), actual.descripcion.trim().ifBlank { null }, categoria, prioridad
                )
                Envio.Guardado(nuevo.id)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                Envio.Error("No se pudo guardar el recordatorio. Inténtalo otra vez.")
            }
            _uiState.update { it.copy(envio = resultado) }
        }
    }
}
