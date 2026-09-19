package com.example.trabajopracticoing2.ui.incidentes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.EstadoIncidente
import com.example.trabajopracticoing2.model.Incidente

/** Criterios de filtrado del listado de incidentes. */
enum class FiltroIncidentes { TODOS, ABIERTOS, EN_PROCESO, CERRADOS }

/** ViewModel del listado de incidentes. */
class IncidentesViewModel : ViewModel() {

    private var filtro = FiltroIncidentes.TODOS

    private val _incidentes = MutableLiveData<List<Incidente>>()
    val incidentes: LiveData<List<Incidente>> = _incidentes

    fun aplicarFiltro(nuevo: FiltroIncidentes) {
        filtro = nuevo
        refrescar()
    }

    fun refrescar() {
        RepositorioSeguridad.inicializarSiHaceFalta()
        val todos = RepositorioSeguridad.incidentes()
        _incidentes.value = when (filtro) {
            FiltroIncidentes.TODOS -> todos
            FiltroIncidentes.ABIERTOS -> todos.filter { it.estado == EstadoIncidente.ABIERTO }
            FiltroIncidentes.EN_PROCESO -> todos.filter {
                it.estado == EstadoIncidente.EN_INVESTIGACION || it.estado == EstadoIncidente.EN_ACCION
            }
            FiltroIncidentes.CERRADOS -> todos.filter { it.estado == EstadoIncidente.CERRADO }
        }
    }
}
