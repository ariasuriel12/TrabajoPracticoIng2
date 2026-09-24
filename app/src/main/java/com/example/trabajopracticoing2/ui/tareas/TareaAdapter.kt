package com.example.trabajopracticoing2.ui.tareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.model.EstadoTarea
import com.example.trabajopracticoing2.model.Tarea
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.checkbox.MaterialCheckBox

/** Adaptador de tareas con accion directa de completado desde la lista. */
class TareaAdapter(
    private var tareas: List<Tarea>,
    private val alCompletar: (Tarea) -> Unit,
    private val alEditar: (Tarea) -> Unit,
    private val alEliminar: (Tarea) -> Unit
) : RecyclerView.Adapter<TareaAdapter.Celda>() {

    class Celda(vista: View) : RecyclerView.ViewHolder(vista) {
        val check: MaterialCheckBox = vista.findViewById(R.id.checkTarea)
        val titulo: TextView = vista.findViewById(R.id.textoTareaTitulo)
        val detalle: TextView = vista.findViewById(R.id.textoTareaDetalle)
        val severidad: TextView = vista.findViewById(R.id.etiquetaTareaSeveridad)
        val vencimiento: TextView = vista.findViewById(R.id.textoTareaVencimiento)
        val responsable: TextView = vista.findViewById(R.id.textoTareaResponsable)
        val botonEditar = vista.findViewById<ImageButton>(R.id.botonEditarTarea)
        val botonEliminar = vista.findViewById<ImageButton>(R.id.botonEliminarTarea)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoVista: Int): Celda =
        Celda(LayoutInflater.from(padre.context).inflate(R.layout.item_tarea, padre, false))

    override fun getItemCount(): Int = tareas.size

    override fun onBindViewHolder(celda: Celda, posicion: Int) {
        val tarea = tareas[posicion]
        val contexto = celda.itemView.context

        celda.titulo.text = tarea.titulo
        celda.detalle.text = tarea.detalle
        Formato.aplicarSeveridad(celda.severidad, tarea.severidad)

        celda.vencimiento.text = tarea.textoVencimiento
        celda.vencimiento.setTextColor(
            Formato.colorDeTexto(
                contexto,
                when {
                    tarea.estado == EstadoTarea.COMPLETADA -> R.color.estado_normal
                    tarea.vencida -> R.color.estado_critico
                    tarea.diasParaVencer <= 3 -> R.color.estado_alto
                    else -> R.color.texto_secundario
                }
            )
        )

        celda.responsable.text = contexto.getString(
            R.string.tarea_responsable_sector,
            tarea.responsable,
            RepositorioSeguridad.nombreSector(tarea.sectorId)
        )

        // Se limpia el listener antes de asignar el estado para evitar
        // disparos espurios al reciclar la celda.
        celda.check.setOnCheckedChangeListener(null)
        val completada = tarea.estado == EstadoTarea.COMPLETADA
        celda.check.isChecked = completada
        celda.check.isEnabled = !completada
        celda.check.setOnCheckedChangeListener { _, marcada ->
            if (marcada && !completada) alCompletar(tarea)
        }

        // Editar / Eliminar según permisos
        val puedeEditar = SesionUsuario.puede(Permiso.ASIGNAR_TAREA)
        celda.botonEditar.visibility = if (puedeEditar) View.VISIBLE else View.GONE
        celda.botonEliminar.visibility = if (puedeEditar) View.VISIBLE else View.GONE
        celda.botonEditar.setOnClickListener { alEditar(tarea) }
        celda.botonEliminar.setOnClickListener { alEliminar(tarea) }
    }

    fun actualizar(nuevas: List<Tarea>) {
        tareas = nuevas
        notifyDataSetChanged()
    }
}
