package com.example.trabajopracticoing2.ui.alertas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.Alerta
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato
import com.example.trabajopracticoing2.ui.incidentes.NuevoIncidenteActivity
import com.example.trabajopracticoing2.ui.sectores.DetalleSectorActivity
import com.google.android.material.button.MaterialButton

/**
 * Detalle de una alerta.
 *
 * Presenta la informacion necesaria para decidir y ofrece las tres acciones
 * posibles: reconocer, escalar a incidente o consultar el sector afectado.
 */
class DetalleAlertaActivity : ActividadBase() {

    private lateinit var alerta: Alerta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_alerta)
        prepararRepositorio()

        val id = intent.getStringExtra(EXTRA_ID).orEmpty()
        val encontrada = RepositorioSeguridad.alerta(id)
        if (encontrada == null) {
            finish()
            return
        }
        alerta = encontrada

        configurarBarra(getString(R.string.alerta_detalle), alerta.id)
        pintar()
        configurarAcciones()
    }

    private fun pintar() {
        val aviso = findViewById<TextView>(R.id.avisoRespuestaInmediata)
        if (alerta.requiereRespuestaInmediata && !alerta.reconocida) {
            aviso.visibility = View.VISIBLE
            aviso.text = getString(R.string.alerta_respuesta_inmediata)
        } else {
            aviso.visibility = View.GONE
        }

        Formato.aplicarSeveridad(findViewById(R.id.etiquetaDetalleSeveridad), alerta.severidad)
        findViewById<TextView>(R.id.textoDetalleTitulo).text = alerta.titulo
        findViewById<TextView>(R.id.textoDetalleDescripcion).text = alerta.descripcion
        findViewById<TextView>(R.id.textoDetalleSector).text =
            getString(R.string.detalle_sector, RepositorioSeguridad.nombreSector(alerta.sectorId))
        findViewById<TextView>(R.id.textoDetalleTipo).text =
            getString(R.string.detalle_tipo, alerta.tipo.etiqueta)
        findViewById<TextView>(R.id.textoDetalleTiempo).text =
            getString(R.string.detalle_detectada, alerta.hace)

        val estado = findViewById<TextView>(R.id.textoDetalleEstado)
        val botonReconocer = findViewById<MaterialButton>(R.id.botonReconocer)
        if (alerta.reconocida) {
            estado.text = getString(R.string.alerta_estado_reconocida, alerta.reconocidaPor ?: "-")
            botonReconocer.isEnabled = false
            botonReconocer.setText(R.string.alerta_ya_reconocida)
        } else {
            estado.text = getString(R.string.alerta_estado_sin_reconocer)
            botonReconocer.isEnabled = true
            botonReconocer.setText(R.string.alerta_reconocer)
        }
    }

    private fun configurarAcciones() {
        findViewById<MaterialButton>(R.id.botonReconocer).setOnClickListener { boton ->
            if (!SesionUsuario.puede(Permiso.RECONOCER_ALERTA)) {
                Formato.aviso(boton, R.string.sin_permiso)
                return@setOnClickListener
            }
            if (RepositorioSeguridad.reconocerAlerta(alerta.id)) {
                Formato.aviso(boton, R.string.alerta_reconocida)
                pintar()
            } else {
                Formato.aviso(boton, R.string.alerta_ya_reconocida)
            }
        }

        findViewById<MaterialButton>(R.id.botonGenerarIncidente).setOnClickListener {
            startActivity(
                NuevoIncidenteActivity.intentDesdeAlerta(
                    this,
                    alerta.titulo,
                    alerta.descripcion,
                    alerta.sectorId,
                    alerta.severidad.name
                )
            )
        }

        findViewById<MaterialButton>(R.id.botonVerSector).setOnClickListener {
            startActivity(DetalleSectorActivity.intent(this, alerta.sectorId))
        }
    }

    companion object {
        private const val EXTRA_ID = "alerta_id"

        fun intent(contexto: Context, id: String): Intent =
            Intent(contexto, DetalleAlertaActivity::class.java).putExtra(EXTRA_ID, id)
    }
}
