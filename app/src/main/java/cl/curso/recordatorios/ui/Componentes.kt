package cl.curso.recordatorios.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcoPantalla(
    titulo: String,
    modifier: Modifier = Modifier,
    onVolver: (() -> Unit)? = null,
    volverHabilitado: Boolean = true,
    accion: @Composable () -> Unit = {},
    contenido: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(titulo, Modifier.semantics { heading() }) },
                navigationIcon = {
                    if (onVolver != null) TextButton(onClick = onVolver, enabled = volverHabilitado) { Text("Volver") }
                },
            )
        },
        floatingActionButton = accion,
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.TopCenter) {
            contenido(Modifier.widthIn(max = 680.dp).fillMaxWidth())
        }
    }
}

@Composable
fun MensajeEstado(
    mensaje: String,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    etiquetaAccion: String? = null,
    onAccion: (() -> Unit)? = null,
) {
    Column(
        modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        if (cargando) CircularProgressIndicator()
        Text(mensaje, style = MaterialTheme.typography.bodyLarge)
        if (etiquetaAccion != null && onAccion != null) {
            Button(onClick = onAccion) { Text(etiquetaAccion) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Selector(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    ayuda: String? = null,
    mensajeError: String? = null,
) {
    var abierto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = abierto,
        onExpandedChange = { if (habilitado) abierto = it },
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            enabled = habilitado,
            isError = mensajeError != null,
            singleLine = true,
            label = { Text(etiqueta) },
            supportingText = (mensajeError ?: ayuda)?.let { texto -> { Text(texto) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            modifier = modifier.fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = habilitado)
                .errorDeCampo(mensajeError),
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) }, onClick = {
                    abierto = false
                    onSeleccionar(opcion)
                })
            }
        }
    }
}

@Composable
fun CampoRecordatorio(
    etiqueta: String,
    valor: String,
    onCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    unaLinea: Boolean = true,
    ayuda: String,
    mensajeError: String?,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambiar,
        label = { Text(etiqueta) },
        enabled = habilitado,
        singleLine = unaLinea,
        minLines = if (unaLinea) 1 else 3,
        isError = mensajeError != null,
        supportingText = { Text(mensajeError ?: ayuda) },
        modifier = modifier.fillMaxWidth().errorDeCampo(mensajeError),
    )
}

private fun Modifier.errorDeCampo(mensaje: String?): Modifier =
    if (mensaje == null) this else semantics { error(mensaje) }
