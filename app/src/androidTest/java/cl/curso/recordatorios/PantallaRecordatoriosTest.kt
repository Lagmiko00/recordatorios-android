package cl.curso.recordatorios

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

class PantallaRecordatoriosTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun pantallaInicialMuestraDatosYCategoriaSinDuplicados() {
        compose.onNodeWithText("6 de 6 recordatorios").assertIsDisplayed()
        compose.onNodeWithText("Preparar examen").assertIsDisplayed()
        compose.onNodeWithText("Categorías: Estudio, Personal, Trabajo").assertIsDisplayed()
        capturar("01_pantalla_inicial.png")
    }

    @Test fun prioridadAltaMuestraDosPendientes() {
        seleccionar("filtro_prioridad", "Alta")
        compose.onNodeWithText("2 de 6 recordatorios").assertIsDisplayed()
        compose.onNodeWithText("Preparar examen").assertIsDisplayed()
        capturar("04_filtro_alta.png")
    }

    @Test fun combinacionSinResultadosPermiteRestablecer() {
        seleccionar("filtro_estado", "Completado")
        seleccionar("filtro_prioridad", "Alta")
        compose.onNodeWithText("0 de 6 recordatorios").assertIsDisplayed()
        compose.onNodeWithText("No hay recordatorios con estos filtros.").assertIsDisplayed()
        capturar("05_sin_resultados.png")
        compose.onNodeWithTag("restablecer").performClick()
        compose.onNodeWithText("6 de 6 recordatorios").assertIsDisplayed()
    }

    @Test fun estadoCompletadoMuestraDosElementos() {
        seleccionar("filtro_estado", "Completado")
        compose.onNodeWithText("2 de 6 recordatorios").assertIsDisplayed()
        compose.onNodeWithText("Leer apuntes de clase").assertIsDisplayed()
    }

    @Test fun descripcionOpcionalTieneTextoAlternativo() {
        compose.onNodeWithTag("lista").performScrollToNode(
            androidx.compose.ui.test.hasTestTag("recordatorio_2"))
        compose.onNodeWithText("Sin descripción").assertIsDisplayed()
    }

    @Test fun filtrosSeConservanAlRecrearActividad() {
        seleccionar("filtro_prioridad", "Baja")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("2 de 6 recordatorios").assertIsDisplayed()
        compose.onNodeWithTag("filtro_prioridad").assertIsDisplayed()
    }

    private fun seleccionar(tag: String, texto: String) {
        compose.onNodeWithTag(tag).performClick()
        compose.onNodeWithText(texto).performClick()
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
