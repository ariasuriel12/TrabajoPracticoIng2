package com.example.trabajopracticoing2.ui.procedimientos

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.ui.comun.ActividadBase

/**
 * Listado de procedimientos ordenado por menor cumplimiento primero:
 * lo que necesita atencion aparece arriba.
 */
class ProcedimientosActivity : ActividadBase() {

    private lateinit var adaptador: ProcedimientoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_simple)
        prepararRepositorio()
        configurarBarra(R.string.procedimientos_titulo)

        findViewById<TextView>(R.id.textoEncabezadoLista)
            .setText(R.string.procedimientos_ayuda)
        findViewById<TextView>(R.id.textoVacio).setText(R.string.procedimientos_vacio)

        val lista = findViewById<RecyclerView>(R.id.listaGenerica)
        adaptador = ProcedimientoAdapter(emptyList()) { procedimiento ->
            startActivity(DetalleProcedimientoActivity.intent(this, procedimiento.id))
        }
        lista.layoutManager = LinearLayoutManager(this)
        lista.adapter = adaptador
    }

    override fun onResume() {
        super.onResume()
        val procedimientos = RepositorioSeguridad.procedimientos()
        adaptador.actualizar(procedimientos)
        findViewById<View>(R.id.estadoVacio).visibility =
            if (procedimientos.isEmpty()) View.VISIBLE else View.GONE
    }
}
