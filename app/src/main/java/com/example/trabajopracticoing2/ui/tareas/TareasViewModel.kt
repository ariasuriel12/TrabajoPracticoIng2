package com.example.trabajopracticoing2.ui.tareas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.EstadoTarea
import com.example.trabajopracticoing2.model.Tarea

/** Criterios de filtrado del listado de tareas. */
enum class FiltroTareas { TODAS, PENDIENTES, VENCIDAS, COMPLETADAS }

/** ViewModel del listado de tareas y pendientes. */
class TareasViewModel : ViewModel() {

    private var filtro = FiltroTareas.TODAS

    private val _tareas = MutableLiveData<List<Tarea>>()
    val tareas: LiveData<List<Tarea>> = _tareas

    fun aplicarFiltro(nuevo: FiltroTareas) {
        filtro = nuevo
        refrescar()
    }

    fun refrescar() {
        RepositorioSeguridad.inicializarSiHaceFalta()
        val todas = RepositorioSeguridad.tareas()
        _tareas.value = when (filtro) {
            FiltroTareas.TODAS -> todas
            FiltroTareas.PENDIENTES -> todas.filter { it.estado != EstadoTarea.COMPLETADA }
            FiltroTareas.VENCIDAS -> todas.filter { it.vencida }
            FiltroTareas.COMPLETADAS -> todas.filter { it.estado == EstadoTarea.COMPLETADA }
        }
    }

    /** Completa una tarea respetando la regla de negocio del repositorio. */
    fun completar(tarea: Tarea): Boolean {
        val resultado = RepositorioSeguridad.completarTarea(tarea.id)
        refrescar()
        return resultado
    }
}
