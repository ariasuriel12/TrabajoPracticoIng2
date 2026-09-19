package com.example.trabajopracticoing2.ui.alertas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.google.android.material.chip.ChipGroup

/**
 * Listado completo de alertas con filtros rapidos.
 * Permite pasar de la vision global del tablero al detalle de cada situacion.
 */
class AlertasFragment : Fragment() {

    private val modelo: AlertasViewModel by viewModels()
    private lateinit var adaptador: AlertaAdapter
    private lateinit var estadoVacio: View

    override fun onCreateView(
        inflater: LayoutInflater,
        contenedor: ViewGroup?,
        estado: Bundle?
    ): View = inflater.inflate(R.layout.fragment_alertas, contenedor, false)

    override fun onViewCreated(vista: View, estado: Bundle?) {
        super.onViewCreated(vista, estado)
        estadoVacio = vista.findViewById(R.id.estadoVacio)
        vista.findViewById<TextView>(R.id.textoVacio).setText(R.string.alertas_vacio)

        val lista = vista.findViewById<RecyclerView>(R.id.listaAlertas)
        adaptador = AlertaAdapter(emptyList()) { alerta ->
            startActivity(DetalleAlertaActivity.intent(requireContext(), alerta.id))
        }
        lista.layoutManager = LinearLayoutManager(requireContext())
        lista.adapter = adaptador

        vista.findViewById<ChipGroup>(R.id.grupoFiltroAlertas)
            .setOnCheckedStateChangeListener { _, elegidos ->
                val filtro = when (elegidos.firstOrNull()) {
                    R.id.chipAlertasCriticas -> FiltroAlertas.CRITICAS
                    R.id.chipAlertasActivas -> FiltroAlertas.ACTIVAS
                    else -> FiltroAlertas.TODAS
                }
                modelo.aplicarFiltro(filtro)
            }

        modelo.alertas.observe(viewLifecycleOwner) { alertas ->
            adaptador.actualizar(alertas)
            estadoVacio.visibility = if (alertas.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        modelo.refrescar()
    }
}
