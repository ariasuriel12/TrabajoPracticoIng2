package com.example.trabajopracticoing2.ui.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.dominio.ResultadoValidacion
import com.example.trabajopracticoing2.dominio.Validadores
import com.example.trabajopracticoing2.model.Rol
import com.example.trabajopracticoing2.model.Usuario
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegistroActivity : AppCompatActivity() {

    private lateinit var campoNombre: TextInputLayout
    private lateinit var campoLegajo: TextInputLayout
    private lateinit var campoClave: TextInputLayout
    private lateinit var campoConfirmarClave: TextInputLayout
    private lateinit var campoRol: TextInputLayout
    private lateinit var campoArea: TextInputLayout

    private lateinit var entradaNombre: TextInputEditText
    private lateinit var entradaLegajo: TextInputEditText
    private lateinit var entradaClave: TextInputEditText
    private lateinit var entradaConfirmarClave: TextInputEditText
    private lateinit var entradaRol: MaterialAutoCompleteTextView
    private lateinit var entradaArea: TextInputEditText

    private val roles = Rol.entries

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        enlazarVistas()
        configurarSelectorDeRol()

        findViewById<MaterialButton>(R.id.botonRegistrar).setOnClickListener {
            registrarUsuario(it)
        }

        findViewById<MaterialButton>(R.id.botonVolverLogin).setOnClickListener {
            finish()
        }
    }

    private fun enlazarVistas() {
        campoNombre = findViewById(R.id.campoNombre)
        campoLegajo = findViewById(R.id.campoLegajo)
        campoClave = findViewById(R.id.campoClave)
        campoConfirmarClave = findViewById(R.id.campoConfirmarClave)
        campoRol = findViewById(R.id.campoRol)
        campoArea = findViewById(R.id.campoArea)

        entradaNombre = findViewById(R.id.entradaNombre)
        entradaLegajo = findViewById(R.id.entradaLegajo)
        entradaClave = findViewById(R.id.entradaClave)
        entradaConfirmarClave = findViewById(R.id.entradaConfirmarClave)
        entradaRol = findViewById(R.id.entradaRol)
        entradaArea = findViewById(R.id.entradaArea)
    }

    private fun configurarSelectorDeRol() {
        entradaRol.setSimpleItems(
            roles.map { it.etiqueta }.toTypedArray()
        )

        entradaRol.setText(
            Rol.RESPONSABLE_SH.etiqueta,
            false
        )
    }

    private fun registrarUsuario(vista: View) {

        limpiarErrores()

        val nombre = entradaNombre.text?.toString().orEmpty().trim()
        val legajo = entradaLegajo.text?.toString().orEmpty().trim()
        val clave = entradaClave.text?.toString().orEmpty()
        val confirmarClave = entradaConfirmarClave.text?.toString().orEmpty()
        val area = entradaArea.text?.toString().orEmpty().trim()

        var hayErrores = false

        if (nombre.isBlank()) {
            campoNombre.error = "Ingresá el nombre"
            hayErrores = true
        }

        val validacionLegajo = Validadores.legajo(legajo)

        if (validacionLegajo is ResultadoValidacion.Invalido) {
            campoLegajo.error =
                Formato.mensajeDeMotivo(this, validacionLegajo.motivo)
            hayErrores = true
        }

        val validacionClave = Validadores.clave(clave)

        if (validacionClave is ResultadoValidacion.Invalido) {
            campoClave.error =
                Formato.mensajeDeMotivo(this, validacionClave.motivo)
            hayErrores = true
        }

        if (clave != confirmarClave) {
            campoConfirmarClave.error = "Las contraseñas no coinciden"
            hayErrores = true
        }

        if (area.isBlank()) {
            campoArea.error = "Ingresá el área"
            hayErrores = true
        }

        if (hayErrores) {
            Formato.aviso(vista, R.string.form_errores)
            return
        }

        val rolSeleccionado = roles.firstOrNull {
            it.etiqueta == entradaRol.text.toString()
        }

        if (rolSeleccionado == null) {
            campoRol.error = "Seleccioná un rol"
            Formato.aviso(vista, R.string.form_errores)
            return
        }

        if (RepositorioSeguridad.existeUsuario(legajo)) {
            campoLegajo.error = "El legajo ya está registrado"
            Formato.aviso(vista, R.string.form_errores)
            return
        }

        val nuevoUsuario = Usuario(
            legajo = legajo,
            nombre = nombre,
            rol = rolSeleccionado,
            area = area,
            clave = clave
        )

        if (!RepositorioSeguridad.registrarUsuario(nuevoUsuario)) {
            campoLegajo.error = "No se pudo registrar el usuario"
            Formato.aviso(vista, R.string.form_errores)
            return
        }

        Formato.aviso(
            vista,
            "Usuario registrado correctamente"
        )

        finish()
    }

    private fun limpiarErrores() {
        campoNombre.error = null
        campoLegajo.error = null
        campoClave.error = null
        campoConfirmarClave.error = null
        campoRol.error = null
        campoArea.error = null
    }
}