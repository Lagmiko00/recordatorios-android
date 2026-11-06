package cl.curso.recordatorios

import cl.curso.recordatorios.datos.DatosSimulados
import cl.curso.recordatorios.datos.filtrarRecordatorios
import cl.curso.recordatorios.datos.prepararEstado
import cl.curso.recordatorios.modelo.Categoria
import cl.curso.recordatorios.modelo.EstadoPantalla
import cl.curso.recordatorios.modelo.EstadoRecordatorio
import cl.curso.recordatorios.modelo.Prioridad
import cl.curso.recordatorios.modelo.validarRecordatorio
import org.junit.Assert.*
import org.junit.Test

class RecordatoriosTest {
    @Test fun datosValidosNoProducenErrores() {
        assertTrue(validarRecordatorio(1, "Comprar materiales").isEmpty())
    }

    @Test fun tituloVacioOConEspaciosEsInvalido() {
        listOf("", "   ", "\n\t").forEach {
            assertEquals(listOf("El título es obligatorio."), validarRecordatorio(1, it))
        }
    }

    @Test fun identificadorDebeSerPositivo() {
        listOf(0L, -1L).forEach {
            assertEquals(listOf("El identificador debe ser mayor que cero."), validarRecordatorio(it, "Tarea"))
        }
    }

    @Test fun validacionDevuelveTodosLosProblemas() {
        assertEquals(2, validarRecordatorio(0, " ").size)
    }

    @Test fun modeloNoPermiteTituloInvalido() {
        assertThrows(IllegalArgumentException::class.java) {
            DatosSimulados.recordatorios.first().copy(titulo = " ")
        }
    }

    @Test fun categoriaRequiereIdentificadorYNombre() {
        assertThrows(IllegalArgumentException::class.java) { Categoria("", "Estudio") }
        assertThrows(IllegalArgumentException::class.java) { Categoria("estudio", " ") }
    }

    @Test fun descripcionPuedeEstarAusente() {
        assertNull(DatosSimulados.recordatorios[1].descripcion)
    }

    @Test fun copiarNoModificaElOriginal() {
        val original = DatosSimulados.recordatorios.first()
        val completado = original.copy(estado = EstadoRecordatorio.Completado)
        assertEquals(EstadoRecordatorio.Pendiente, original.estado)
        assertEquals(EstadoRecordatorio.Completado, completado.estado)
        assertNotEquals(original, completado)
    }

    @Test fun setEliminaCategoriasRepetidas() {
        assertEquals(6, DatosSimulados.recordatorios.size)
        assertEquals(3, DatosSimulados.categorias.size)
    }

    @Test fun mapContieneEtiquetaParaCadaCategoria() {
        DatosSimulados.categorias.forEach {
            assertEquals(it.nombre, DatosSimulados.etiquetasPorCategoria[it])
        }
    }

    @Test fun filtrosCombinadosRespetanAmbasCondiciones() {
        val datos = DatosSimulados.recordatorios
        val estados = listOf(null, "Pendiente", "Completado")
        val prioridades = listOf(null, "Alta", "Media", "Baja")
        val cantidades = listOf(6, 2, 2, 2, 4, 2, 1, 1, 2, 0, 1, 1)
        var i = 0
        estados.forEach { estado ->
            prioridades.forEach { prioridad ->
                assertEquals("Estado=$estado prioridad=$prioridad", cantidades[i++],
                    filtrarRecordatorios(datos, estado, prioridad).size)
            }
        }
        assertEquals(6, datos.size)
    }

    @Test fun filtrarListaVaciaEsSeguro() {
        assertTrue(filtrarRecordatorios(emptyList(), "Pendiente", "Alta").isEmpty())
    }

    @Test fun listaVaciaYDatosValidosGeneranEstadosDiferentes() {
        assertEquals(EstadoPantalla.Vacio, prepararEstado(emptyList()))
        assertTrue(prepararEstado(DatosSimulados.recordatorios) is EstadoPantalla.Contenido)
    }

    @Test fun idsDuplicadosGeneranError() {
        val registro = DatosSimulados.recordatorios.first()
        assertTrue(prepararEstado(listOf(registro, registro)) is EstadoPantalla.Error)
    }
}
