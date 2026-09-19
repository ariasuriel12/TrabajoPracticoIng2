package com.example.trabajopracticoing2.ui.tablero

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.MainActivity
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.ui.alertas.AlertaAdapter
import com.example.trabajopracticoing2.ui.alertas.DetalleAlertaActivity
import com.example.trabajopracticoing2.ui.auditoria.AuditoriaActivity
import com.example.trabajopracticoing2.ui.comun.Formato
import com.example.trabajopracticoing2.ui.incidentes.NuevoIncidenteActivity
import com.example.trabajopracticoing2.ui.procedimientos.ProcedimientosActivity
import com.example.trabajopracticoing2.ui.sectores.DetalleSectorActivity
import com.example.trabajopracticoing2.ui.sectores.SectorAdapter
import com.example.trabajopracticoing2.ui.tareas.NuevaTareaActivity
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla principal del responsable de Seguridad e Higiene.
 *
 * Da la vision global exigida por la consigna: semaforo de estado de planta,
 * indicadores del dia, alertas que requieren atencion, estado por sector y
 * accesos rapidos a las operaciones mas frecuentes.
 */
class TableroFragment : Fragment() {

    private val modelo: TableroViewModel by viewModels()

    private lateinit var adaptadorAlertas: AlertaAdapter
    private lateinit var adaptadorSectores: SectorAdapter

    private lateinit var indicadorEstado: View
    private lateinit var etiquetaEstado: TextView
    private lateinit var mensajeEstado: TextView
    private lateinit var textoActualizado: TextView
    private lateinit var textoSinAlertas: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        contenedor: ViewGroup?,
        estado: Bundle?
    ): View = inflater.inflate(R.layout.fragment_tablero, contenedor, false)

    override fun onViewCreated(vista: View, estado: Bundle?) {
        super.onViewCreated(vista, estado)
        enlazarVistas(vista)
        configurarListas(vista)
        configurarAccesos(vista)
        observarModelo()
    }

    override fun onResume() {
        super.onResume()
        // Al volver de una pantalla de detalle los indicadores se recalculan.
        modelo.refrescar()
    }

    private fun enlazarVistas(vista: View) {
        indicadorEstado = vista.findViewById(R.id.indicadorEstado)
        etiquetaEstado = vista.findViewById(R.id.etiquetaEstadoGeneral)
        mensajeEstado = vista.findViewById(R.id.textoEstadoMensaje)
        textoActualizado = vista.findViewById(R.id.textoActualizado)
        textoSinAlertas = vista.findViewById(R.id.textoSinAlertasCriticas)
    }

    private fun configurarListas(vista: View) {
        val listaAlertas = vista.findViewById<RecyclerView>(R.id.listaAlertasCriticas)
        adaptadorAlertas = AlertaAdapter(emptyList()) { alerta ->
            startActivity(DetalleAlertaActivity.intent(requireContext(), alerta.id))
        }
        listaAlertas.layoutManager = LinearLayoutManager(requireContext())
        listaAlertas.adapter = adaptadorAlertas

        val listaSectores = vista.findViewById<RecyclerView>(R.id.listaSectores)
        adaptadorSectores = SectorAdapter(emptyList()) { sector ->
            startActivity(DetalleSectorActivity.intent(requireContext(), sector.id))
        }
        listaSectores.layoutManager = LinearLayoutManager(requireContext())
        listaSectores.adapter = adaptadorSectores
    }

    private fun configurarAccesos(vista: View) {
        vista.findViewById<MaterialButton>(R.id.botonVerAlertas).setOnClickListener {
            (activity as? MainActivity)?.seleccionarDestino(R.id.nav_alertas)
        }
        vista.findViewById<MaterialButton>(R.id.botonAccesoIncidente).setOnClickListener {
            startActivity(Intent(requireContext(), NuevoIncidenteActivity::class.java))
        }
        vista.findViewById<MaterialButton>(R.id.botonAccesoTarea).setOnClickListener {
            startActivity(Intent(requireContext(), NuevaTareaActivity::class.java))
        }
        vista.findViewById<MaterialButton>(R.id.botonAccesoProcedimientos).setOnClickListener {
            startActivity(Intent(requireContext(), ProcedimientosActivity::class.java))
        }
        vista.findViewById<MaterialButton>(R.id.botonAccesoAuditoria).setOnClickListener {
            startActivity(Intent(requireContext(), AuditoriaActivity::class.java))
        }
    }

    private fun observarModelo() {
        val raiz = requireView()

        modelo.resumen.observe(viewLifecycleOwner) { resumen ->
            Formato.aplicarEstado(etiquetaEstado, resumen.estado)
            indicadorEstado.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), resumen.estado.colorRes)
            mensajeEstado.text = resumen.mensaje
            textoActualizado.text = getString(
                R.string.tablero_actualizado_a,
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            )

            raiz.findViewById<TextView>(R.id.valorAlertas).text = resumen.alertasActivas.toString()
            raiz.findViewById<TextView>(R.id.valorIncidentes).text =
                resumen.incidentesAbiertos.toString()
            raiz.findViewById<TextView>(R.id.valorTareas).text = resumen.tareasVencidas.toString()
            raiz.findViewById<TextView>(R.id.valorCumplimiento).text =
                getString(R.string.porcentaje, resumen.cumplimientoPromedio)
            raiz.findViewById<TextView>(R.id.valorDias).text = resumen.diasSinAccidentes.toString()
        }

        modelo.alertasCriticas.observe(viewLifecycleOwner) { alertas ->
            adaptadorAlertas.actualizar(alertas)
            textoSinAlertas.visibility = if (alertas.isEmpty()) View.VISIBLE else View.GONE
        }

        modelo.sectores.observe(viewLifecycleOwner) { sectores ->
            adaptadorSectores.actualizar(sectores)
        }
    }
}
