package com.example.trabajopracticoing2.data

import android.content.Context
import com.example.trabajopracticoing2.model.AccionCorrectiva
import com.example.trabajopracticoing2.model.Alerta
import com.example.trabajopracticoing2.model.ContactoEmergencia
import com.example.trabajopracticoing2.model.EstadoAccion
import com.example.trabajopracticoing2.model.EstadoIncidente
import com.example.trabajopracticoing2.model.EstadoOperativo
import com.example.trabajopracticoing2.model.EstadoTarea
import com.example.trabajopracticoing2.model.Incidente
import com.example.trabajopracticoing2.model.PasoProtocolo
import com.example.trabajopracticoing2.model.Procedimiento
import com.example.trabajopracticoing2.model.ProtocoloEmergencia
import com.example.trabajopracticoing2.model.PuntoControl
import com.example.trabajopracticoing2.model.ResumenPlanta
import com.example.trabajopracticoing2.model.Sector
import com.example.trabajopracticoing2.model.Severidad
import com.example.trabajopracticoing2.model.Tarea
import com.example.trabajopracticoing2.model.TipoAlerta
import com.example.trabajopracticoing2.model.TipoEvento
import com.example.trabajopracticoing2.model.TipoIncidente
import com.example.trabajopracticoing2.model.Usuario
import com.example.trabajopracticoing2.model.Rol

/**
 * Unica fuente de datos del prototipo.
 * Mantiene el estado en memoria y concentra las reglas de negocio de consulta y actualizacion.
 * Al ser un prototipo navegable no hay persistencia: los datos se reinician con la aplicacion.
 */
object RepositorioSeguridad {

    private val usuarios = mutableListOf<Usuario>()
    private val sectores = mutableListOf<Sector>()
    private val alertas = mutableListOf<Alerta>()
    private val incidentes = mutableListOf<Incidente>()
    private val tareas = mutableListOf<Tarea>()
    private val procedimientos = mutableListOf<Procedimiento>()
    private val protocolos = mutableListOf<ProtocoloEmergencia>()
    private val contactos = mutableListOf<ContactoEmergencia>()

    private var secuenciaIncidente = 14
    private var secuenciaTarea = 0
    private var secuenciaAccion = 30
    private var diasSinAccidentes = 42
    private var inicializado = false
    private var contextoAplicacion: Context? = null

    // --------------------------------------------------------------- usuarios

    fun usuarios(): List<Usuario> = usuarios.toList()

    fun usuario(legajo: String): Usuario? =
        usuarios.firstOrNull { it.legajo == legajo }

    fun existeUsuario(legajo: String): Boolean =
        usuario(legajo) != null

    fun registrarUsuario(usuario: Usuario): Boolean {
        if (existeUsuario(usuario.legajo)) return false

        usuarios.add(usuario)

        RepositorioAuditoria.registrar(
            TipoEvento.ALTA,
            usuario.legajo,
            "Registró al usuario \"${usuario.nombre}\" con rol ${usuario.rol.etiqueta}"
        )

        return true
    }

    fun validarCredenciales(legajo: String, clave: String): Usuario? {
        return usuarios.firstOrNull {
            it.legajo == legajo && it.clave == clave
        }
    }

    // ---------------------------------------------------------------- carga

    fun inicializarConContexto(context: Context) {
        if (contextoAplicacion == null) {
            contextoAplicacion = context.applicationContext
        }
        inicializarSiHaceFalta(context)
    }

    fun inicializarSiHaceFalta(context: Context? = null) {
        if (context != null && contextoAplicacion == null) {
            contextoAplicacion = context.applicationContext
        }
        if (inicializado) {
            if (tareas.isEmpty() && contextoAplicacion != null) {
                cargarTareasDesdePrefs()
            }
            return
        }
        cargarDatosDemo()
        cargarTareasDesdePrefs()
        inicializado = true
    }

    private fun cargarTareasDesdePrefs() {
        val ctx = contextoAplicacion ?: return
        val guardadas = PreferenciasTareas.cargarTareas(ctx)
        tareas.clear()
        tareas.addAll(guardadas)
        secuenciaTarea = PreferenciasTareas.cargarSecuencia(ctx, guardadas.size)
    }

    private fun guardarTareasEnPrefs() {
        contextoAplicacion?.let { ctx ->
            PreferenciasTareas.guardarTareas(ctx, tareas, secuenciaTarea)
        }
    }

