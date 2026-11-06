package cl.curso.recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.curso.recordatorios.ui.PantallaRecordatorios
import cl.curso.recordatorios.ui.TemaRecordatorios

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaRecordatorios { PantallaRecordatorios() }
        }
    }
}
