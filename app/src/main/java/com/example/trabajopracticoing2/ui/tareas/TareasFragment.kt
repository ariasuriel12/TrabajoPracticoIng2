package com.example.trabajopracticoing2.ui.tareas

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
import androidx.appcompat.app.AlertDialog
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

/** Listado de tareas y pendientes con seguimiento de vencimientos. */
class TareasFragment : Fragment() {

    private val modelo: TareasViewModel by viewModels()
    private lateinit var adaptador: TareaAdapter
    private lateinit var estadoVacio: View

    override fun onCreateView(
        inflater: LayoutInflater,
        contenedor: ViewGroup?,
        estado: Bundle?
    ): View = inflater.inflate(R.layout.fragment_tareas, contenedor, false)

    override fun onViewCreated(vista: View, estado: Bundle?) {
        super.onViewCreated(vista, estado)
        estadoVacio = vista.findViewById(R.id.estadoVacio)
        vista.findViewById<TextView>(R.id.textoVacio).setText(R.string.tareas_vacio)

        val lista = vista.findViewById<RecyclerView>(R.id.listaTareas)
        adaptador = TareaAdapter(emptyList(),
            { tarea ->
                if (SesionUsuario.puede(Permiso.COMPLETAR_TAREA)) {
                    if (modelo.completar(tarea)) {
                        Formato.aviso(requireView(), R.string.tarea_completada)
                    }
                } else {
                    Formato.aviso(requireView(), R.string.sin_permiso)
                    modelo.refrescar()
                }
            },
            { tarea -> // editar
                val intent = Intent(requireContext(), NuevaTareaActivity::class.java)
                intent.putExtra("tareaId", tarea.id)
                startActivity(intent)
            },
            { tarea -> // eliminar
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.tarea_confirmar_titulo)
                    .setMessage(R.string.tarea_confirmar_mensaje)
                    .setNegativeButton(R.string.form_cancelar, null)
                    .setPositiveButton(R.string.tarea_eliminar_si) { _, _ ->
                        val ok = com.example.trabajopracticoing2.data.RepositorioSeguridad.eliminarTarea(tarea.id)
                        if (ok) {
                            modelo.refrescar()
                            Formato.aviso(requireView(), R.string.tarea_eliminada)
                        } else {
                            Formato.aviso(requireView(), R.string.sin_permiso)
                        }
                    }
                    .show()
            }
        )
        lista.layoutManager = LinearLayoutManager(requireContext())
        lista.adapter = adaptador

        vista.findViewById<ChipGroup>(R.id.grupoFiltroTareas)
            .setOnCheckedStateChangeListener { _, elegidos ->
                val filtro = when (elegidos.firstOrNull()) {
                    R.id.chipTareasPendientes -> FiltroTareas.PENDIENTES
                    R.id.chipTareasVencidas -> FiltroTareas.VENCIDAS
                    R.id.chipTareasCompletadas -> FiltroTareas.COMPLETADAS
                    else -> FiltroTareas.TODAS
                }
                modelo.aplicarFiltro(filtro)
            }

        vista.findViewById<FloatingActionButton>(R.id.botonNuevaTarea).setOnClickListener { boton ->
            if (SesionUsuario.puede(Permiso.ASIGNAR_TAREA)) {
                startActivity(Intent(requireContext(), NuevaTareaActivity::class.java))
            } else {
                Formato.aviso(boton, R.string.sin_permiso)
            }
        }

        modelo.tareas.observe(viewLifecycleOwner) { tareas ->
            adaptador.actualizar(tareas)
            estadoVacio.visibility = if (tareas.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        modelo.refrescar()
    }
}
