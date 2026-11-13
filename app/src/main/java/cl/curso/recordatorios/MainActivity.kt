package cl.curso.recordatorios

import android.os.Bundle
import android.content.pm.ApplicationInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.curso.recordatorios.datos.Escenario
import cl.curso.recordatorios.datos.RepositorioEnMemoria
import cl.curso.recordatorios.datos.RepositorioRecordatorios
import cl.curso.recordatorios.ui.NavegacionRecordatorios
import cl.curso.recordatorios.ui.TemaRecordatorios

class SesionRecordatorios(val repositorio: RepositorioRecordatorios) : ViewModel()

class MainActivity : ComponentActivity() {
    private val sesion by viewModels<SesionRecordatorios> {
        viewModelFactory {
            initializer { SesionRecordatorios(RepositorioEnMemoria(escenario())) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaRecordatorios { NavegacionRecordatorios(sesion.repositorio) }
        }
    }

    private fun escenario(): Escenario {
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE == 0) return Escenario.NORMAL
        return when (intent.getStringExtra("escenario")) {
            "vacio" -> Escenario.VACIO
            "error_carga" -> Escenario.ERROR_CARGA
            "error_guardado" -> Escenario.ERROR_GUARDADO
            else -> Escenario.NORMAL
        }
    }
}
