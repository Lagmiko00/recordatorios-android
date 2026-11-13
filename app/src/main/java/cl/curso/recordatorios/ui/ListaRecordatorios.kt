package cl.curso.recordatorios.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.curso.recordatorios.R
import cl.curso.recordatorios.datos.DatosSimulados
import cl.curso.recordatorios.modelo.EstadoPantalla
import cl.curso.recordatorios.modelo.EstadoRecordatorio
import cl.curso.recordatorios.modelo.Prioridad
import cl.curso.recordatorios.modelo.Recordatorio
import cl.curso.recordatorios.presentacion.ListaUiState

@Composable
fun ListaRecordatorios(
    uiState: ListaUiState,
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (Long) -> Unit,
    onReintentar: () -> Unit,
    onEstado: (String?) -> Unit,
    onPrioridad: (String?) -> Unit,
    onLimpiar: () -> Unit,
) {
    MarcoPantalla(
        titulo = stringResource(R.string.app_name),
        accion = {
            if (uiState.consulta is EstadoPantalla.Contenido) {
                ExtendedFloatingActionButton(
                    onClick = onCrearRecordatorio,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) { Text("Nuevo recordatorio") }
            }
        },
    ) { modifier ->
        when (val estado = uiState.consulta) {
            EstadoPantalla.Inicial, EstadoPantalla.Cargando ->
                MensajeEstado("Cargando recordatorios…", modifier, cargando = true)
            EstadoPantalla.Vacio ->
                MensajeEstado("Todavía no hay recordatorios.", modifier, etiquetaAccion = "Crear recordatorio", onAccion = onCrearRecordatorio)
            is EstadoPantalla.Error ->
                MensajeEstado(estado.mensaje, modifier, etiquetaAccion = "Reintentar", onAccion = onReintentar)
            is EstadoPantalla.Contenido -> {
                val visibles = uiState.visibles()
                val todos = stringResource(R.string.todos)
                val todas = stringResource(R.string.todas)
                LazyColumn(
                    modifier = modifier.fillMaxHeight().testTag("lista"),
                    contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Selector(
                                stringResource(R.string.filtro_estado), uiState.estado ?: todos,
                                listOf(todos, EstadoRecordatorio.Pendiente.etiqueta, EstadoRecordatorio.Completado.etiqueta),
                                onSeleccionar = { onEstado(it.takeUnless { valor -> valor == todos }) },
                                modifier = Modifier.testTag("filtro_estado"),
                            )
                            Selector(
                                stringResource(R.string.filtro_prioridad), uiState.prioridad ?: todas,
                                listOf(todas, Prioridad.Alta.etiqueta, Prioridad.Media.etiqueta, Prioridad.Baja.etiqueta),
                                onSeleccionar = { onPrioridad(it.takeUnless { valor -> valor == todas }) },
                                modifier = Modifier.testTag("filtro_prioridad"),
                            )
                            Text(stringResource(R.string.cantidad, visibles.size, estado.recordatorios.size))
                            if (uiState.estado != null || uiState.prioridad != null) {
                                TextButton(onClick = onLimpiar, modifier = Modifier.testTag("restablecer")) {
                                    Text(stringResource(R.string.restablecer))
                                }
                            }
                        }
                    }
                    if (visibles.isEmpty()) item { Text(stringResource(R.string.sin_resultados)) }
                    items(visibles, key = { it.id }) { recordatorio ->
                        TarjetaRecordatorio(recordatorio, onRecordatorioClick)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaRecordatorio(
    recordatorio: Recordatorio,
    onSeleccionar: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = { onSeleccionar(recordatorio.id) },
        modifier = modifier.fillMaxWidth().testTag("recordatorio_${recordatorio.id}"),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(recordatorio.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.categoria, DatosSimulados.etiquetasPorCategoria[recordatorio.categoria]
                    ?: recordatorio.categoria.nombre),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(stringResource(R.string.prioridad, recordatorio.prioridad.etiqueta), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.estado, recordatorio.estado.etiqueta), color = MaterialTheme.colorScheme.primary)
        }
    }
}
