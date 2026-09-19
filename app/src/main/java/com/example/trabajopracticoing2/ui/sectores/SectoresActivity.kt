package com.example.trabajopracticoing2.ui.sectores

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.ui.comun.ActividadBase

/** Listado de sectores de planta con su semaforo de estado operativo. */
class SectoresActivity : ActividadBase() {

    private lateinit var adaptador: SectorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_simple)
        prepararRepositorio()
        configurarBarra(R.string.sectores_titulo)

        findViewById<TextView>(R.id.textoEncabezadoLista).setText(R.string.sectores_ayuda)
        findViewById<TextView>(R.id.textoVacio).setText(R.string.sectores_vacio)

        val lista = findViewById<RecyclerView>(R.id.listaGenerica)
        adaptador = SectorAdapter(emptyList()) { sector ->
            startActivity(DetalleSectorActivity.intent(this, sector.id))
        }
        lista.layoutManager = LinearLayoutManager(this)
        lista.adapter = adaptador
    }

    override fun onResume() {
        super.onResume()
        val sectores = RepositorioSeguridad.sectores()
        adaptador.actualizar(sectores)
        findViewById<View>(R.id.estadoVacio).visibility =
            if (sectores.isEmpty()) View.VISIBLE else View.GONE
    }
}