    private fun cargarDatosDemo() {
        //Usuarios
        usuarios.addAll(
            listOf(
                Usuario(
                    legajo = "1001",
                    nombre = "Marcela Ferreyra",
                    rol = Rol.RESPONSABLE_SH,
                    area = "Seguridad e Higiene - Planta Valentín Alsina",
                    clave = "1234"
                ),
                Usuario(
                    legajo = "1002",
                    nombre = "Diego Quiroga",
                    rol = Rol.TECNICO_SH,
                    area = "Seguridad e Higiene - Planta Valentín Alsina",
                    clave = "1234"
                ),
                Usuario(
                    legajo = "1003",
                    nombre = "Hernán Suárez",
                    rol = Rol.SUPERVISOR_PLANTA,
                    area = "Producción - Planta Valentín Alsina",
                    clave = "1234"
                ),
                Usuario(
                    legajo = "1004",
                    nombre = "Lucía Bentancur",
                    rol = Rol.AUDITOR,
                    area = "Auditoría interna",
                    clave = "1234"
                )
            )
        )
        sectores.addAll(
            listOf(
                Sector("S1", "Producción de esencias", "J. Molina", "Exposición a solventes", EstadoOperativo.PRECAUCION),
                Sector("S2", "Depósito de inflamables", "R. Paz", "Incendio / derrame", EstadoOperativo.CRITICO),
                Sector("S3", "Laboratorio de control", "A. Ledesma", "Sustancias corrosivas", EstadoOperativo.PRECAUCION),
                Sector("S4", "Línea de envasado", "C. Ramos", "Atrapamiento con máquina", EstadoOperativo.NORMAL),
                Sector("S5", "Sala de calderas", "M. Ojeda", "Presión y temperatura", EstadoOperativo.CRITICO),
                Sector("S6", "Depósito de materia prima", "S. Acuña", "Apilamiento y carga", EstadoOperativo.NORMAL),
                Sector("S7", "Expedición", "L. Ibarra", "Circulación de autoelevadores", EstadoOperativo.NORMAL)
            )
        )

        alertas.addAll(
            listOf(
                Alerta(
                    id = "AL-101",
                    titulo = "Sensor de gases sin respuesta",
                    descripcion = "El detector de vapores orgánicos de Sala de calderas no reporta lecturas desde hace 18 minutos. No se puede confirmar la atmósfera del sector.",
                    tipo = TipoAlerta.SENSOR,
                    severidad = Severidad.CRITICA,
                    sectorId = "S5",
                    hace = "hace 18 min",
                    requiereRespuestaInmediata = true
                ),
                Alerta(
                    id = "AL-102",
                    titulo = "Derrame de solvente detectado",
                    descripcion = "Se reportó derrame de aproximadamente 20 litros de alcohol isopropílico sobre el pasillo del depósito. Zona señalizada pero sin contención completa.",
                    tipo = TipoAlerta.AMBIENTAL,
                    severidad = Severidad.CRITICA,
                    sectorId = "S2",
                    hace = "hace 42 min",
                    requiereRespuestaInmediata = true
                ),
                Alerta(
                    id = "AL-103",
                    titulo = "12 extintores con carga vencida",
                    descripcion = "El control mensual detectó 12 extintores con vencimiento de carga superado en la línea de envasado.",
                    tipo = TipoAlerta.VENCIMIENTO,
                    severidad = Severidad.ALTA,
                    sectorId = "S4",
                    hace = "hace 3 h",
                    requiereRespuestaInmediata = false
                ),
                Alerta(
                    id = "AL-104",
                    titulo = "Capacitación vencida: 7 operarios",
                    descripcion = "Siete operarios de producción tienen vencida la capacitación de manipulación de sustancias peligrosas.",
                    tipo = TipoAlerta.CAPACITACION,
                    severidad = Severidad.ALTA,
                    sectorId = "S1",
                    hace = "hace 1 día",
                    requiereRespuestaInmediata = false
                ),
                Alerta(
                    id = "AL-105",
                    titulo = "Ducha de emergencia con presión baja",
                    descripcion = "La ducha lavaojos del laboratorio no alcanza el caudal mínimo exigido por procedimiento.",
                    tipo = TipoAlerta.INCUMPLIMIENTO,
                    severidad = Severidad.MEDIA,
                    sectorId = "S3",
                    hace = "hace 2 días",
                    requiereRespuestaInmediata = false
                ),
                Alerta(
                    id = "AL-106",
                    titulo = "Señalización deteriorada",
                    descripcion = "Cartelería de circulación de autoelevadores borrada en el acceso a expedición.",
                    tipo = TipoAlerta.INCUMPLIMIENTO,
                    severidad = Severidad.BAJA,
                    sectorId = "S7",
                    hace = "hace 4 días",
                    requiereRespuestaInmediata = false
                )
            )
        )

        incidentes.addAll(
            listOf(
                Incidente(
                    id = "INC-2026-014",
                    titulo = "Derrame de esencia cítrica en depósito",
                    descripcion = "Durante el traslado de un tambor de 200 L se produjo la rotura del envase y el derrame de aproximadamente 60 litros sobre el piso del depósito de inflamables.",
                    tipo = TipoIncidente.DERRAME,
                    severidad = Severidad.CRITICA,
                    sectorId = "S2",
                    fecha = "18/09/2026 09:40",
                    personasAfectadas = 0,
                    reportadoPor = "R. Paz",
                    responsable = "Marcela Ferreyra",
                    estado = EstadoIncidente.EN_INVESTIGACION,
                    acciones = mutableListOf(
                        AccionCorrectiva("AC-21", "Contener y neutralizar el derrame con kit antiderrame", "Brigada de emergencias", EstadoAccion.COMPLETADA),
                        AccionCorrectiva("AC-22", "Revisar el estado de los tambores del lote 2026-08", "R. Paz", EstadoAccion.EN_CURSO),
                        AccionCorrectiva("AC-23", "Actualizar el procedimiento de traslado de tambores", "Marcela Ferreyra", EstadoAccion.PENDIENTE)
                    )
                ),
                Incidente(
                    id = "INC-2026-013",
                    titulo = "Principio de incendio en tablero eléctrico",
                    descripcion = "Se detectó humo en el tablero secundario de la sala de calderas. Se cortó la energía y se utilizó extintor de CO2.",
                    tipo = TipoIncidente.INCENDIO,
                    severidad = Severidad.CRITICA,
                    sectorId = "S5",
                    fecha = "15/09/2026 17:05",
                    personasAfectadas = 0,
                    reportadoPor = "M. Ojeda",
                    responsable = "Diego Quiroga",
                    estado = EstadoIncidente.EN_ACCION,
                    acciones = mutableListOf(
                        AccionCorrectiva("AC-18", "Termografía de todos los tableros de planta", "Mantenimiento", EstadoAccion.EN_CURSO),
                        AccionCorrectiva("AC-19", "Recambio de contactores del tablero afectado", "Mantenimiento", EstadoAccion.COMPLETADA)
                    )
                ),
                Incidente(
                    id = "INC-2026-012",
                    titulo = "Casi-incidente con autoelevador sin luz de retroceso",
                    descripcion = "Un autoelevador circuló sin baliza ni luz de retroceso y estuvo a punto de impactar a un operario en expedición.",
                    tipo = TipoIncidente.CASI_INCIDENTE,
                    severidad = Severidad.ALTA,
                    sectorId = "S7",
                    fecha = "12/09/2026 11:20",
                    personasAfectadas = 0,
                    reportadoPor = "L. Ibarra",
                    responsable = "Hernán Suárez",
                    estado = EstadoIncidente.ABIERTO
                ),
                Incidente(
                    id = "INC-2026-011",
                    titulo = "Quemadura química leve en laboratorio",
                    descripcion = "Un analista sufrió salpicadura de ácido diluido en el antebrazo. Se aplicó protocolo de lavado y se derivó al servicio médico.",
                    tipo = TipoIncidente.LESION,
                    severidad = Severidad.MEDIA,
                    sectorId = "S3",
                    fecha = "05/09/2026 14:50",
                    personasAfectadas = 1,
                    reportadoPor = "A. Ledesma",
                    responsable = "Marcela Ferreyra",
                    estado = EstadoIncidente.CERRADO,
                    acciones = mutableListOf(
                        AccionCorrectiva("AC-15", "Recapacitación en uso de EPP de laboratorio", "A. Ledesma", EstadoAccion.COMPLETADA),
                        AccionCorrectiva("AC-16", "Reposición de pantallas faciales", "Compras", EstadoAccion.COMPLETADA)
                    )
                )
            )
        )

        // No agregar tareas hardcodeadas aquí. Las tareas las gestiona PreferenciasTareas

        procedimientos.addAll(
            listOf(
                Procedimiento(
                    "P1", "PRO-01", "Manipulación de sustancias inflamables",
                    "Condiciones de traslado, trasvase y almacenamiento de inflamables.",
                    "S2", "16/09/2026",
                    mutableListOf(
                        PuntoControl("Tambores rotulados e identificados", true),
                        PuntoControl("Kit antiderrame completo y accesible", true),
                        PuntoControl("Puesta a tierra en trasvase", false),
                        PuntoControl("Ventilación forzada operativa", true),
                        PuntoControl("Hojas de seguridad disponibles en el sector", false)
                    )
                ),
                Procedimiento(
                    "P2", "PRO-02", "Uso de elementos de protección personal",
                    "EPP obligatorio por sector y control de entrega.",
                    "S4", "17/09/2026",
                    mutableListOf(
                        PuntoControl("Entrega registrada por legajo", true),
                        PuntoControl("EPP en condiciones de uso", true),
                        PuntoControl("Uso efectivo verificado en recorrida", true),
                        PuntoControl("Stock de reposición disponible", false)
                    )
                ),
                Procedimiento(
                    "P3", "PRO-03", "Bloqueo y etiquetado (LOTO)",
                    "Bloqueo de energías peligrosas antes de intervenir equipos.",
                    "S5", "02/09/2026",
                    mutableListOf(
                        PuntoControl("Candados individuales asignados", false),
                        PuntoControl("Tarjetas de bloqueo disponibles", true),
                        PuntoControl("Registro de bloqueos del turno", false),
                        PuntoControl("Personal capacitado en LOTO", false)
                    )
                ),
                Procedimiento(
                    "P4", "PRO-04", "Respuesta ante derrames",
                    "Contención, neutralización y disposición de residuos.",
                    "S2", "18/09/2026",
                    mutableListOf(
                        PuntoControl("Kit antiderrame señalizado", true),
                        PuntoControl("Personal entrenado por turno", true),
                        PuntoControl("Contenedor de residuos peligrosos habilitado", true),
                        PuntoControl("Registro del evento dentro de las 24 h", false)
                    )
                ),
                Procedimiento(
                    "P5", "PRO-05", "Trabajo en altura",
                    "Permisos de trabajo, arnés y puntos de anclaje.",
                    "S6", "28/08/2026",
                    mutableListOf(
                        PuntoControl("Permiso de trabajo emitido", true),
                        PuntoControl("Arnés con inspección vigente", true),
                        PuntoControl("Puntos de anclaje certificados", false)
                    )
                )
            )
        )

        protocolos.addAll(
            listOf(
                ProtocoloEmergencia(
                    "E1", "Derrame de sustancia peligrosa", "Respuesta en menos de 3 minutos",
                    listOf(
                        PasoProtocolo(1, "Alejar al personal y delimitar la zona afectada"),
                        PasoProtocolo(2, "Cortar la fuente del derrame si es seguro hacerlo"),
                        PasoProtocolo(3, "Colocar barreras de contención del kit antiderrame"),
                        PasoProtocolo(4, "Verificar EPP de quien interviene (guantes, máscara, botas)"),
                        PasoProtocolo(5, "Neutralizar y recoger según la hoja de seguridad"),
                        PasoProtocolo(6, "Registrar el incidente y disponer los residuos peligrosos")
                    )
                ),
                ProtocoloEmergencia(
                    "E2", "Principio de incendio", "Respuesta en menos de 2 minutos",
                    listOf(
                        PasoProtocolo(1, "Activar la alarma del sector"),
                        PasoProtocolo(2, "Cortar energía del equipo o tablero afectado"),
                        PasoProtocolo(3, "Atacar el fuego con el extintor adecuado solo si es incipiente"),
                        PasoProtocolo(4, "Evacuar hacia el punto de encuentro si no se controla"),
                        PasoProtocolo(5, "Convocar a bomberos y esperar en el acceso principal")
                    )
                ),
                ProtocoloEmergencia(
                    "E3", "Evacuación general", "Evacuación completa en menos de 6 minutos",
                    listOf(
                        PasoProtocolo(1, "Dar aviso general por sirena y megafonía"),
                        PasoProtocolo(2, "Detener procesos críticos y cerrar válvulas principales"),
                        PasoProtocolo(3, "Evacuar por las vías señalizadas sin usar ascensores"),
                        PasoProtocolo(4, "Realizar el conteo de personal en el punto de encuentro"),
                        PasoProtocolo(5, "Informar faltantes al jefe de emergencia")
                    )
                ),
                ProtocoloEmergencia(
                    "E4", "Accidente con lesión de persona", "Primera respuesta en menos de 2 minutos",
                    listOf(
                        PasoProtocolo(1, "No mover al accidentado salvo riesgo inminente"),
                        PasoProtocolo(2, "Convocar al servicio médico interno"),
                        PasoProtocolo(3, "Aplicar primeros auxilios solo si está capacitado"),
                        PasoProtocolo(4, "Solicitar ambulancia a la ART"),
                        PasoProtocolo(5, "Preservar la escena para la investigación posterior")
                    )
                )
            )
        )

        contactos.addAll(
            listOf(
                ContactoEmergencia("Brigada de emergencias", "Interno de planta", "Int. 120"),
                ContactoEmergencia("Servicio médico", "Enfermería de planta", "Int. 135"),
                ContactoEmergencia("Bomberos Lanús", "Externo", "100"),
                ContactoEmergencia("ART - Emergencias", "Externo", "0800-333-1234")
            )
        )
    }

