package com.example.trabajopracticoing2.ui.auditoria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.model.RegistroAuditoria
import com.example.trabajopracticoing2.ui.comun.Formato

/**
 * Adaptador del registro de auditoria.
 * Cada fila responde a quien, cuando, que operacion y sobre que entidad.
 */
class AuditoriaAdapter(
    private var registros: List<RegistroAuditoria>
) : RecyclerView.Adapter<AuditoriaAdapter.Celda>() {

    class Celda(vista: View) : RecyclerView.ViewHolder(vista) {
        val tipo: TextView = vista.findViewById(R.id.etiquetaAudTipo)
        val fecha: TextView = vista.findViewById(R.id.textoAudFecha)
        val detalle: TextView = vista.findViewById(R.id.textoAudDetalle)
        val usuario: TextView = vista.findViewById(R.id.textoAudUsuario)
        val entidad: TextView = vista.findViewById(R.id.textoAudEntidad)
    }

    override fun onCreateViewHolder(padre: ViewGroup, tipoVista: Int): Celda =
        Celda(LayoutInflater.from(padre.context).inflate(R.layout.item_auditoria, padre, false))

    override fun getItemCount(): Int = registros.size

    override fun onBindViewHolder(celda: Celda, posicion: Int) {
        val registro = registros[posicion]
        val contexto = celda.itemView.context

        Formato.aplicarColores(
            celda.tipo,
            registro.tipo.etiqueta,
            R.color.azul_primario,
            R.color.azul_suave
        )
        celda.fecha.text = registro.fechaHora
        celda.detalle.text = registro.detalle
        celda.usuario.text =
            contexto.getString(R.string.auditoria_usuario, registro.usuario, registro.rol)
        celda.entidad.text =
            contexto.getString(R.string.auditoria_entidad, registro.id, registro.entidad)
    }

    fun actualizar(nuevos: List<RegistroAuditoria>) {
        registros = nuevos
        notifyDataSetChanged()
    }
}
