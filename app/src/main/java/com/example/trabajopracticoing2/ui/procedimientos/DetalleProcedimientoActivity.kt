package com.example.trabajopracticoing2.ui.procedimientos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.model.Procedimiento
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.LinearProgressIndicator

/**
 * Checklist de verificacion de un procedimiento.
 * El porcentaje de cumplimiento se recalcula en vivo a medida que se marcan
 * los puntos de control, dando retroalimentacion inmediata al usuario.
 */
class DetalleProcedimientoActivity : ActividadBase() {

    private lateinit var procedimiento: Procedimiento
    private lateinit var contenedorPuntos: LinearLayout
    private lateinit var barra: LinearProgressIndicator
    private lateinit var porcentaje: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_procedimiento)
        prepararRepositorio()

        val id = intent.getStringExtra(EXTRA_ID).orEmpty()
        val encontrado = RepositorioSeguridad.procedimiento(id)
        if (encontrado == null) {
            finish()
            return
        }
        procedimiento = encontrado

        contenedorPuntos = findViewById(R.id.contenedorPuntos)
        barra = findViewById(R.id.barraProcDet)
        porcentaje = findViewById(R.id.textoProcDetPorcentaje)

        configurarBarra(getString(R.string.procedimiento_detalle), procedimiento.codigo)
        pintarCabecera()
        pintarPuntos()
        pintarCumplimiento()

        findViewById<MaterialButton>(R.id.botonRegistrarVerificacion).setOnClickListener { boton ->
            if (!SesionUsuario.puede(Permiso.REGISTRAR_VERIFICACION)) {
                Formato.aviso(boton, R.string.sin_permiso)
                return@setOnClickListener
            }
            RepositorioSeguridad.registrarVerificacion(procedimiento.id)
            Formato.aviso(boton, R.string.procedimiento_verificado)
        }
    }

    private fun pintarCabecera() {
        findViewById<TextView>(R.id.textoProcDetCodigo).text = procedimiento.codigo
        findViewById<TextView>(R.id.textoProcDetNombre).text = procedimiento.nombre
        findViewById<TextView>(R.id.textoProcDetDescripcion).text = procedimiento.descripcion
        findViewById<TextView>(R.id.textoProcDetSector).text = getString(
            R.string.detalle_sector,
            RepositorioSeguridad.nombreSector(procedimiento.sectorId)
        )
        findViewById<TextView>(R.id.textoProcDetUltima).text =
            getString(R.string.detalle_ultima_verificacion, procedimiento.ultimaVerificacion)
    }

    private fun pintarPuntos() {
        contenedorPuntos.removeAllViews()
        val inflador = LayoutInflater.from(this)
        val habilitado = SesionUsuario.puede(Permiso.REGISTRAR_VERIFICACION)

        procedimiento.puntos.forEachIndexed { indice, punto ->
            val fila: View = inflador.inflate(R.layout.item_punto_control, contenedorPuntos, false)
            fila.findViewById<TextView>(R.id.textoPunto).text = punto.descripcion

            val check = fila.findViewById<MaterialCheckBox>(R.id.checkPunto)
            check.setOnCheckedChangeListener(null)
            check.isChecked = punto.cumplido
            check.isEnabled = habilitado
            check.setOnCheckedChangeListener { _, marcado ->
                RepositorioSeguridad.actualizarPuntoControl(procedimiento.id, indice, marcado)
                pintarCumplimiento()
            }
            contenedorPuntos.addView(fila)
        }
    }

    private fun pintarCumplimiento() {
        val valor = procedimiento.cumplimiento
        barra.setProgressCompat(valor, true)
        barra.setIndicatorColor(ContextCompat.getColor(this, procedimiento.estado.colorRes))
        porcentaje.text = getString(R.string.porcentaje, valor)
    }

    companion object {
        private const val EXTRA_ID = "procedimiento_id"

        fun intent(contexto: Context, id: String): Intent =
            Intent(contexto, DetalleProcedimientoActivity::class.java).putExtra(EXTRA_ID, id)
    }
}
