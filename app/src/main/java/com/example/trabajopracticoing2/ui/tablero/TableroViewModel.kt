package com.example.trabajopracticoing2.ui.tablero

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.model.Alerta
import com.example.trabajopracticoing2.model.ResumenPlanta
import com.example.trabajopracticoing2.model.Sector

/**
 * ViewModel del tablero.
 * Expone el resumen consolidado de la planta, las alertas que requieren
 * atencion y el estado por sector. La vista solo observa y pinta.
 */
class TableroViewModel : ViewModel() {

    private val _resumen = MutableLiveData<ResumenPlanta>()
    val resumen: LiveData<ResumenPlanta> = _resumen

    private val _alertasCriticas = MutableLiveData<List<Alerta>>()
    val alertasCriticas: LiveData<List<Alerta>> = _alertasCriticas

    private val _sectores = MutableLiveData<List<Sector>>()
    val sectores: LiveData<List<Sector>> = _sectores

    /** Recalcula todos los indicadores desde el repositorio. */
    fun refrescar() {
        RepositorioSeguridad.inicializarSiHaceFalta()
        _resumen.value = RepositorioSeguridad.resumen()
        _alertasCriticas.value = RepositorioSeguridad.alertasCriticas()
        _sectores.value = RepositorioSeguridad.sectores()
    }
}
