package com.example.trabajopracticoing2.ui.incidentes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.dominio.ResultadoValidacion
import com.example.trabajopracticoing2.dominio.Validadores
import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.model.Severidad
import com.example.trabajopracticoing2.model.TipoIncidente
import com.example.trabajopracticoing2.ui.comun.ActividadBase
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Formulario de alta de incidentes.
 *
 * Aplica prevencion de errores: todos los campos categoricos se eligen de
 * listas cerradas y los campos libres se validan antes de guardar.
 * Puede abrirse vacio o precargado desde una alerta (escalamiento).
 */
class NuevoIncidenteActivity : ActividadBase() {

    private lateinit var campoTitulo: TextInputLayout
    private lateinit var campoTipo: TextInputLayout
    private lateinit var campoSector: TextInputLayout
    private lateinit var campoSeveridad: TextInputLayout
    private lateinit var campoDescripcion: TextInputLayout
    private lateinit var campoPersonas: TextInputLayout
    private lateinit var campoResponsable: TextInputLayout

    private lateinit var entradaTitulo: TextInputEditText
    private lateinit var entradaTipo: MaterialAutoCompleteTextView
    private lateinit var entradaSector: MaterialAutoCompleteTextView
    private lateinit var entradaSeveridad: MaterialAutoCompleteTextView
    private lateinit var entradaDescripcion: TextInputEditText
    private lateinit var entradaPersonas: TextInputEditText
    private lateinit var entradaResponsable: MaterialAutoCompleteTextView

    private val tipos = TipoIncidente.values()
    private val severidades = Severidad.values()
    private val sectores by lazy { RepositorioSeguridad.sectores() }
    private val responsables by lazy { RepositorioSeguridad.responsablesDisponibles() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_incidente)
        prepararRepositorio()
        configurarBarra(R.string.incidente_nuevo)

        enlazarVistas()
        cargarOpciones()
        precargarDesdeAlerta()

