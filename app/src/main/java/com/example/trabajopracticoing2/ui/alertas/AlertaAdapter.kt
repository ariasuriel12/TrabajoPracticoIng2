package com.example.trabajopracticoing2.ui.alertas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.Alerta
import com.example.trabajopracticoing2.ui.comun.Formato

/**
 * Adaptador de la lista de alertas.
 * La franja lateral y la etiqueta de severidad permiten priorizar visualmente
 * sin necesidad de leer el texto completo.
 */
class AlertaAdapter(
    private var alertas: List<Alerta>,
    private val alTocar: (Alerta) -> Unit
) : RecyclerView.Adapter<AlertaAdapter.Celda>() {

    class Celda(vista: View) : RecyclerView.ViewHolder(vista) {
        val franja: View = vista.findViewById(R.id.franjaAlerta)
        val severidad: TextView = vista.findViewById(R.id.etiquetaAlertaSeveridad)
        val tipo: TextView = vista.findViewById(R.id.textoAlertaTipo)
        val tiempo: TextView = vista.findViewById(R.id.textoAlertaTiempo)
        val titulo: TextView = vista.findViewById(R.id.textoAlertaTitulo)
        val sector: TextView = vista.findViewById(R.id.textoAlertaSector)
        val estado: TextView = vista.findViewById(R.id.textoAlertaEstado)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoVista: Int): Celda =
        Celda(LayoutInflater.from(padre.context).inflate(R.layout.item_alerta, padre, false))

    override fun getItemCount(): Int = alertas.size

    override fun onBindViewHolder(celda: Celda, posicion: Int) {
        val alerta = alertas[posicion]
        val contexto = celda.itemView.context

        Formato.aplicarSeveridad(celda.severidad, alerta.severidad)
        Formato.aplicarFranja(celda.franja, alerta.severidad.colorRes)

        celda.tipo.text = alerta.tipo.etiqueta
        celda.tiempo.text = alerta.hace
        celda.titulo.text = alerta.titulo
        celda.sector.text = RepositorioSeguridad.nombreSector(alerta.sectorId)

        if (alerta.reconocida) {
            celda.estado.text = contexto.getString(
                R.string.alerta_estado_reconocida,
                alerta.reconocidaPor ?: "-"
            )
            celda.estado.setTextColor(Formato.colorDeTexto(contexto, R.color.estado_normal))
        } else {
            celda.estado.text = contexto.getString(R.string.alerta_estado_sin_reconocer)
            celda.estado.setTextColor(Formato.colorDeSeveridad(contexto, alerta.severidad))
        }

        celda.itemView.setOnClickListener { alTocar(alerta) }
    }

    fun actualizar(nuevas: List<Alerta>) {
        alertas = nuevas
        notifyDataSetChanged()
    }
}
