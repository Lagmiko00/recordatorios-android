package cl.curso.recordatorios.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.curso.recordatorios.datos.RepositorioRecordatorios
import cl.curso.recordatorios.presentacion.CreacionViewModel
import cl.curso.recordatorios.presentacion.DetalleViewModel
import cl.curso.recordatorios.presentacion.Envio
import cl.curso.recordatorios.presentacion.ListaViewModel

private const val LISTA = "lista"
private const val CREAR = "crear"
private const val DETALLE = "detalle/{id}"

@Composable
fun NavegacionRecordatorios(repositorio: RepositorioRecordatorios) {
    val nav = rememberNavController()
    val lista: ListaViewModel = viewModel { ListaViewModel(repositorio) }

    NavHost(navController = nav, startDestination = LISTA) {
        composable(LISTA) {
            val estado by lista.uiState.collectAsStateWithLifecycle()
            ListaRecordatorios(
                estado,
                onCrearRecordatorio = {
                    if (nav.currentDestination?.route == LISTA) nav.navigate(CREAR)
                },
                onRecordatorioClick = { id ->
                    if (nav.currentDestination?.route == LISTA) nav.navigate("detalle/$id")
                },
                onReintentar = lista::cargar,
                onEstado = lista::cambiarEstado,
                onPrioridad = lista::cambiarPrioridad,
                onLimpiar = lista::limpiarFiltros,
            )
        }
        composable(CREAR) {
            val crear: CreacionViewModel = viewModel { CreacionViewModel(repositorio) }
            val estado by crear.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(estado.envio) {
                if (estado.envio is Envio.Guardado && nav.currentDestination?.route == CREAR) {
                    lista.limpiarFiltros()
                    lista.cargar()
                    nav.popBackStack()
                }
            }
            CrearRecordatorio(
                estado, crear.categorias, crear.prioridades,
                onTitulo = crear::cambiarTitulo,
                onDescripcion = crear::cambiarDescripcion,
                onCategoria = crear::cambiarCategoria,
                onPrioridad = crear::cambiarPrioridad,
                onGuardar = crear::guardar,
                onCancelar = { if (nav.currentDestination?.route == CREAR) nav.popBackStack() },
            )
        }
        composable(DETALLE, arguments = listOf(navArgument("id") { type = NavType.LongType })) { entrada ->
            val id = requireNotNull(entrada.arguments).getLong("id")
            val detalle: DetalleViewModel = viewModel { DetalleViewModel(repositorio, id) }
            val estado by detalle.uiState.collectAsStateWithLifecycle()
            DetalleRecordatorio(
                estado,
                onVolver = { if (nav.currentDestination?.route == DETALLE) nav.popBackStack() },
                onReintentar = detalle::cargar,
            )
        }
    }
}
