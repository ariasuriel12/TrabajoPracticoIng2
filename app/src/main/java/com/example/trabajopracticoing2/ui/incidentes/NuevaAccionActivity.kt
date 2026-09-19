package com.example.trabajopracticoing2.ui.incidentes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.dominio.ResultadoValidacion
import com.example.trabajopracticoing2.dominio.Validadores
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Alta de una accion correctiva asociada a un incidente.
 * Permite asignar responsabilidades, requisito explicito del usuario principal.
 */
class NuevaAccionActivity : ActividadBase() {

    private lateinit var incidenteId: String
    private lateinit var campoDescripcion: TextInputLayout
    private lateinit var campoResponsable: TextInputLayout
    private lateinit var entradaDescripcion: TextInputEditText
    private lateinit var entradaResponsable: MaterialAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_accion)
        prepararRepositorio()

        incidenteId = intent.getStringExtra(EXTRA_INCIDENTE).orEmpty()
        if (RepositorioSeguridad.incidente(incidenteId) == null) {
            finish()
            return
        }
        configurarBarra(getString(R.string.incidente_nueva_accion), incidenteId)

        campoDescripcion = findViewById(R.id.campoDescripcionAccion)
        campoResponsable = findViewById(R.id.campoResponsableAccion)
        entradaDescripcion = findViewById(R.id.entradaDescripcionAccion)
        entradaResponsable = findViewById(R.id.entradaResponsableAccion)
        entradaResponsable.setSimpleItems(
            RepositorioSeguridad.responsablesDisponibles().toTypedArray()
        )

        findViewById<MaterialButton>(R.id.botonGuardarAccion).setOnClickListener { guardar(it) }
        findViewById<MaterialButton>(R.id.botonCancelarAccion).setOnClickListener { finish() }
    }

    private fun guardar(vista: View) {
        campoDescripcion.error = null
        campoResponsable.error = null
        var hayErrores = false

        val descripcion = entradaDescripcion.text?.toString().orEmpty().trim()
        val validacion = Validadores.textoObligatorio(descripcion, MIN_DESCRIPCION)
        if (validacion is ResultadoValidacion.Invalido) {
            campoDescripcion.error = getString(R.string.accion_descripcion_corta)
            hayErrores = true
        }

        val responsable = entradaResponsable.text?.toString().orEmpty().trim()
        if (Validadores.seleccion(responsable) is ResultadoValidacion.Invalido) {
            campoResponsable.error = getString(R.string.form_seleccione)
            hayErrores = true
        }

        if (hayErrores) {
            Formato.aviso(vista, R.string.form_errores)
            return
        }

        RepositorioSeguridad.agregarAccionCorrectiva(incidenteId, descripcion, responsable)
        setResult(RESULT_OK)
        finish()
    }

    companion object {
        private const val EXTRA_INCIDENTE = "incidente_id"
        private const val MIN_DESCRIPCION = 10

        fun intent(contexto: Context, incidenteId: String): Intent =
            Intent(contexto, NuevaAccionActivity::class.java)
                .putExtra(EXTRA_INCIDENTE, incidenteId)
    }
}
