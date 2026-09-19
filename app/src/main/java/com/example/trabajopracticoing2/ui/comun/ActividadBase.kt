package com.example.trabajopracticoing2.ui.comun

import androidx.appcompat.app.AppCompatActivity
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.google.android.material.appbar.MaterialToolbar

/**
 * Actividad base de las pantallas secundarias del prototipo.
 *
 * Centraliza dos responsabilidades repetidas en todas las pantallas de detalle:
 * la configuracion de la barra superior (titulo, subtitulo y accion de volver)
 * y la inicializacion perezosa del repositorio en memoria.
 *
 * Responde a la regla de consistencia de interfaz: todas las pantallas
 * secundarias se ven y se comportan igual.
 */
abstract class ActividadBase : AppCompatActivity() {

    protected fun prepararRepositorio() {
        RepositorioSeguridad.inicializarSiHaceFalta()
    }

    /**
     * Configura la barra superior incluida desde `view_barra.xml`.
     * La flecha de navegacion siempre vuelve a la pantalla anterior.
     */
    protected fun configurarBarra(titulo: String, subtitulo: String? = null) {
        val barra = findViewById<MaterialToolbar>(R.id.barraSuperior)
        barra.title = titulo
        barra.subtitle = subtitulo
        barra.setNavigationOnClickListener { finish() }
    }

    protected fun configurarBarra(tituloRes: Int, subtitulo: String? = null) {
        configurarBarra(getString(tituloRes), subtitulo)
    }
}
