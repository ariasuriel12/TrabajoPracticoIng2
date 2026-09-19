package com.example.trabajopracticoing2.model

import com.example.trabajopracticoing2.R

/**
 * Severidad de una alerta o incidente.
 * Se usa para ordenar la informacion y para resolver el color del semaforo visual.
 */
enum class Severidad(
    val etiqueta: String,
    val peso: Int,
    val colorRes: Int,
    val colorFondoRes: Int
) {
    CRITICA("Crítica", 4, R.color.estado_critico, R.color.estado_critico_suave),
    ALTA("Alta", 3, R.color.estado_alto, R.color.estado_alto_suave),
    MEDIA("Media", 2, R.color.estado_precaucion, R.color.estado_precaucion_suave),
    BAJA("Baja", 1, R.color.estado_normal, R.color.estado_normal_suave)
}

/** Estado operativo global o por sector (semaforo). */
enum class EstadoOperativo(
    val etiqueta: String,
    val descripcion: String,
    val colorRes: Int,
    val colorFondoRes: Int
) {
    NORMAL(
        "Normal",
        "Sin situaciones de riesgo activas.",
        R.color.estado_normal,
        R.color.estado_normal_suave
    ),
    PRECAUCION(
        "Precaución",
        "Hay desvíos que requieren seguimiento.",
        R.color.estado_precaucion,
        R.color.estado_precaucion_suave
    ),
    CRITICO(
        "Crítico",
        "Situación de riesgo que requiere intervención inmediata.",
        R.color.estado_critico,
        R.color.estado_critico_suave
    )
}

/** Operaciones sensibles del sistema, usadas para restringir accesos. */
enum class Permiso {
    VER_TABLERO,
    REGISTRAR_INCIDENTE,
    CERRAR_INCIDENTE,
    RECONOCER_ALERTA,
    ASIGNAR_TAREA,
    COMPLETAR_TAREA,
    REGISTRAR_VERIFICACION,
    DECLARAR_EMERGENCIA,
    VER_AUDITORIA
}

/** Perfiles de acceso identificados en el relevamiento. */
enum class Rol(val etiqueta: String, val permisos: Set<Permiso>) {
    RESPONSABLE_SH(
        "Responsable de Seguridad e Higiene",
        Permiso.values().toSet()
    ),
    TECNICO_SH(
        "Técnico de Seguridad e Higiene",
        setOf(
            Permiso.VER_TABLERO,
            Permiso.REGISTRAR_INCIDENTE,
            Permiso.RECONOCER_ALERTA,
            Permiso.COMPLETAR_TAREA,
            Permiso.REGISTRAR_VERIFICACION
        )
    ),
    SUPERVISOR_PLANTA(
        "Supervisor de planta",
        setOf(
            Permiso.VER_TABLERO,
            Permiso.REGISTRAR_INCIDENTE,
            Permiso.COMPLETAR_TAREA
        )
    ),
    AUDITOR(
        "Auditor interno",
        setOf(Permiso.VER_TABLERO, Permiso.VER_AUDITORIA)
    );

    fun puede(permiso: Permiso): Boolean = permisos.contains(permiso)
}

data class Usuario(
    val legajo: String,
    val nombre: String,
    val rol: Rol,
    val area: String
)

data class Sector(
    val id: String,
    val nombre: String,
    val responsable: String,
    val riesgoPrincipal: String,
    val estado: EstadoOperativo
)

enum class TipoAlerta(val etiqueta: String) {
    SENSOR("Sensor / telemetría"),
    VENCIMIENTO("Vencimiento"),
    INCUMPLIMIENTO("Incumplimiento de procedimiento"),
    CAPACITACION("Capacitación"),
    AMBIENTAL("Condición ambiental")
}

data class Alerta(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoAlerta,
    val severidad: Severidad,
    val sectorId: String,
    val hace: String,
    val requiereRespuestaInmediata: Boolean,
    var reconocida: Boolean = false,
    var reconocidaPor: String? = null
)

enum class TipoIncidente(val etiqueta: String) {
    DERRAME("Derrame de sustancia"),
    INCENDIO("Principio de incendio"),
    LESION("Lesión de persona"),
    CASI_INCIDENTE("Casi-incidente"),
    EQUIPO("Falla de equipo"),
    OTRO("Otro")
}

