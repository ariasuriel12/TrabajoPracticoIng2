package com.example.trabajopracticoing2.data

import com.example.trabajopracticoing2.model.Permiso
import com.example.trabajopracticoing2.model.Rol
import com.example.trabajopracticoing2.model.Usuario

/**
 * Sesion en memoria del usuario autenticado.
 *
 * En este prototipo la autenticacion es de demostracion: el login y la navegacion
 * siguen existiendo, pero no hay validacion real ni persistencia ni backend.
 */
object SesionUsuario {

    private const val LEGAJO_DEMO = "1001"
    private const val CLAVE_DEMO = "1234"
    private const val MODO_DEMO = true

    var usuarioActual: Usuario? = null
        private set

    fun credencialesValidas(legajo: String, clave: String): Boolean =
        if (MODO_DEMO) {
            legajo.isNotBlank() && clave.isNotBlank()
        } else {
            legajo == LEGAJO_DEMO && clave == CLAVE_DEMO
        }

    fun iniciarSesion(legajo: String, rol: Rol): Usuario {
        val usuario = Usuario(
            legajo = legajo,
            nombre = nombreSegunRol(rol),
            rol = rol,
            area = "Seguridad e Higiene - Planta Valentín Alsina"
        )
        usuarioActual = usuario
        return usuario
    }

    fun cerrarSesion() {
        usuarioActual = null
    }

    fun nombreUsuario(): String = usuarioActual?.nombre ?: "Usuario"

    fun rolUsuario(): Rol = usuarioActual?.rol ?: Rol.AUDITOR

    fun puede(permiso: Permiso): Boolean = usuarioActual?.rol?.puede(permiso) ?: false

    private fun nombreSegunRol(rol: Rol): String = when (rol) {
        Rol.RESPONSABLE_SH -> "Marcela Ferreyra"
        Rol.TECNICO_SH -> "Diego Quiroga"
        Rol.SUPERVISOR_PLANTA -> "Hernán Suárez"
        Rol.AUDITOR -> "Lucía Bentancur"
    }
}