    // ------------------------------------------------------------- consultas

    fun sectores(): List<Sector> = sectores.toList()

    fun sector(id: String): Sector? = sectores.firstOrNull { it.id == id }

    fun nombreSector(id: String): String = sector(id)?.nombre ?: "Sector sin asignar"

    fun alertas(): List<Alerta> =
        alertas.sortedWith(compareByDescending<Alerta> { it.severidad.peso }.thenBy { it.reconocida })

    fun alertasActivas(): List<Alerta> = alertas().filter { !it.reconocida }

    fun alertasCriticas(): List<Alerta> =
        alertasActivas().filter { it.severidad == Severidad.CRITICA || it.severidad == Severidad.ALTA }

    fun alerta(id: String): Alerta? = alertas.firstOrNull { it.id == id }

    fun alertasDeSector(sectorId: String): List<Alerta> = alertas().filter { it.sectorId == sectorId }

    fun incidentes(): List<Incidente> = incidentes.toList()

    fun incidente(id: String): Incidente? = incidentes.firstOrNull { it.id == id }

    fun incidentesAbiertos(): List<Incidente> = incidentes.filter { it.estado != EstadoIncidente.CERRADO }

    fun incidentesDeSector(sectorId: String): List<Incidente> = incidentes.filter { it.sectorId == sectorId }

