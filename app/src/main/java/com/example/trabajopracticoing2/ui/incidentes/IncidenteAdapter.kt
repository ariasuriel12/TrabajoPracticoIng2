package com.example.trabajopracticoing2.ui.incidentes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.Incidente
import com.example.trabajopracticoing2.ui.comun.Formato

/** Adaptador de la lista de incidentes: identificador, estado y acciones pendientes. */
class IncidenteAdapter(
    private var incidentes: List<Incidente>,
    private val alTocar: (Incidente) -> Unit
) : RecyclerView.Adapter<IncidenteAdapter.Celda>() {

    class Celda(vista: View) : RecyclerView.ViewHolder(vista) {
        val franja: View = vista.findViewById(R.id.franjaIncidente)
        val identificador: TextView = vista.findViewById(R.id.textoIncidenteId)
        val estado: TextView = vista.findViewById(R.id.etiquetaIncidenteEstado)
        val titulo: TextView = vista.findViewById(R.id.textoIncidenteTitulo)
        val sector: TextView = vista.findViewById(R.id.textoIncidenteSector)
        val fecha: TextView = vista.findViewById(R.id.textoIncidenteFecha)
        val acciones: TextView = vista.findViewById(R.id.textoIncidenteAcciones)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoVista: Int): Celda =
        Celda(LayoutInflater.from(padre.context).inflate(R.layout.item_incidente, padre, false))

    override fun getItemCount(): Int = incidentes.size

    override fun onBindViewHolder(celda: Celda, posicion: Int) {
        val incidente = incidentes[posicion]
        val contexto = celda.itemView.context

        celda.identificador.text = incidente.id
        Formato.aplicarColores(
            celda.estado,
            incidente.estado.etiqueta,
            incidente.estado.colorRes,
            incidente.estado.colorFondoRes
        )
        Formato.aplicarFranja(celda.franja, incidente.severidad.colorRes)

        celda.titulo.text = incidente.titulo
        celda.sector.text = RepositorioSeguridad.nombreSector(incidente.sectorId)
        celda.fecha.text = incidente.fecha

        val pendientes = incidente.accionesPendientes
        celda.acciones.text = contexto.getString(R.string.incidente_acciones_pendientes, pendientes)
        celda.acciones.setTextColor(
            Formato.colorDeTexto(
                contexto,
                if (pendientes > 0) R.color.estado_alto else R.color.estado_normal
            )
        )

        celda.itemView.setOnClickListener { alTocar(incidente) }
    }

    fun actualizar(nuevos: List<Incidente>) {
        incidentes = nuevos
        notifyDataSetChanged()
    }
}
