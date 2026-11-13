package cl.curso.recordatorios.presentacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.curso.recordatorios.datos.RepositorioRecordatorios
import cl.curso.recordatorios.modelo.Recordatorio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data object NoEncontrado : DetalleUiState
    data class Contenido(val recordatorio: Recordatorio) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}

class DetalleViewModel(private val repositorio: RepositorioRecordatorios, private val id: Long) : ViewModel() {
    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        _uiState.update { DetalleUiState.Cargando }
        viewModelScope.launch {
            val estado = try {
                val registro = repositorio.buscar(id)
                if (registro == null) DetalleUiState.NoEncontrado else DetalleUiState.Contenido(registro)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                DetalleUiState.Error("No se pudo abrir el recordatorio.")
            }
            _uiState.update { estado }
        }
    }
}
