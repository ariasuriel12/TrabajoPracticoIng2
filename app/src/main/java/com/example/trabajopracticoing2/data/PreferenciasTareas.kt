package com.example.trabajopracticoing2.data

import android.content.Context
import android.content.SharedPreferences
import com.example.trabajopracticoing2.model.EstadoTarea
import com.example.trabajopracticoing2.model.Severidad
import com.example.trabajopracticoing2.model.Tarea
import org.json.JSONArray
import org.json.JSONObject

/**
 * Gestor de persistencia en SharedPreferences para las tareas del sistema.
 *
 * Permite almacenar y recuperar la colección de tareas y el contador de secuencia de IDs
 * en almacenamiento persistente local en formato JSON, sin hardcodear datos iniciales y
 * garantizando que los cambios perduren entre aperturas y cierres de la aplicación.
 */
object PreferenciasTareas {

    private const val NOMBRE_PREFS = "conurban_food_tareas_prefs"
    private const val CLAVE_TAREAS = "clave_lista_tareas"
    private const val CLAVE_SECUENCIA = "clave_secuencia_tareas"

    private fun obtenerPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(NOMBRE_PREFS, Context.MODE_PRIVATE)
    }

    /**
     * Guarda la lista actual de tareas y el último contador de secuencia en SharedPreferences.
     */
    fun guardarTareas(context: Context, tareas: List<Tarea>, secuencia: Int) {
        val jsonArray = JSONArray()
        for (tarea in tareas) {
            val jsonObjeto = JSONObject().apply {
                put("id", tarea.id)
                put("titulo", tarea.titulo)
                put("detalle", tarea.detalle)
                put("responsable", tarea.responsable)
                put("sectorId", tarea.sectorId)
                put("diasParaVencer", tarea.diasParaVencer)
                put("severidad", tarea.severidad.name)
                put("estado", tarea.estado.name)
            }
            jsonArray.put(jsonObjeto)
        }

        obtenerPrefs(context).edit()
            .putString(CLAVE_TAREAS, jsonArray.toString())
            .putInt(CLAVE_SECUENCIA, secuencia)
            .apply()
    }

    /**
     * Carga y deserializa las tareas persistidas en SharedPreferences.
     * Retorna una lista vacía si no hay tareas guardadas.
     */
    fun cargarTareas(context: Context): List<Tarea> {
        val prefs = obtenerPrefs(context)
        val jsonString = prefs.getString(CLAVE_TAREAS, null) ?: return emptyList()

        val lista = mutableListOf<Tarea>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val severidad = try {
                    Severidad.valueOf(item.getString("severidad"))
                } catch (_: Exception) {
                    Severidad.MEDIA
                }
                val estado = try {
                    EstadoTarea.valueOf(item.getString("estado"))
                } catch (_: Exception) {
                    EstadoTarea.PENDIENTE
                }

                lista.add(
                    Tarea(
                        id = item.getString("id"),
                        titulo = item.getString("titulo"),
                        detalle = item.getString("detalle"),
                        responsable = item.getString("responsable"),
                        sectorId = item.getString("sectorId"),
                        diasParaVencer = item.getInt("diasParaVencer"),
                        severidad = severidad,
                        estado = estado
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lista
    }

    /**
     * Recupera el contador de secuencia correlativo para IDs de tareas (ej. T-001, T-002).
     */
    fun cargarSecuencia(context: Context, porDefecto: Int = 0): Int {
        return obtenerPrefs(context).getInt(CLAVE_SECUENCIA, porDefecto)
    }

    /**
     * Limpia las tareas guardadas (útil para pruebas o reinicio controlado).
     */
    fun limpiar(context: Context) {
        obtenerPrefs(context).edit().clear().apply()
    }
}