    fun tareas(): List<Tarea> = tareas.sortedBy { it.diasParaVencer }

    fun tarea(id: String): Tarea? = tareas.firstOrNull { it.id == id }

    fun tareasVencidas(): List<Tarea> = tareas.filter { it.vencida }

    fun procedimientos(): List<Procedimiento> = procedimientos.sortedBy { it.cumplimiento }

    fun procedimiento(id: String): Procedimiento? = procedimientos.firstOrNull { it.id == id }

    fun protocolos(): List<ProtocoloEmergencia> = protocolos.toList()

    fun protocolo(id: String): ProtocoloEmergencia? = protocolos.firstOrNull { it.id == id }

    fun contactos(): List<ContactoEmergencia> = contactos.toList()

    fun responsablesDisponibles(): List<String> = listOf(
        "Marcela Ferreyra",
        "Diego Quiroga",
        "Hernán Suárez",
        "Mantenimiento",
        "Brigada de emergencias"
    )

    /** Calcula el resumen que se muestra en el tablero. */
    fun resumen(): ResumenPlanta {
        val activas = alertasActivas()
        val criticas = activas.count { it.severidad == Severidad.CRITICA }
        val abiertos = incidentesAbiertos().size
        val vencidas = tareasVencidas().size
        val cumplimiento =
            if (procedimientos.isEmpty()) 0
            else procedimientos.sumOf { it.cumplimiento } / procedimientos.size

        val estado = when {
            criticas > 0 -> EstadoOperativo.CRITICO
            activas.isNotEmpty() || vencidas > 0 || cumplimiento < 80 -> EstadoOperativo.PRECAUCION
            else -> EstadoOperativo.NORMAL
        }

        val mensaje = when (estado) {
            EstadoOperativo.CRITICO ->
                "$criticas alerta(s) crítica(s) sin reconocer requieren intervención inmediata."
            EstadoOperativo.PRECAUCION ->
                "Hay desvíos abiertos con seguimiento pendiente. Sin situaciones críticas activas."
            EstadoOperativo.NORMAL ->
                "Sin alertas activas ni tareas vencidas."
        }

        return ResumenPlanta(
            estado = estado,
            mensaje = mensaje,
            alertasActivas = activas.size,
            alertasCriticas = criticas,
            incidentesAbiertos = abiertos,
            tareasVencidas = vencidas,
            cumplimientoPromedio = cumplimiento,
            diasSinAccidentes = diasSinAccidentes
        )
    }

