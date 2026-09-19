package com.example.trabajopracticoing2.ui.emergencia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.model.ProtocoloEmergencia
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.MaterialAutoCompleteTextView

/**
 * Modo emergencia: la situacion critica identificada en la consigna.
 *
 * Esta pensada para leerse y operarse bajo presion:
 * - un unico objetivo por pantalla;
 * - los pasos del protocolo numerados y en orden;
 * - los contactos siempre visibles, sin navegacion adicional;
 * - confirmacion explicita antes de declarar, por ser una accion irreversible.
 */
class EmergenciaActivity : ActividadBase() {

    private var protocoloElegido: ProtocoloEmergencia? = null

    private lateinit var entradaSector: MaterialAutoCompleteTextView
    private lateinit var tarjetaPasos: MaterialCardView
    private lateinit var contenedorPasos: LinearLayout
    private lateinit var textoTiempoObjetivo: TextView
    private lateinit var botonDeclarar: MaterialButton

    private val sectores by lazy { RepositorioSeguridad.sectores() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergencia)
        prepararRepositorio()
        configurarBarra(R.string.emergencia_titulo)

        entradaSector = findViewById(R.id.entradaSectorEmergencia)
        tarjetaPasos = findViewById(R.id.tarjetaPasos)
        contenedorPasos = findViewById(R.id.contenedorPasos)
        textoTiempoObjetivo = findViewById(R.id.textoTiempoObjetivo)
        botonDeclarar = findViewById(R.id.botonDeclararEmergencia)

        configurarSector()
        configurarProtocolos()
        pintarContactos()

        botonDeclarar.setOnClickListener { confirmarDeclaracion(it) }
    }

    private fun configurarSector() {
        entradaSector.setSimpleItems(sectores.map { it.nombre }.toTypedArray())
        sectores.firstOrNull()?.let { entradaSector.setText(it.nombre, false) }
    }

    private fun configurarProtocolos() {
        val lista = findViewById<RecyclerView>(R.id.listaProtocolos)
        lista.layoutManager = LinearLayoutManager(this)
        lista.adapter = ProtocoloAdapter(RepositorioSeguridad.protocolos()) { protocolo ->
            protocoloElegido = protocolo
            pintarPasos(protocolo)
        }
    }

    private fun pintarPasos(protocolo: ProtocoloEmergencia) {
        tarjetaPasos.visibility = View.VISIBLE
        textoTiempoObjetivo.text = protocolo.tiempoObjetivo
        contenedorPasos.removeAllViews()

        val inflador = LayoutInflater.from(this)
        protocolo.pasos.forEach { paso ->
            val fila: View = inflador.inflate(R.layout.item_paso, contenedorPasos, false)
            fila.findViewById<TextView>(R.id.textoPasoOrden).text = paso.orden.toString()
            fila.findViewById<TextView>(R.id.textoPasoDescripcion).text = paso.descripcion
            contenedorPasos.addView(fila)
        }
        botonDeclarar.isEnabled = true
    }

    private fun pintarContactos() {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorContactos)
        val inflador = LayoutInflater.from(this)
        RepositorioSeguridad.contactos().forEach { contacto ->
            val fila: View = inflador.inflate(R.layout.item_contacto, contenedor, false)
            fila.findViewById<TextView>(R.id.textoContactoNombre).text = contacto.nombre
            fila.findViewById<TextView>(R.id.textoContactoRol).text = contacto.rol
            fila.findViewById<TextView>(R.id.textoContactoTelefono).text = contacto.telefono
            contenedor.addView(fila)
        }
    }

    /** Accion irreversible: se pide confirmacion explicita antes de ejecutarla. */
    private fun confirmarDeclaracion(vista: View) {
        val protocolo = protocoloElegido ?: return

        if (!SesionUsuario.puede(Permiso.DECLARAR_EMERGENCIA)) {
            Formato.aviso(vista, R.string.emergencia_sin_permiso)
            return
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.emergencia_confirmar_titulo)
            .setMessage(R.string.emergencia_confirmar_mensaje)
            .setNegativeButton(R.string.form_cancelar, null)
            .setPositiveButton(R.string.emergencia_confirmar_si) { _, _ ->
                val sector = sectores.firstOrNull {
                    it.nombre == entradaSector.text?.toString()?.trim()
                }
                RepositorioSeguridad.declararEmergencia(
                    protocolo.id,
                    sector?.id ?: sectores.first().id
                )
                Formato.aviso(vista, R.string.emergencia_declarada)
                botonDeclarar.isEnabled = false
                botonDeclarar.setText(R.string.emergencia_ya_declarada)
            }
            .show()
    }
}
