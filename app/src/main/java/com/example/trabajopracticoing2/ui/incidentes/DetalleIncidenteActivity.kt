package com.example.trabajopracticoing2.ui.incidentes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.AccionCorrectiva
import com.example.trabajopracticoing2.model.EstadoAccion
import com.example.trabajopracticoing2.model.EstadoIncidente
import com.example.trabajopracticoing2.model.Incidente
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox

/**
 * Detalle de un incidente y seguimiento de sus acciones correctivas.
 *
 * Implementa la regla de negocio relevada: un incidente no puede cerrarse
 * mientras existan acciones correctivas pendientes. El boton de cierre se
 * deshabilita y se explica el motivo (prevencion de errores).
 */
class DetalleIncidenteActivity : ActividadBase() {

    private lateinit var incidente: Incidente
    private lateinit var contenedorAcciones: LinearLayout
    private lateinit var textoSinAcciones: TextView
    private lateinit var botonCerrar: MaterialButton
    private lateinit var avisoCierre: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_incidente)
        prepararRepositorio()

        val id = intent.getStringExtra(EXTRA_ID).orEmpty()
        val encontrado = RepositorioSeguridad.incidente(id)
        if (encontrado == null) {
            finish()
            return
        }
        incidente = encontrado

        contenedorAcciones = findViewById(R.id.contenedorAcciones)
        textoSinAcciones = findViewById(R.id.textoSinAcciones)
        botonCerrar = findViewById(R.id.botonCerrarIncidente)
        avisoCierre = findViewById(R.id.textoAvisoCierre)

        configurarBarra(getString(R.string.incidente_detalle), incidente.id)
        configurarAcciones()
    }

    override fun onResume() {
        super.onResume()
        // Vuelve a leer del repositorio por si se agrego una accion correctiva.
        RepositorioSeguridad.incidente(incidente.id)?.let { incidente = it }
        pintar()
    }

    private fun pintar() {
        findViewById<TextView>(R.id.textoIncDetId).text = incidente.id
        Formato.aplicarColores(
            findViewById(R.id.etiquetaIncDetEstado),
            incidente.estado.etiqueta,
            incidente.estado.colorRes,
            incidente.estado.colorFondoRes
        )
        findViewById<TextView>(R.id.textoIncDetTitulo).text = incidente.titulo
        Formato.aplicarSeveridad(findViewById(R.id.etiquetaIncDetSeveridad), incidente.severidad)
        findViewById<TextView>(R.id.textoIncDetDescripcion).text = incidente.descripcion
        findViewById<TextView>(R.id.textoIncDetSector).text = getString(
            R.string.detalle_sector,
            RepositorioSeguridad.nombreSector(incidente.sectorId)
        )
        findViewById<TextView>(R.id.textoIncDetFecha).text =
            getString(R.string.detalle_fecha, incidente.fecha)
        findViewById<TextView>(R.id.textoIncDetReportado).text =
            getString(R.string.detalle_reportado, incidente.reportadoPor)
        findViewById<TextView>(R.id.textoIncDetResponsable).text =
            getString(R.string.detalle_responsable, incidente.responsable)
        findViewById<TextView>(R.id.textoIncDetPersonas).text =
            getString(R.string.detalle_personas, incidente.personasAfectadas)

        pintarAcciones()
        pintarEstadoDeCierre()
    }

    private fun pintarAcciones() {
        contenedorAcciones.removeAllViews()
        if (incidente.acciones.isEmpty()) {
            textoSinAcciones.visibility = View.VISIBLE
            return
        }
        textoSinAcciones.visibility = View.GONE

        val inflador = LayoutInflater.from(this)
        incidente.acciones.forEach { accion ->
            val fila = inflador.inflate(R.layout.item_accion, contenedorAcciones, false)
            enlazarAccion(fila, accion)
            contenedorAcciones.addView(fila)
        }
    }

    private fun enlazarAccion(fila: View, accion: AccionCorrectiva) {
        fila.findViewById<TextView>(R.id.textoAccionDescripcion).text = accion.descripcion
        fila.findViewById<TextView>(R.id.textoAccionResponsable).text =
            getString(R.string.detalle_responsable, accion.responsable)

        val etiqueta = fila.findViewById<TextView>(R.id.etiquetaAccionEstado)
        val colores = when (accion.estado) {
            EstadoAccion.COMPLETADA -> R.color.estado_normal to R.color.estado_normal_suave
            EstadoAccion.EN_CURSO -> R.color.estado_precaucion to R.color.estado_precaucion_suave
            EstadoAccion.PENDIENTE -> R.color.estado_alto to R.color.estado_alto_suave
        }
        Formato.aplicarColores(etiqueta, accion.estado.etiqueta, colores.first, colores.second)

        val check = fila.findViewById<MaterialCheckBox>(R.id.checkAccion)
        val completada = accion.estado == EstadoAccion.COMPLETADA
        check.setOnCheckedChangeListener(null)
        check.isChecked = completada
        check.isEnabled = !completada && incidente.estado != EstadoIncidente.CERRADO
        check.setOnCheckedChangeListener { vista, marcada ->
            if (!marcada) return@setOnCheckedChangeListener
            if (!SesionUsuario.puede(Permiso.CERRAR_INCIDENTE)) {
                Formato.aviso(vista, R.string.sin_permiso)
                pintar()
                return@setOnCheckedChangeListener
            }
            RepositorioSeguridad.completarAccion(incidente.id, accion.id)
            Formato.aviso(vista, R.string.accion_completada)
            pintar()
        }
    }

    /** Explica por que el cierre esta o no disponible. */
    private fun pintarEstadoDeCierre() {
        val cerrado = incidente.estado == EstadoIncidente.CERRADO
        val puedeCerrar = RepositorioSeguridad.puedeCerrarIncidente(incidente.id)

        botonCerrar.isEnabled = puedeCerrar
        avisoCierre.visibility = if (!cerrado && !puedeCerrar) View.VISIBLE else View.GONE
        avisoCierre.setText(R.string.incidente_no_cerrable)

        if (cerrado) {
            botonCerrar.setText(R.string.incidente_ya_cerrado)
        } else {
            botonCerrar.setText(R.string.incidente_cerrar)
        }
    }

    private fun configurarAcciones() {
        findViewById<MaterialButton>(R.id.botonAgregarAccion).setOnClickListener {
            startActivity(NuevaAccionActivity.intent(this, incidente.id))
        }
        botonCerrar.setOnClickListener { boton ->
            if (!SesionUsuario.puede(Permiso.CERRAR_INCIDENTE)) {
                Formato.aviso(boton, R.string.sin_permiso)
                return@setOnClickListener
            }
            if (RepositorioSeguridad.cerrarIncidente(incidente.id)) {
                Formato.aviso(boton, R.string.incidente_cerrado)
                pintar()
            } else {
                Formato.aviso(boton, R.string.incidente_no_cerrable)
            }
        }
    }

    companion object {
        private const val EXTRA_ID = "incidente_id"

        fun intent(contexto: Context, id: String): Intent =
            Intent(contexto, DetalleIncidenteActivity::class.java).putExtra(EXTRA_ID, id)
    }
}