    /** Estado operativo derivado de las alertas activas del sector. */
    fun estadoDeSector(sectorId: String): EstadoOperativo {
        val activas = alertasActivas().filter { it.sectorId == sectorId }
        return when {
            activas.any { it.severidad == Severidad.CRITICA } -> EstadoOperativo.CRITICO
            activas.isNotEmpty() -> EstadoOperativo.PRECAUCION
            else -> EstadoOperativo.NORMAL
        }
    }

    // ---------------------------------------------------------- operaciones

    fun reconocerAlerta(id: String): Boolean {
        val alerta = alerta(id) ?: return false
        if (alerta.reconocida) return false
        alerta.reconocida = true
        alerta.reconocidaPor = SesionUsuario.nombreUsuario()
        RepositorioAuditoria.registrar(
            TipoEvento.RECONOCIMIENTO,
            alerta.id,
            "Reconoció la alerta \"${alerta.titulo}\" (${alerta.severidad.etiqueta})"
        )
        return true
    }

    fun registrarIncidente(
        titulo: String,
        descripcion: String,
        tipo: TipoIncidente,
        severidad: Severidad,
        sectorId: String,
        personasAfectadas: Int,
        responsable: String
    ): Incidente {
        secuenciaIncidente += 1
        val nuevo = Incidente(
            id = "INC-2026-%03d".format(secuenciaIncidente),
            titulo = titulo,
            descripcion = descripcion,
            tipo = tipo,
            severidad = severidad,
            sectorId = sectorId,
            fecha = "19/09/2026",
            personasAfectadas = personasAfectadas,
            reportadoPor = SesionUsuario.nombreUsuario(),
            responsable = responsable,
            estado = EstadoIncidente.ABIERTO
        )
        incidentes.add(0, nuevo)
        if (personasAfectadas > 0) {
            diasSinAccidentes = 0
        }
        RepositorioAuditoria.registrar(
            TipoEvento.ALTA,
            nuevo.id,
            "Registró el incidente \"$titulo\" en ${nombreSector(sectorId)} (${severidad.etiqueta})"
        )
        return nuevo
    }

