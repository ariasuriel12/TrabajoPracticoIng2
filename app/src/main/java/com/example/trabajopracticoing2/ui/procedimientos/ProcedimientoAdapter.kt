package com.example.trabajopracticoing2.ui.procedimientos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.Procedimiento
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.progressindicator.LinearProgressIndicator

/** Adaptador del listado de procedimientos con su nivel de cumplimiento. */
class ProcedimientoAdapter(
    private var procedimientos: List<Procedimiento>,
    private val alTocar: (Procedimiento) -> Unit
) : RecyclerView.Adapter<ProcedimientoAdapter.Celda>() {

    class Celda(vista: View) : RecyclerView.ViewHolder(vista) {
        val codigo: TextView = vista.findViewById(R.id.textoProcCodigo)
        val estado: TextView = vista.findViewById(R.id.etiquetaProcEstado)
        val nombre: TextView = vista.findViewById(R.id.textoProcNombre)
        val barra: LinearProgressIndicator = vista.findViewById(R.id.barraProcCumplimiento)
        val porcentaje: TextView = vista.findViewById(R.id.textoProcPorcentaje)
        val ultima: TextView = vista.findViewById(R.id.textoProcUltima)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoVista: Int): Celda =
        Celda(LayoutInflater.from(padre.context).inflate(R.layout.item_procedimiento, padre, false))

    override fun getItemCount(): Int = procedimientos.size

    override fun onBindViewHolder(celda: Celda, posicion: Int) {
        val procedimiento = procedimientos[posicion]
        val contexto = celda.itemView.context

        celda.codigo.text = procedimiento.codigo
        celda.nombre.text = procedimiento.nombre
        Formato.aplicarEstado(celda.estado, procedimiento.estado)

        celda.barra.setProgressCompat(procedimiento.cumplimiento, false)
        celda.barra.setIndicatorColor(
            ContextCompat.getColor(contexto, procedimiento.estado.colorRes)
        )
        celda.porcentaje.text = contexto.getString(R.string.porcentaje, procedimiento.cumplimiento)
        celda.ultima.text = contexto.getString(
            R.string.procedimiento_ultima,
            procedimiento.ultimaVerificacion,
            RepositorioSeguridad.nombreSector(procedimiento.sectorId)
        )

        celda.itemView.setOnClickListener { alTocar(procedimiento) }
    }

    fun actualizar(nuevos: List<Procedimiento>) {
        procedimientos = nuevos
        notifyDataSetChanged()
    }
}
