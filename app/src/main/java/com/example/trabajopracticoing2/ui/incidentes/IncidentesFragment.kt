package com.example.trabajopracticoing2.ui.incidentes

import android.content.Intent
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
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

/** Listado de incidentes con filtros por estado y alta de nuevos incidentes. */
class IncidentesFragment : Fragment() {

    private val modelo: IncidentesViewModel by viewModels()
    private lateinit var adaptador: IncidenteAdapter
    private lateinit var estadoVacio: View

    override fun onCreateView(
        inflater: LayoutInflater,
        contenedor: ViewGroup?,
        estado: Bundle?
    ): View = inflater.inflate(R.layout.fragment_incidentes, contenedor, false)

    override fun onViewCreated(vista: View, estado: Bundle?) {
        super.onViewCreated(vista, estado)
        estadoVacio = vista.findViewById(R.id.estadoVacio)
        vista.findViewById<TextView>(R.id.textoVacio).setText(R.string.incidentes_vacio)

        val lista = vista.findViewById<RecyclerView>(R.id.listaIncidentes)
        adaptador = IncidenteAdapter(emptyList()) { incidente ->
            startActivity(DetalleIncidenteActivity.intent(requireContext(), incidente.id))
        }
        lista.layoutManager = LinearLayoutManager(requireContext())
        lista.adapter = adaptador

        vista.findViewById<ChipGroup>(R.id.grupoFiltroIncidentes)
            .setOnCheckedStateChangeListener { _, elegidos ->
                val filtro = when (elegidos.firstOrNull()) {
                    R.id.chipIncidentesAbiertos -> FiltroIncidentes.ABIERTOS
                    R.id.chipIncidentesProceso -> FiltroIncidentes.EN_PROCESO
                    R.id.chipIncidentesCerrados -> FiltroIncidentes.CERRADOS
                    else -> FiltroIncidentes.TODOS
                }
                modelo.aplicarFiltro(filtro)
            }

        // Operacion restringida: solo los perfiles habilitados pueden registrar.
        vista.findViewById<FloatingActionButton>(R.id.botonNuevoIncidente).setOnClickListener { boton ->
            if (SesionUsuario.puede(Permiso.REGISTRAR_INCIDENTE)) {
                startActivity(Intent(requireContext(), NuevoIncidenteActivity::class.java))
            } else {
                Formato.aviso(boton, R.string.sin_permiso)
            }
        }

        modelo.incidentes.observe(viewLifecycleOwner) { incidentes ->
            adaptador.actualizar(incidentes)
            estadoVacio.visibility = if (incidentes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        modelo.refrescar()
    }
}
