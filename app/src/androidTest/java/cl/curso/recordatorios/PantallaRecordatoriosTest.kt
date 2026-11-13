package cl.curso.recordatorios

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.io.File

class PantallaRecordatoriosTest {
    @get:Rule val compose = createEmptyComposeRule()
    private lateinit var activity: ActivityScenario<MainActivity>

    @After fun cerrar() {
        if (::activity.isInitialized) activity.close()
    }

    @Test fun listadoDetalleYRegreso() {
        abrir()
        esperar("6 de 6 recordatorios")
        capturar("01_listado.png")
        compose.onNodeWithTag("recordatorio_1").performClick()
        esperar("Repasar los temas de la próxima evaluación.")
        compose.onNodeWithText("Recordatorio n.º 1").assertIsDisplayed()
        capturar("03_detalle.png")
        compose.onNodeWithText("Volver").performClick()
        esperar("6 de 6 recordatorios")
    }

    @Test fun validarFormularioGuardarYConsultarNuevoRegistro() {
        abrir()
        formulario()
        compose.onNodeWithTag("guardar").assertIsNotEnabled()
        compose.onNodeWithTag("titulo").performTextInput("  abc  ")
        compose.onNodeWithText("Escribe al menos 4 caracteres.").assertExists()
        compose.onNodeWithTag("titulo").performTextReplacement("  Revisar calendario  ")
        compose.onNodeWithTag("descripcion").performTextInput("a".repeat(201))
        compose.onNodeWithText("La descripción admite hasta 200 caracteres.").assertExists()
        compose.onNodeWithTag("guardar").assertIsNotEnabled()
        compose.onNodeWithTag("descripcion").performTextReplacement("Confirmar las fechas de las reuniones.")
        closeSoftKeyboard()
        compose.onNodeWithTag("guardar").assertIsNotEnabled()
        elegir("categoria", "Trabajo")
        compose.onNodeWithTag("guardar").assertIsNotEnabled()
        elegir("prioridad", "Media")
        compose.onNodeWithTag("guardar").assertIsEnabled()
        capturar("02_formulario.png")
        compose.onNodeWithTag("guardar").performClick()
        compose.onNodeWithTag("guardar").assertIsNotEnabled()
        compose.onNodeWithText("Guardando…").assertIsDisplayed()
        capturar("08_guardando.png")
        esperar("7 de 7 recordatorios")
        compose.onNodeWithTag("recordatorio_7").performClick()
        esperar("Confirmar las fechas de las reuniones.")
        compose.onNodeWithText("Revisar calendario").assertIsDisplayed()
    }

    @Test fun cancelarNoCreaUnRegistro() {
        abrir()
        formulario()
        compose.onNodeWithTag("titulo").performTextInput("No guardar esta tarea")
        closeSoftKeyboard()
        compose.onNodeWithText("Cancelar").performScrollTo().performClick()
        esperar("6 de 6 recordatorios")
        compose.onNodeWithText("No guardar esta tarea").assertDoesNotExist()
    }

    @Test fun estadoVacioOfreceCrear() {
        abrir("vacio")
        esperar("Todavía no hay recordatorios.")
        capturar("04_vacio.png")
        compose.onNodeWithText("Crear recordatorio").performClick()
        compose.onNodeWithTag("titulo").assertIsDisplayed()
    }

    @Test fun errorDeCargaYReintento() {
        abrir("error_carga")
        esperar("No se pudo cargar la lista de recordatorios.")
        capturar("05_error_carga.png")
        compose.onNodeWithText("Reintentar").performClick()
        compose.onNodeWithText("Cargando recordatorios…").assertExists()
        esperar("6 de 6 recordatorios")
    }

    @Test fun errorDeGuardadoConservaElFormularioYPermiteReintentar() {
        abrir("error_guardado")
        formulario()
        compose.onNodeWithTag("titulo").performTextInput("Revisar presupuesto")
        closeSoftKeyboard()
        elegir("categoria", "Trabajo")
        elegir("prioridad", "Alta")
        compose.onNodeWithTag("guardar").performScrollTo().performClick()
        esperar("No se pudo guardar el recordatorio. Inténtalo otra vez.")
        compose.onNodeWithText("Revisar presupuesto").assertExists()
        compose.onNodeWithTag("guardar").assertIsEnabled()
        capturar("07_error_guardado.png")
        compose.onNodeWithTag("guardar").performScrollTo().performClick()
        esperar("7 de 7 recordatorios")
    }

    @Test fun filtrosYFormularioSobrevivenAlRecrearActividad() {
        abrir()
        esperar("6 de 6 recordatorios")
        elegir("filtro_prioridad", "Alta")
        esperar("2 de 6 recordatorios")
        activity.recreate()
        esperar("2 de 6 recordatorios")
        formulario()
        compose.onNodeWithTag("titulo").performTextInput("Consultar apuntes")
        closeSoftKeyboard()
        activity.recreate()
        compose.onNodeWithText("Consultar apuntes").assertExists()
        compose.onNodeWithTag("guardar").assertIsNotEnabled()
    }

    private fun abrir(escenario: String = "normal") {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        activity = ActivityScenario.launch(Intent(context, MainActivity::class.java).putExtra("escenario", escenario))
    }

    private fun formulario() {
        esperar("Nuevo recordatorio")
        compose.onNodeWithText("Nuevo recordatorio").performClick()
        compose.onNodeWithTag("titulo").assertIsDisplayed()
    }

    private fun elegir(tag: String, opcion: String) {
        compose.onNodeWithTag(tag).performScrollTo().performClick()
        compose.onNodeWithText(opcion).performClick()
    }

    private fun esperar(texto: String) {
        compose.waitUntil(6_000) { compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }

    private fun capturar(nombre: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        val destino = File(instrumentation.targetContext.getExternalFilesDir(null), nombre)
        destino.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}