    fun agregarAccionCorrectiva(incidenteId: String, descripcion: String, responsable: String): Boolean {
        val incidente = incidente(incidenteId) ?: return false
        secuenciaAccion += 1
        incidente.acciones.add(
            AccionCorrectiva("AC-%02d".format(secuenciaAccion), descripcion, responsable, EstadoAccion.PENDIENTE)
        )
        if (incidente.estado == EstadoIncidente.ABIERTO || incidente.estado == EstadoIncidente.EN_INVESTIGACION) {
            incidente.estado = EstadoIncidente.EN_ACCION
        }
        RepositorioAuditoria.registrar(
            TipoEvento.MODIFICACION,
            incidente.id,
            "Agregó la acción correctiva \"$descripcion\" a cargo de $responsable"
        )
        return true
    }

    fun completarAccion(incidenteId: String, accionId: String): Boolean {
        val incidente = incidente(incidenteId) ?: return false
        val accion = incidente.acciones.firstOrNull { it.id == accionId } ?: return false
        if (accion.estado == EstadoAccion.COMPLETADA) return false
        accion.estado = EstadoAccion.COMPLETADA
        RepositorioAuditoria.registrar(
            TipoEvento.MODIFICACION,
            incidente.id,
            "Marcó como completada la acción \"${accion.descripcion}\""
        )
        return true
    }

    /** Regla de negocio: no se puede cerrar un incidente con acciones correctivas pendientes. */
    fun puedeCerrarIncidente(incidenteId: String): Boolean {
        val incidente = incidente(incidenteId) ?: return false
        return incidente.estado != EstadoIncidente.CERRADO && incidente.accionesPendientes == 0
    }

