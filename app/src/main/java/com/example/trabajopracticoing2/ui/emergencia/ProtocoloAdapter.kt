package com.example.trabajopracticoing2.ui.emergencia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.model.ProtocoloEmergencia
import com.google.android.material.card.MaterialCardView

/** Adaptador de la lista de protocolos de emergencia (seleccion unica). */
class ProtocoloAdapter(
    private val protocolos: List<ProtocoloEmergencia>,
    private val alElegir: (ProtocoloEmergencia) -> Unit
) : RecyclerView.Adapter<ProtocoloAdapter.Celda>() {

    private var seleccionado: String? = null

    class Celda(vista: View) : RecyclerView.ViewHolder(vista) {
        val tarjeta: MaterialCardView = vista.findViewById(R.id.tarjetaProtocolo)
        val nombre: TextView = vista.findViewById(R.id.textoProtocoloNombre)
        val tiempo: TextView = vista.findViewById(R.id.textoProtocoloTiempo)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoVista: Int): Celda =
        Celda(LayoutInflater.from(padre.context).inflate(R.layout.item_protocolo, padre, false))

    override fun getItemCount(): Int = protocolos.size

    override fun onBindViewHolder(celda: Celda, posicion: Int) {
        val protocolo = protocolos[posicion]
        val contexto = celda.itemView.context

        celda.nombre.text = protocolo.nombre
        celda.tiempo.text = protocolo.tiempoObjetivo

        val elegido = protocolo.id == seleccionado
        celda.tarjeta.setStrokeWidth(if (elegido) GROSOR_SELECCION else 0)
        celda.tarjeta.setStrokeColor(ContextCompat.getColor(contexto, R.color.estado_critico))

        celda.itemView.setOnClickListener {
            seleccionado = protocolo.id
            notifyDataSetChanged()
            alElegir(protocolo)
        }
    }

    private companion object {
        /** Grosor en px del borde que marca el protocolo seleccionado. */
        const val GROSOR_SELECCION = 6
    }
}
