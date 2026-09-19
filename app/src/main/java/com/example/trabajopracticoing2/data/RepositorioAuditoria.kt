package com.example.trabajopracticoing2.data

import com.example.trabajopracticoing2.model.RegistroAuditoria
import com.example.trabajopracticoing2.model.TipoEvento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Registro de trazabilidad del sistema.
 * Responde a la pregunta: quien hizo que, cuando y sobre que entidad.
 */
object RepositorioAuditoria {

    private val registros = mutableListOf<RegistroAuditoria>()
    private var contador = 0

    private val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun registrar(tipo: TipoEvento, entidad: String, detalle: String) {
        contador += 1
        val usuario = SesionUsuario.usuarioActual
        registros.add(
            0,
            RegistroAuditoria(
                id = "AUD-%04d".format(contador),
                usuario = usuario?.nombre ?: "Sistema",
                rol = usuario?.rol?.etiqueta ?: "Automático",
                fechaHora = formato.format(Date()),
                tipo = tipo,
                entidad = entidad,
                detalle = detalle
            )
        )
    }

    fun listar(): List<RegistroAuditoria> = registros.toList()

    fun listarPorTipo(tipo: TipoEvento?): List<RegistroAuditoria> =
        if (tipo == null) listar() else registros.filter { it.tipo == tipo }

    fun limpiar() {
        registros.clear()
        contador = 0
    }
}
