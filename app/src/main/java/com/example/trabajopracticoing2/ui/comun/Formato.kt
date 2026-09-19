package com.example.trabajopracticoing2.ui.comun

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.dominio.Motivo
import com.example.trabajopracticoing2.model.EstadoOperativo
import com.example.trabajopracticoing2.model.Severidad
import com.google.android.material.snackbar.Snackbar

/**
 * Utilidades de presentacion compartidas por todas las pantallas.
 * Centralizan el codigo de color del semaforo para mantener consistencia visual.
 */
object Formato {

    /** Pinta una etiqueta (badge) con el color correspondiente a la severidad. */
    fun aplicarSeveridad(etiqueta: TextView, severidad: Severidad) {
        val contexto = etiqueta.context
        etiqueta.text = severidad.etiqueta
        etiqueta.setTextColor(ContextCompat.getColor(contexto, severidad.colorRes))
        etiqueta.setBackgroundResource(R.drawable.bg_badge)
        etiqueta.backgroundTintList =
            ContextCompat.getColorStateList(contexto, severidad.colorFondoRes)
    }

    /** Pinta una etiqueta con el color del estado operativo. */
    fun aplicarEstado(etiqueta: TextView, estado: EstadoOperativo) {
        val contexto = etiqueta.context
        etiqueta.text = estado.etiqueta
        etiqueta.setTextColor(ContextCompat.getColor(contexto, estado.colorRes))
        etiqueta.setBackgroundResource(R.drawable.bg_badge)
        etiqueta.backgroundTintList =
            ContextCompat.getColorStateList(contexto, estado.colorFondoRes)
    }

    /** Pinta una etiqueta con un par de colores arbitrario (estados de incidente). */
    fun aplicarColores(etiqueta: TextView, texto: String, colorRes: Int, colorFondoRes: Int) {
        val contexto = etiqueta.context
        etiqueta.text = texto
        etiqueta.setTextColor(ContextCompat.getColor(contexto, colorRes))
        etiqueta.setBackgroundResource(R.drawable.bg_badge)
        etiqueta.backgroundTintList =
            ContextCompat.getColorStateList(contexto, colorFondoRes)
    }

    /** Aplica color de fondo a la franja lateral de una tarjeta. */
    fun aplicarFranja(vista: View, colorRes: Int) {
        vista.setBackgroundResource(R.drawable.bg_franja)
        vista.backgroundTintList =
            ContextCompat.getColorStateList(vista.context, colorRes)
    }

    fun colorDeSeveridad(contexto: Context, severidad: Severidad): Int =
        ContextCompat.getColor(contexto, severidad.colorRes)

    /** Resuelve un color del tema. Evita repetir ContextCompat en los adaptadores. */
    fun colorDeTexto(contexto: Context, colorRes: Int): Int =
        ContextCompat.getColor(contexto, colorRes)

    /** Traduce un motivo de validacion al texto que ve el usuario. */
    fun mensajeDeMotivo(contexto: Context, motivo: Motivo): String = when (motivo) {
        Motivo.OBLIGATORIO -> contexto.getString(R.string.form_obligatorio)
        Motivo.DEMASIADO_CORTO -> contexto.getString(R.string.form_titulo_corto)
        Motivo.NUMERO_INVALIDO -> contexto.getString(R.string.form_numero_invalido)
        Motivo.SIN_SELECCION -> contexto.getString(R.string.form_seleccione)
        Motivo.LEGAJO_INVALIDO -> contexto.getString(R.string.login_error_legajo)
        Motivo.CLAVE_INVALIDA -> contexto.getString(R.string.login_error_clave)
    }

    fun aviso(vista: View, mensaje: String) {
        Snackbar.make(vista, mensaje, Snackbar.LENGTH_LONG).show()
    }

    fun aviso(vista: View, mensajeRes: Int) {
        Snackbar.make(vista, mensajeRes, Snackbar.LENGTH_LONG).show()
    }
}
