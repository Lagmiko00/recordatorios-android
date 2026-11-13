package cl.curso.recordatorios.datos

import cl.curso.recordatorios.modelo.Categoria
import cl.curso.recordatorios.modelo.EstadoRecordatorio
import cl.curso.recordatorios.modelo.Prioridad
import cl.curso.recordatorios.modelo.Recordatorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface RepositorioRecordatorios {
    suspend fun obtener(): List<Recordatorio>
    suspend fun buscar(id: Long): Recordatorio?
    suspend fun guardar(titulo: String, descripcion: String?, categoria: Categoria, prioridad: Prioridad): Recordatorio
}

enum class Escenario { NORMAL, VACIO, ERROR_CARGA, ERROR_GUARDADO }

class RepositorioEnMemoria(escenario: Escenario) : RepositorioRecordatorios {
    private val mutex = Mutex()
    private var registros = if (escenario == Escenario.VACIO) emptyList() else DatosSimulados.recordatorios
    private var fallaCarga = escenario == Escenario.ERROR_CARGA
    private var fallaGuardado = escenario == Escenario.ERROR_GUARDADO

    override suspend fun obtener(): List<Recordatorio> {
        delay(650)
        return mutex.withLock {
            if (fallaCarga) {
                fallaCarga = false
                throw IllegalStateException("No se pudo cargar la lista de recordatorios.")
            }
            registros
        }
    }

    override suspend fun buscar(id: Long): Recordatorio? {
        delay(250)
        return mutex.withLock { registros.firstOrNull { it.id == id } }
    }

    override suspend fun guardar(
        titulo: String,
        descripcion: String?,
        categoria: Categoria,
        prioridad: Prioridad,
    ): Recordatorio {
        delay(950)
        return mutex.withLock {
            if (fallaGuardado) {
                fallaGuardado = false
                throw IllegalStateException("No se pudo guardar el recordatorio.")
            }
            val id = (registros.maxOfOrNull { it.id } ?: 0) + 1
            val nuevo = Recordatorio(id, titulo, categoria, prioridad, EstadoRecordatorio.Pendiente, descripcion)
            registros = listOf(nuevo) + registros
            nuevo
        }
    }
}
