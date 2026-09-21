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

    var usuarioActual: Usuario? = null
        private set

    fun iniciarSesion(usuario: Usuario): Usuario {
        usuarioActual = usuario
        return usuario
    }

    fun cerrarSesion() {
        usuarioActual = null
    }

    fun nombreUsuario(): String =
        usuarioActual?.nombre ?: "Usuario"

    fun rolUsuario(): Rol =
        usuarioActual?.rol ?: Rol.AUDITOR

    fun puede(permiso: Permiso): Boolean =
        usuarioActual?.rol?.puede(permiso) ?: false
}
