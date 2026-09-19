package com.example.trabajopracticoing2.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trabajopracticoing2.MainActivity
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioAuditoria
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.dominio.ResultadoValidacion
import com.example.trabajopracticoing2.dominio.Validadores
import com.example.trabajopracticoing2.model.Rol
import com.example.trabajopracticoing2.model.TipoEvento
import com.example.trabajopracticoing2.ui.comun.Formato
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Pantalla inicial del prototipo.
 *
 * Cumple dos requisitos de la consigna:
 * - identificar quien opera el sistema, para poder registrar la trazabilidad;
 * - seleccionar el perfil de acceso, base de las operaciones restringidas.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var campoLegajo: TextInputLayout
    private lateinit var campoClave: TextInputLayout
    private lateinit var campoRol: TextInputLayout
    private lateinit var entradaLegajo: TextInputEditText
    private lateinit var entradaClave: TextInputEditText
    private lateinit var entradaRol: MaterialAutoCompleteTextView

    private val roles = Rol.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        RepositorioSeguridad.inicializarSiHaceFalta()
        aplicarInsets()
        enlazarVistas()
        configurarSelectorDeRol()

        findViewById<MaterialButton>(R.id.botonIngresar).setOnClickListener { intentarIngreso(it) }
    }

    private fun aplicarInsets() {
        val raiz = findViewById<View>(R.id.raizLogin)
        ViewCompat.setOnApplyWindowInsetsListener(raiz) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(0, 0, 0, barras.bottom)
            insets
        }
    }

    private fun enlazarVistas() {
        campoLegajo = findViewById(R.id.campoLegajo)
        campoClave = findViewById(R.id.campoClave)
        campoRol = findViewById(R.id.campoRol)
        entradaLegajo = findViewById(R.id.entradaLegajo)
        entradaClave = findViewById(R.id.entradaClave)
        entradaRol = findViewById(R.id.entradaRol)
    }

    /** El perfil se elige de una lista cerrada: previene errores de carga. */
    private fun configurarSelectorDeRol() {
        entradaRol.setSimpleItems(roles.map { it.etiqueta }.toTypedArray())
        entradaRol.setText(Rol.RESPONSABLE_SH.etiqueta, false)
    }

    /**
     * En este prototipo el ingreso es una demostracion visual.
     * No hay autenticacion real ni backend; solo se acepta la navegacion
     * si los campos basicos tienen contenido y se puede seguir con la app.
     */
    private fun intentarIngreso(vista: View) {
        limpiarErrores()
        var hayErrores = false

        val legajo = entradaLegajo.text?.toString().orEmpty().trim()
        val clave = entradaClave.text?.toString().orEmpty()
        val rolElegido = entradaRol.text?.toString().orEmpty().trim()

        val validacionLegajo = Validadores.legajo(legajo)
        if (validacionLegajo is ResultadoValidacion.Invalido) {
            campoLegajo.error = Formato.mensajeDeMotivo(this, validacionLegajo.motivo)
            hayErrores = true
        }

        val validacionClave = Validadores.clave(clave)
        if (validacionClave is ResultadoValidacion.Invalido) {
            campoClave.error = Formato.mensajeDeMotivo(this, validacionClave.motivo)
            hayErrores = true
        }

        val rol = roles.firstOrNull { it.etiqueta == rolElegido }
        if (rol == null) {
            campoRol.error = getString(R.string.login_error_rol)
            hayErrores = true
        }

        if (hayErrores) {
            Formato.aviso(vista, R.string.form_errores)
            return
        }

        // Demo: entra si el usuario completó los campos; no hay comprobacion real de backend.
        val usuario = SesionUsuario.iniciarSesion(legajo, rol!!)
        RepositorioAuditoria.registrar(
            TipoEvento.ACCESO,
            "SESION",
            "Inicio de sesión demo de ${usuario.nombre} (legajo $legajo)"
        )
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun limpiarErrores() {
        campoLegajo.error = null
        campoClave.error = null
        campoRol.error = null
    }
}
