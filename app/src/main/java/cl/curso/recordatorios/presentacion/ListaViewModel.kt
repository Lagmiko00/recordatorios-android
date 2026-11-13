package cl.curso.recordatorios.presentacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.curso.recordatorios.datos.RepositorioRecordatorios
import cl.curso.recordatorios.datos.filtrarRecordatorios
import cl.curso.recordatorios.datos.prepararEstado
import cl.curso.recordatorios.modelo.EstadoPantalla
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListaUiState(
    val consulta: EstadoPantalla = EstadoPantalla.Cargando,
    val estado: String? = null,
    val prioridad: String? = null,
) {
    fun visibles() = filtrarRecordatorios(
        (consulta as? EstadoPantalla.Contenido)?.recordatorios.orEmpty(), estado, prioridad
    )
}

class ListaViewModel(private val repositorio: RepositorioRecordatorios) : ViewModel() {
    private val _uiState = MutableStateFlow(ListaUiState())
    val uiState = _uiState.asStateFlow()
    private var carga: Job? = null

    init { cargar() }

    fun cargar() {
        carga?.cancel()
        _uiState.update { it.copy(consulta = EstadoPantalla.Cargando) }
        carga = viewModelScope.launch {
            val resultado = try {
                prepararEstado(repositorio.obtener())
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                EstadoPantalla.Error("No se pudo cargar la lista de recordatorios.")
            }
            _uiState.update { it.copy(consulta = resultado) }
        }
    }

    fun cambiarEstado(valor: String?) = _uiState.update { it.copy(estado = valor) }
    fun cambiarPrioridad(valor: String?) = _uiState.update { it.copy(prioridad = valor) }
    fun limpiarFiltros() = _uiState.update { it.copy(estado = null, prioridad = null) }
}
