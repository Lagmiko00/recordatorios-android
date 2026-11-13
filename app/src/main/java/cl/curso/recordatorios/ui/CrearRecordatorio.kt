package cl.curso.recordatorios.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cl.curso.recordatorios.modelo.Categoria
import cl.curso.recordatorios.modelo.Prioridad
import cl.curso.recordatorios.presentacion.CreacionUiState
import cl.curso.recordatorios.presentacion.Envio

@Composable
fun CrearRecordatorio(
    uiState: CreacionUiState,
    categorias: List<Categoria>,
    prioridades: List<Prioridad>,
    onTitulo: (String) -> Unit,
    onDescripcion: (String) -> Unit,
    onCategoria: (Categoria) -> Unit,
    onPrioridad: (Prioridad) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
) {
    val editable = !uiState.enviando && uiState.envio !is Envio.Guardado
    BackHandler(enabled = !editable) {}
    MarcoPantalla("Nuevo recordatorio", onVolver = onCancelar, volverHabilitado = editable) { modifier ->
        Column(
            modifier.imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CampoRecordatorio(
                "Título", uiState.titulo, onTitulo, Modifier.testTag("titulo"), editable,
                ayuda = "Mínimo 4 caracteres.",
                mensajeError = uiState.errorTitulo.takeIf { uiState.tituloEditado },
            )
            CampoRecordatorio(
                "Descripción (opcional)", uiState.descripcion, onDescripcion, Modifier.testTag("descripcion"), editable,
                unaLinea = false, ayuda = "${uiState.descripcion.length} de 200 caracteres",
                mensajeError = uiState.errorDescripcion,
            )
            Selector(
                "Categoría", uiState.categoria?.nombre.orEmpty(), categorias.map { it.nombre },
                onSeleccionar = { valor -> onCategoria(categorias.first { it.nombre == valor }) },
                modifier = Modifier.testTag("categoria"), habilitado = editable, ayuda = uiState.errorCategoria,
            )
            Selector(
                "Prioridad", uiState.prioridad?.etiqueta.orEmpty(), prioridades.map { it.etiqueta },
                onSeleccionar = { valor -> onPrioridad(prioridades.first { it.etiqueta == valor }) },
                modifier = Modifier.testTag("prioridad"), habilitado = editable, ayuda = uiState.errorPrioridad,
            )
            val envio = uiState.envio
            if (envio is Envio.Error) {
                Text(envio.mensaje, Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    color = MaterialTheme.colorScheme.error)
            }
            if (uiState.enviando) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            Button(
                onClick = onGuardar,
                enabled = uiState.puedeGuardar,
                modifier = Modifier.fillMaxWidth().testTag("guardar"),
            ) {
                Text(if (uiState.enviando) "Guardando…" else "Guardar recordatorio")
            }
            OutlinedButton(onClick = onCancelar, enabled = editable, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    }
}
