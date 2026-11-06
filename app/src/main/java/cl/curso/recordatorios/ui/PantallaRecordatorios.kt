package cl.curso.recordatorios.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.curso.recordatorios.R
import cl.curso.recordatorios.datos.DatosSimulados
import cl.curso.recordatorios.datos.filtrarRecordatorios
import cl.curso.recordatorios.datos.prepararEstado
import cl.curso.recordatorios.modelo.EstadoPantalla
import cl.curso.recordatorios.modelo.Recordatorio

@Composable
fun PantallaRecordatorios() {
    var estado by remember { mutableStateOf<EstadoPantalla>(EstadoPantalla.Inicial) }
    LaunchedEffect(Unit) {
        estado = EstadoPantalla.Cargando
        estado = prepararEstado(DatosSimulados.recordatorios)
    }
    ContenidoRecordatorios(estado)
}

@Composable
fun ContenidoRecordatorios(estado: EstadoPantalla) {
    var filtroEstado by rememberSaveable { mutableStateOf<String?>(null) }
    var filtroPrioridad by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold { espacioSistema ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(espacioSistema).testTag("lista"),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold)
                }
            }
            when (estado) {
                EstadoPantalla.Inicial, EstadoPantalla.Cargando -> item {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.cargando))
                }
                EstadoPantalla.Vacio -> item { Text(stringResource(R.string.lista_vacia)) }
                is EstadoPantalla.Error -> item {
                    Text(stringResource(R.string.error, estado.mensaje), color = MaterialTheme.colorScheme.error)
                }
                is EstadoPantalla.Contenido -> {
                    val visibles = filtrarRecordatorios(estado.recordatorios, filtroEstado, filtroPrioridad)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Selector(stringResource(R.string.filtro_estado), filtroEstado,
                                listOf("Pendiente", "Completado"), stringResource(R.string.todos), "filtro_estado") {
                                filtroEstado = it
                            }
                            Selector(stringResource(R.string.filtro_prioridad), filtroPrioridad,
                                listOf("Alta", "Media", "Baja"), stringResource(R.string.todas), "filtro_prioridad") {
                                filtroPrioridad = it
                            }
                            Text(stringResource(R.string.categorias,
                                DatosSimulados.categorias.joinToString { it.nombre }),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(stringResource(R.string.cantidad, visibles.size, estado.recordatorios.size),
                                fontWeight = FontWeight.SemiBold, modifier = Modifier.testTag("cantidad"))
                        }
                    }
                    if (filtroEstado != null || filtroPrioridad != null) {
                        item {
                            TextButton(onClick = { filtroEstado = null; filtroPrioridad = null },
                                modifier = Modifier.testTag("restablecer")) {
                                Text(stringResource(R.string.restablecer))
                            }
                        }
                    }
                    if (visibles.isEmpty()) {
                        item { Text(stringResource(R.string.sin_resultados)) }
                    }
                    items(visibles, key = { it.id }) { recordatorio -> TarjetaRecordatorio(recordatorio) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Selector(
    etiqueta: String,
    seleccionado: String?,
    opciones: List<String>,
    todos: String,
    tag: String,
    seleccionar: (String?) -> Unit,
) {
    var abierto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
        OutlinedTextField(
            value = seleccionado ?: todos,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable).testTag(tag),
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            (listOf<String?>(null) + opciones).forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion ?: todos) }, onClick = {
                    seleccionar(opcion)
                    abierto = false
                })
            }
        }
    }
}

@Composable
private fun TarjetaRecordatorio(recordatorio: Recordatorio) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().testTag("recordatorio_${recordatorio.id}"),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(recordatorio.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.categoria,
                DatosSimulados.etiquetasPorCategoria[recordatorio.categoria] ?: recordatorio.categoria.nombre))
            Text(stringResource(R.string.prioridad, recordatorio.prioridad.etiqueta))
            Text(stringResource(R.string.estado, recordatorio.estado.etiqueta),
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text(recordatorio.descripcion ?: stringResource(R.string.sin_descripcion),
                color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VistaPreviaRecordatorios() {
    TemaRecordatorios { ContenidoRecordatorios(prepararEstado(DatosSimulados.recordatorios)) }
}
