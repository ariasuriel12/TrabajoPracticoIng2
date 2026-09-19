package com.example.trabajopracticoing2.ui.alertas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.Alerta
import com.example.trabajopracticoing2.model.Severidad

/** Criterios de filtrado del listado de alertas. */
enum class FiltroAlertas { TODAS, CRITICAS, ACTIVAS }

/** ViewModel del listado de alertas: aplica el filtro elegido por el usuario. */
class AlertasViewModel : ViewModel() {

    private var filtro = FiltroAlertas.TODAS

    private val _alertas = MutableLiveData<List<Alerta>>()
    val alertas: LiveData<List<Alerta>> = _alertas

    fun aplicarFiltro(nuevo: FiltroAlertas) {
        filtro = nuevo
        refrescar()
    }

    fun refrescar() {
        RepositorioSeguridad.inicializarSiHaceFalta()
        _alertas.value = when (filtro) {
            FiltroAlertas.TODAS -> RepositorioSeguridad.alertas()
            FiltroAlertas.CRITICAS ->
                RepositorioSeguridad.alertas().filter { it.severidad == Severidad.CRITICA }
            FiltroAlertas.ACTIVAS -> RepositorioSeguridad.alertasActivas()
        }
    }
}
