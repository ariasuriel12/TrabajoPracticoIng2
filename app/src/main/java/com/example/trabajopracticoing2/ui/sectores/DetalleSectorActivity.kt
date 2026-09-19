package com.example.trabajopracticoing2.ui.sectores

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.Sector
import com.example.trabajopracticoing2.ui.alertas.AlertaAdapter
import com.example.trabajopracticoing2.ui.alertas.DetalleAlertaActivity
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato
import com.example.trabajopracticoing2.ui.incidentes.DetalleIncidenteActivity
import com.example.trabajopracticoing2.ui.incidentes.IncidenteAdapter

/**
 * Detalle de un sector: estado actual, alertas del sector y antecedentes
 * de incidentes. Cubre la necesidad de "consultar antecedentes".
 */
class DetalleSectorActivity : ActividadBase() {

    private lateinit var sector: Sector
    private lateinit var adaptadorAlertas: AlertaAdapter
    private lateinit var adaptadorIncidentes: IncidenteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_sector)
        prepararRepositorio()

        val id = intent.getStringExtra(EXTRA_ID).orEmpty()
        val encontrado = RepositorioSeguridad.sector(id)
        if (encontrado == null) {
            finish()
            return
        }
        sector = encontrado

        configurarBarra(getString(R.string.sector_detalle), sector.nombre)
        configurarListas()
    }

    override fun onResume() {
        super.onResume()
        pintar()
    }

    private fun configurarListas() {
        val listaAlertas = findViewById<RecyclerView>(R.id.listaSectorAlertas)
        adaptadorAlertas = AlertaAdapter(emptyList()) { alerta ->
            startActivity(DetalleAlertaActivity.intent(this, alerta.id))
        }
        listaAlertas.layoutManager = LinearLayoutManager(this)
        listaAlertas.adapter = adaptadorAlertas

        val listaIncidentes = findViewById<RecyclerView>(R.id.listaSectorIncidentes)
        adaptadorIncidentes = IncidenteAdapter(emptyList()) { incidente ->
            startActivity(DetalleIncidenteActivity.intent(this, incidente.id))
        }
        listaIncidentes.layoutManager = LinearLayoutManager(this)
        listaIncidentes.adapter = adaptadorIncidentes
    }

    private fun pintar() {
        Formato.aplicarEstado(
            findViewById(R.id.etiquetaSectorDetEstado),
            RepositorioSeguridad.estadoDeSector(sector.id)
        )
        findViewById<TextView>(R.id.textoSectorDetNombre).text = sector.nombre
        findViewById<TextView>(R.id.textoSectorDetResponsable).text =
            getString(R.string.detalle_responsable, sector.responsable)
        findViewById<TextView>(R.id.textoSectorDetRiesgo).text =
            getString(R.string.sector_riesgo, sector.riesgoPrincipal)

        val alertas = RepositorioSeguridad.alertasDeSector(sector.id)
        adaptadorAlertas.actualizar(alertas)
        findViewById<View>(R.id.textoSectorSinAlertas).visibility =
            if (alertas.isEmpty()) View.VISIBLE else View.GONE

        val incidentes = RepositorioSeguridad.incidentesDeSector(sector.id)
        adaptadorIncidentes.actualizar(incidentes)
        findViewById<View>(R.id.textoSectorSinIncidentes).visibility =
            if (incidentes.isEmpty()) View.VISIBLE else View.GONE
    }

    companion object {
        private const val EXTRA_ID = "sector_id"

        fun intent(contexto: Context, id: String): Intent =
            Intent(contexto, DetalleSectorActivity::class.java).putExtra(EXTRA_ID, id)
    }
}
