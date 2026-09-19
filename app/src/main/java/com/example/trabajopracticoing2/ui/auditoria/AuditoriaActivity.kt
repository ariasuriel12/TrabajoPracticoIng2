package com.example.trabajopracticoing2.ui.auditoria

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioAuditoria
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato

/**
 * Registro de auditoria y trazabilidad.
 * Es una pantalla de solo lectura y de acceso restringido: responde a quien
 * hizo que, cuando y sobre que registro.
 */
class AuditoriaActivity : ActividadBase() {

    private lateinit var adaptador: AuditoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_simple)
        prepararRepositorio()
        configurarBarra(R.string.auditoria_titulo)

        findViewById<TextView>(R.id.textoEncabezadoLista).setText(R.string.auditoria_ayuda)
        findViewById<TextView>(R.id.textoVacio).setText(R.string.auditoria_vacio)

        // Operacion restringida: sin permiso la pantalla no muestra datos.
        if (!SesionUsuario.puede(Permiso.VER_AUDITORIA)) {
            Formato.aviso(findViewById(R.id.listaGenerica), R.string.sin_permiso)
            findViewById<View>(R.id.estadoVacio).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textoVacio).setText(R.string.sin_permiso)
            return
        }

        val lista = findViewById<RecyclerView>(R.id.listaGenerica)
        adaptador = AuditoriaAdapter(emptyList())
        lista.layoutManager = LinearLayoutManager(this)
        lista.adapter = adaptador
    }

    override fun onResume() {
        super.onResume()
        if (!SesionUsuario.puede(Permiso.VER_AUDITORIA)) return
        val registros = RepositorioAuditoria.listar()
        adaptador.actualizar(registros)
        findViewById<View>(R.id.estadoVacio).visibility =
            if (registros.isEmpty()) View.VISIBLE else View.GONE
    }
}