enum class EstadoIncidente(val etiqueta: String, val colorRes: Int, val colorFondoRes: Int) {
    ABIERTO("Abierto", R.color.estado_critico, R.color.estado_critico_suave),
    EN_INVESTIGACION("En investigación", R.color.estado_alto, R.color.estado_alto_suave),
    EN_ACCION("En acción correctiva", R.color.estado_precaucion, R.color.estado_precaucion_suave),
    CERRADO("Cerrado", R.color.estado_normal, R.color.estado_normal_suave)
}

enum class EstadoAccion(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_CURSO("En curso"),
    COMPLETADA("Completada")
}

data class AccionCorrectiva(
    val id: String,
    val descripcion: String,
    val responsable: String,
    var estado: EstadoAccion
)

data class Incidente(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoIncidente,
    val severidad: Severidad,
    val sectorId: String,
    val fecha: String,
    val personasAfectadas: Int,
    val reportadoPor: String,
    var responsable: String,
    var estado: EstadoIncidente,
    val acciones: MutableList<AccionCorrectiva> = mutableListOf()
) {
    val accionesPendientes: Int
        get() = acciones.count { it.estado != EstadoAccion.COMPLETADA }
}

enum class EstadoTarea(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_CURSO("En curso"),
    COMPLETADA("Completada")
}

data class Tarea(
    val id: String,
    val titulo: String,
    val detalle: String,
    val responsable: String,
    val sectorId: String,
    /** Dias hasta el vencimiento. Negativo significa vencida. */
    val diasParaVencer: Int,
    val severidad: Severidad,
    var estado: EstadoTarea
) {
    val vencida: Boolean
        get() = estado != EstadoTarea.COMPLETADA && diasParaVencer < 0

    val textoVencimiento: String
        get() = when {
            estado == EstadoTarea.COMPLETADA -> "Completada"
            diasParaVencer < 0 -> "Vencida hace ${-diasParaVencer} día(s)"
            diasParaVencer == 0 -> "Vence hoy"
            else -> "Vence en $diasParaVencer día(s)"
        }
}

data class PuntoControl(
    val descripcion: String,
    var cumplido: Boolean
)

data class Procedimiento(
    val id: String,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val sectorId: String,
    val ultimaVerificacion: String,
    val puntos: MutableList<PuntoControl> = mutableListOf()
) {
    val cumplimiento: Int
        get() = if (puntos.isEmpty()) 0 else puntos.count { it.cumplido } * 100 / puntos.size

    val estado: EstadoOperativo
        get() = when {
            cumplimiento >= 85 -> EstadoOperativo.NORMAL
            cumplimiento >= 60 -> EstadoOperativo.PRECAUCION
            else -> EstadoOperativo.CRITICO
        }
}

enum class TipoEvento(val etiqueta: String) {
    ACCESO("Acceso"),
    ALTA("Alta de registro"),
    MODIFICACION("Modificación"),
    CIERRE("Cierre"),
    RECONOCIMIENTO("Reconocimiento de alerta"),
    VERIFICACION("Verificación"),
    EMERGENCIA("Emergencia"),
    RECHAZO("Operación rechazada")
}

/** Registro de auditoria: quien, cuando, que operacion y sobre que entidad. */
data class RegistroAuditoria(
    val id: String,
    val usuario: String,
    val rol: String,
    val fechaHora: String,
    val tipo: TipoEvento,
    val entidad: String,
    val detalle: String
)

/** Paso de un protocolo de emergencia. */
data class PasoProtocolo(
    val orden: Int,
    val descripcion: String
)

data class ProtocoloEmergencia(
    val id: String,
    val nombre: String,
    val tiempoObjetivo: String,
    val pasos: List<PasoProtocolo>
)

data class ContactoEmergencia(
    val nombre: String,
    val rol: String,
    val telefono: String
)

/** Resumen calculado que alimenta el tablero. */
data class ResumenPlanta(
    val estado: EstadoOperativo,
    val mensaje: String,
    val alertasActivas: Int,
    val alertasCriticas: Int,
    val incidentesAbiertos: Int,
    val tareasVencidas: Int,
    val cumplimientoPromedio: Int,
    val diasSinAccidentes: Int
)
