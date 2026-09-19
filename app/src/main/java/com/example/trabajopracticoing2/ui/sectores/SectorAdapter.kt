package com.example.trabajopracticoing2.ui.sectores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.Sector
import com.example.trabajopracticoing2.ui.comun.Formato

/**
 * Adaptador del listado de sectores.
 * El estado se recalcula a partir de las alertas activas, de modo que el
 * semaforo por sector siempre refleja la situacion actual de la planta.
 */
class SectorAdapter(
    private var sectores: List<Sector>,
    private val alTocar: (Sector) -> Unit
) : RecyclerView.Adapter<SectorAdapter.Celda>() {

    class Celda(vista: View) : RecyclerView.ViewHolder(vista) {
        val indicador: View = vista.findViewById(R.id.indicadorSector)
        val nombre: TextView = vista.findViewById(R.id.textoSectorNombre)
        val riesgo: TextView = vista.findViewById(R.id.textoSectorRiesgo)
        val alertas: TextView = vista.findViewById(R.id.textoSectorAlertas)
        val estado: TextView = vista.findViewById(R.id.etiquetaSectorEstado)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoVista: Int): Celda =
        Celda(LayoutInflater.from(padre.context).inflate(R.layout.item_sector, padre, false))

    override fun getItemCount(): Int = sectores.size

    override fun onBindViewHolder(celda: Celda, posicion: Int) {
        val sector = sectores[posicion]
        val contexto = celda.itemView.context
        val estado = RepositorioSeguridad.estadoDeSector(sector.id)
        val activas = RepositorioSeguridad.alertasDeSector(sector.id).count { !it.reconocida }

        celda.nombre.text = sector.nombre
        celda.riesgo.text = contexto.getString(R.string.sector_riesgo, sector.riesgoPrincipal)
        celda.alertas.text = contexto.getString(R.string.sector_alertas_activas, activas)
        Formato.aplicarEstado(celda.estado, estado)
        celda.indicador.backgroundTintList =
            ContextCompat.getColorStateList(contexto, estado.colorRes)

        celda.itemView.setOnClickListener { alTocar(sector) }
    }

    fun actualizar(nuevos: List<Sector>) {
        sectores = nuevos
        notifyDataSetChanged()
    }
}
