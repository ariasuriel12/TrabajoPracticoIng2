package com.example.trabajopracticoing2

import android.app.Application
import com.example.trabajopracticoing2.data.RepositorioSeguridad

/**
 * Clase Application personalizada para ConurbanFood SGSH.
 *
 * Se encarga de inicializar el repositorio contextual al arranque del proceso
 * de la aplicación, asegurando la disponibilidad de persistencia en SharedPreferences
 * para las tareas antes de que se creen las actividades o fragmentos.
 */
class SeguridadApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RepositorioSeguridad.inicializarConContexto(this)
    }
}