    fun cerrarIncidente(incidenteId: String): Boolean {
        if (!puedeCerrarIncidente(incidenteId)) return false
        val incidente = incidente(incidenteId) ?: return false
        incidente.estado = EstadoIncidente.CERRADO
        RepositorioAuditoria.registrar(
            TipoEvento.CIERRE,
            incidente.id,
            "Cerró el incidente \"${incidente.titulo}\""
        )
        return true
    }

    fun crearTarea(
        titulo: String,
        detalle: String,
        responsable: String,
        sectorId: String,
        diasParaVencer: Int,
        severidad: Severidad
    ): Tarea {
        secuenciaTarea += 1
        val nueva = Tarea(
            id = "T-%03d".format(secuenciaTarea),
            titulo = titulo,
            detalle = detalle,
            responsable = responsable,
            sectorId = sectorId,
            diasParaVencer = diasParaVencer,
            severidad = severidad,
            estado = EstadoTarea.PENDIENTE
        )
        tareas.add(nueva)
        guardarTareasEnPrefs()
        RepositorioAuditoria.registrar(
            TipoEvento.ALTA,
            nueva.id,
            "Creó la tarea \"$titulo\" y la asignó a $responsable"
        )
        return nueva
    }

    fun completarTarea(id: String): Boolean {
        val tarea = tarea(id) ?: return false
        if (tarea.estado == EstadoTarea.COMPLETADA) return false
        tarea.estado = EstadoTarea.COMPLETADA
        guardarTareasEnPrefs()
        RepositorioAuditoria.registrar(
            TipoEvento.MODIFICACION,
            tarea.id,
            "Marcó como completada la tarea \"${tarea.titulo}\""
        )
        return true
    }

    /** Actualiza una tarea existente y persiste los cambios. */
    fun actualizarTarea(
        id: String,
        titulo: String,
        detalle: String,
        responsable: String,
        sectorId: String,
        diasParaVencer: Int,
        severidad: Severidad
    ): Boolean {
        val t = tarea(id) ?: return false
        val actualizado = Tarea(
            id = t.id,
            titulo = titulo,
            detalle = detalle,
            responsable = responsable,
            sectorId = sectorId,
            diasParaVencer = diasParaVencer,
            severidad = severidad,
            estado = t.estado
        )
        val idx = tareas.indexOfFirst { it.id == id }
        if (idx >= 0) {
            tareas[idx] = actualizado
            guardarTareasEnPrefs()
            RepositorioAuditoria.registrar(
                TipoEvento.MODIFICACION,
                actualizado.id,
                "Actualizó la tarea \"${actualizado.titulo}\""
            )
            return true
        }
        return false
    }

    /** Elimina una tarea por id y persiste los cambios. */
    fun eliminarTarea(id: String): Boolean {
        val tarea = tarea(id) ?: return false
        val eliminado = tareas.remove(tarea)
        if (eliminado) {
            guardarTareasEnPrefs()
            RepositorioAuditoria.registrar(
                TipoEvento.RECHAZO,
                tarea.id,
                "Eliminó la tarea \"${tarea.titulo}\""
            )
        }
        return eliminado
    }

    fun actualizarPuntoControl(procedimientoId: String, indice: Int, cumplido: Boolean): Boolean {
        val procedimiento = procedimiento(procedimientoId) ?: return false
        if (indice !in procedimiento.puntos.indices) return false
        procedimiento.puntos[indice].cumplido = cumplido
        return true
    }

    fun registrarVerificacion(procedimientoId: String): Boolean {
        val procedimiento = procedimiento(procedimientoId) ?: return false
        RepositorioAuditoria.registrar(
            TipoEvento.VERIFICACION,
            procedimiento.codigo,
            "Registró la verificación de \"${procedimiento.nombre}\" con ${procedimiento.cumplimiento}% de cumplimiento"
        )
        return true
    }

    fun declararEmergencia(protocoloId: String, sectorId: String): Boolean {
        val protocolo = protocolo(protocoloId) ?: return false
        RepositorioAuditoria.registrar(
            TipoEvento.EMERGENCIA,
            protocolo.id,
            "Declaró emergencia \"${protocolo.nombre}\" en ${nombreSector(sectorId)}"
        )
        return true
    }
}