        findViewById<MaterialButton>(R.id.botonGuardarIncidente).setOnClickListener { guardar(it) }
        findViewById<MaterialButton>(R.id.botonCancelarIncidente).setOnClickListener { finish() }
    }

    private fun enlazarVistas() {
        campoTitulo = findViewById(R.id.campoTituloIncidente)
        campoTipo = findViewById(R.id.campoTipoIncidente)
        campoSector = findViewById(R.id.campoSectorIncidente)
        campoSeveridad = findViewById(R.id.campoSeveridadIncidente)
        campoDescripcion = findViewById(R.id.campoDescripcionIncidente)
        campoPersonas = findViewById(R.id.campoPersonasIncidente)
        campoResponsable = findViewById(R.id.campoResponsableIncidente)

        entradaTitulo = findViewById(R.id.entradaTituloIncidente)
        entradaTipo = findViewById(R.id.entradaTipoIncidente)
        entradaSector = findViewById(R.id.entradaSectorIncidente)
        entradaSeveridad = findViewById(R.id.entradaSeveridadIncidente)
        entradaDescripcion = findViewById(R.id.entradaDescripcionIncidente)
        entradaPersonas = findViewById(R.id.entradaPersonasIncidente)
        entradaResponsable = findViewById(R.id.entradaResponsableIncidente)
    }

    private fun cargarOpciones() {
        entradaTipo.setSimpleItems(tipos.map { it.etiqueta }.toTypedArray())
        entradaSector.setSimpleItems(sectores.map { it.nombre }.toTypedArray())
        entradaSeveridad.setSimpleItems(severidades.map { it.etiqueta }.toTypedArray())
        entradaResponsable.setSimpleItems(responsables.toTypedArray())
        entradaPersonas.setText("0")
        entradaResponsable.setText(SesionUsuario.nombreUsuario(), false)
    }

    /** Precarga los datos cuando el incidente se origina en una alerta. */
    private fun precargarDesdeAlerta() {
        val titulo = intent.getStringExtra(EXTRA_TITULO) ?: return
        entradaTitulo.setText(titulo)
        entradaDescripcion.setText(intent.getStringExtra(EXTRA_DESCRIPCION).orEmpty())

        intent.getStringExtra(EXTRA_SECTOR)?.let { sectorId ->
            sectores.firstOrNull { it.id == sectorId }?.let {
                entradaSector.setText(it.nombre, false)
            }
        }
        intent.getStringExtra(EXTRA_SEVERIDAD)?.let { nombre ->
            severidades.firstOrNull { it.name == nombre }?.let {
                entradaSeveridad.setText(it.etiqueta, false)
            }
        }
    }

    private fun guardar(vista: View) {
        if (!SesionUsuario.puede(Permiso.REGISTRAR_INCIDENTE)) {
            Formato.aviso(vista, R.string.sin_permiso)
            return
        }

        limpiarErrores()
        var hayErrores = false

        val titulo = entradaTitulo.text?.toString().orEmpty().trim()
        val descripcion = entradaDescripcion.text?.toString().orEmpty().trim()
        val personas = entradaPersonas.text?.toString().orEmpty().trim()

        hayErrores = validar(campoTitulo, Validadores.textoObligatorio(titulo, MIN_TITULO)) || hayErrores
        hayErrores = validar(campoDescripcion, Validadores.textoObligatorio(descripcion, MIN_DESCRIPCION), R.string.form_descripcion_corta) || hayErrores
        hayErrores = validar(campoPersonas, Validadores.enteroEnRango(personas, 0, 999)) || hayErrores

        val tipo = tipos.firstOrNull { it.etiqueta == entradaTipo.text?.toString()?.trim() }
        hayErrores = validar(campoTipo, Validadores.seleccion(tipo?.name)) || hayErrores

        val sector = sectores.firstOrNull { it.nombre == entradaSector.text?.toString()?.trim() }
        hayErrores = validar(campoSector, Validadores.seleccion(sector?.id)) || hayErrores

        val severidad = severidades.firstOrNull { it.etiqueta == entradaSeveridad.text?.toString()?.trim() }
        hayErrores = validar(campoSeveridad, Validadores.seleccion(severidad?.name)) || hayErrores

        val responsable = entradaResponsable.text?.toString().orEmpty().trim()
        hayErrores = validar(campoResponsable, Validadores.seleccion(responsable)) || hayErrores

        if (hayErrores) {
            Formato.aviso(vista, R.string.form_errores)
            return
        }

        RepositorioSeguridad.registrarIncidente(
            titulo = titulo,
            descripcion = descripcion,
            tipo = tipo!!,
            severidad = severidad!!,
            sectorId = sector!!.id,
            personasAfectadas = personas.toInt(),
            responsable = responsable
        )
        setResult(RESULT_OK)
        finish()
    }

    /** Devuelve true si la validacion fallo, y en ese caso pinta el error. */
    private fun validar(
        campo: TextInputLayout,
        resultado: ResultadoValidacion,
        mensajeCorto: Int? = null
    ): Boolean {
        if (resultado is ResultadoValidacion.Invalido) {
            campo.error = mensajeCorto?.let { getString(it) }
                ?: Formato.mensajeDeMotivo(this, resultado.motivo)
            return true
        }
        return false
    }

    private fun limpiarErrores() {
        listOf(
            campoTitulo, campoTipo, campoSector, campoSeveridad,
            campoDescripcion, campoPersonas, campoResponsable
        ).forEach { it.error = null }
    }

    companion object {
        private const val EXTRA_TITULO = "titulo"
        private const val EXTRA_DESCRIPCION = "descripcion"
        private const val EXTRA_SECTOR = "sector"
        private const val EXTRA_SEVERIDAD = "severidad"
        private const val MIN_TITULO = 5
        private const val MIN_DESCRIPCION = 15

        fun intentDesdeAlerta(
            contexto: Context,
            titulo: String,
            descripcion: String,
            sectorId: String,
            severidad: String
        ): Intent = Intent(contexto, NuevoIncidenteActivity::class.java)
            .putExtra(EXTRA_TITULO, titulo)
            .putExtra(EXTRA_DESCRIPCION, descripcion)
            .putExtra(EXTRA_SECTOR, sectorId)
            .putExtra(EXTRA_SEVERIDAD, severidad)
    }
}
