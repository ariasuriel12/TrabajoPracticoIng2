package com.example.trabajopracticoing2.ui.tareas

import android.os.Bundle
import android.view.View
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.dominio.ResultadoValidacion
import com.example.trabajopracticoing2.dominio.Validadores
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.model.Severidad
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Formulario de alta de tareas.
 * Cubre la necesidad de asignar responsabilidades y fijar un vencimiento
 * para poder hacer seguimiento de los pendientes.
 */
class NuevaTareaActivity : ActividadBase() {

    private lateinit var campoTitulo: TextInputLayout
    private lateinit var campoDetalle: TextInputLayout
    private lateinit var campoResponsable: TextInputLayout
    private lateinit var campoSector: TextInputLayout
    private lateinit var campoSeveridad: TextInputLayout
    private lateinit var campoVencimiento: TextInputLayout

    private lateinit var entradaTitulo: TextInputEditText
    private lateinit var entradaDetalle: TextInputEditText
    private lateinit var entradaResponsable: MaterialAutoCompleteTextView
    private lateinit var entradaSector: MaterialAutoCompleteTextView
    private lateinit var entradaSeveridad: MaterialAutoCompleteTextView
    private lateinit var entradaVencimiento: TextInputEditText

    private val severidades = Severidad.values()
    private val sectores by lazy { RepositorioSeguridad.sectores() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_tarea)
        prepararRepositorio()
        configurarBarra(R.string.tarea_nueva)

        campoTitulo = findViewById(R.id.campoTituloTarea)
        campoDetalle = findViewById(R.id.campoDetalleTarea)
        campoResponsable = findViewById(R.id.campoResponsableTarea)
        campoSector = findViewById(R.id.campoSectorTarea)
        campoSeveridad = findViewById(R.id.campoSeveridadTarea)
        campoVencimiento = findViewById(R.id.campoVencimientoTarea)

        entradaTitulo = findViewById(R.id.entradaTituloTarea)
        entradaDetalle = findViewById(R.id.entradaDetalleTarea)
        entradaResponsable = findViewById(R.id.entradaResponsableTarea)
        entradaSector = findViewById(R.id.entradaSectorTarea)
        entradaSeveridad = findViewById(R.id.entradaSeveridadTarea)
        entradaVencimiento = findViewById(R.id.entradaVencimientoTarea)

        entradaResponsable.setSimpleItems(
            RepositorioSeguridad.responsablesDisponibles().toTypedArray()
        )
        entradaSector.setSimpleItems(sectores.map { it.nombre }.toTypedArray())
        entradaSeveridad.setSimpleItems(severidades.map { it.etiqueta }.toTypedArray())
        entradaSeveridad.setText(Severidad.MEDIA.etiqueta, false)
        entradaVencimiento.setText("7")

        findViewById<MaterialButton>(R.id.botonGuardarTarea).setOnClickListener { guardar(it) }
        findViewById<MaterialButton>(R.id.botonCancelarTarea).setOnClickListener { finish() }
    }

    private fun guardar(vista: View) {
        if (!SesionUsuario.puede(Permiso.ASIGNAR_TAREA)) {
            Formato.aviso(vista, R.string.sin_permiso)
            return
        }

        listOf(campoTitulo, campoDetalle, campoResponsable, campoSector, campoSeveridad, campoVencimiento)
            .forEach { it.error = null }
        var hayErrores = false

        val titulo = entradaTitulo.text?.toString().orEmpty().trim()
        if (Validadores.textoObligatorio(titulo, MIN_TITULO) is ResultadoValidacion.Invalido) {
            campoTitulo.error = getString(R.string.form_titulo_corto)
            hayErrores = true
        }

        val detalle = entradaDetalle.text?.toString().orEmpty().trim()
        if (Validadores.textoObligatorio(detalle, MIN_DETALLE) is ResultadoValidacion.Invalido) {
            campoDetalle.error = getString(R.string.form_descripcion_corta)
            hayErrores = true
        }

        val responsable = entradaResponsable.text?.toString().orEmpty().trim()
        if (Validadores.seleccion(responsable) is ResultadoValidacion.Invalido) {
            campoResponsable.error = getString(R.string.form_seleccione)
            hayErrores = true
        }

        val sector = sectores.firstOrNull { it.nombre == entradaSector.text?.toString()?.trim() }
        if (sector == null) {
            campoSector.error = getString(R.string.form_seleccione)
            hayErrores = true
        }

        val severidad =
            severidades.firstOrNull { it.etiqueta == entradaSeveridad.text?.toString()?.trim() }
        if (severidad == null) {
            campoSeveridad.error = getString(R.string.form_seleccione)
            hayErrores = true
        }

        val vencimiento = entradaVencimiento.text?.toString().orEmpty().trim()
        if (Validadores.enteroEnRango(vencimiento, 0, 365) is ResultadoValidacion.Invalido) {
            campoVencimiento.error = getString(R.string.form_vencimiento_invalido)
            hayErrores = true
        }

        if (hayErrores) {
            Formato.aviso(vista, R.string.form_errores)
            return
        }

        RepositorioSeguridad.crearTarea(
            titulo = titulo,
            detalle = detalle,
            responsable = responsable,
            sectorId = sector!!.id,
            diasParaVencer = vencimiento.toInt(),
            severidad = severidad!!
        )
        setResult(RESULT_OK)
        finish()
    }

    private companion object {
        const val MIN_TITULO = 5
        const val MIN_DETALLE = 10
    }
}
