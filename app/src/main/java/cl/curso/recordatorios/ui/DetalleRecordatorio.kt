package cl.curso.recordatorios.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cl.curso.recordatorios.R
import cl.curso.recordatorios.presentacion.DetalleUiState

@Composable
fun DetalleRecordatorio(uiState: DetalleUiState, onVolver: () -> Unit, onReintentar: () -> Unit) {
    MarcoPantalla("Detalle", onVolver = onVolver) { modifier ->
        when (uiState) {
            DetalleUiState.Cargando -> MensajeEstado("Abriendo recordatorio…", modifier, cargando = true)
            DetalleUiState.NoEncontrado -> MensajeEstado("El recordatorio ya no está disponible.", modifier)
            is DetalleUiState.Error ->
                MensajeEstado(uiState.mensaje, modifier, etiquetaAccion = "Reintentar", onAccion = onReintentar)
            is DetalleUiState.Contenido -> {
                val registro = uiState.recordatorio
                Column(
                    modifier.verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(registro.titulo, Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
                    Text("Recordatorio n.º ${registro.id}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider()
                    Text(stringResource(R.string.categoria, registro.categoria.nombre))
                    Text(stringResource(R.string.prioridad, registro.prioridad.etiqueta))
                    Text(stringResource(R.string.estado, registro.estado.etiqueta))
                    HorizontalDivider()
                    Text("Descripción", Modifier.semantics { heading() }, style = MaterialTheme.typography.titleMedium)
                    Text(registro.descripcion ?: stringResource(R.string.sin_descripcion))
                }
            }
        }
    }
}
