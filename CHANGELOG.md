# CHANGELOG

Registro de cambios del proyecto **ConurbanFood SGSH** (TP Integrador — Ingeniería de Software II).

El formato sigue la estructura *Keep a Changelog* (Added / Changed / Fixed / Documentation).

---

## [2026-09-19] — Implementación inicial de la consigna

### Resumen general

El proyecto original era un proyecto Android recién creado desde la plantilla **Empty Views
Activity** (una sola `MainActivity` con un `TextView` de "Hello World!"). Sobre esa base se
implementó el **prototipo navegable completo** del Sistema de Gestión de Seguridad e Higiene que
pide el PDF de la consigna: 18 pantallas, navegación de 5 destinos, formularios con validación,
reglas de negocio, control de acceso por perfil, registro de auditoría y modo emergencia.

Se conservó íntegramente la arquitectura tecnológica original (**Kotlin + Views/XML**, sin Compose)
y se incorporó **MVVM + patrón Repository** sobre ella.

---

### Added

#### Modelo de dominio y capa de datos

- `app/src/main/java/.../model/Modelos.kt` — entidades y enums del dominio: `Severidad`,
  `EstadoOperativo`, `Permiso`, `Rol`, `Usuario`, `Sector`, `TipoAlerta`, `Alerta`, `TipoIncidente`,
  `EstadoIncidente`, `EstadoAccion`, `AccionCorrectiva`, `Incidente`, `EstadoTarea`, `Tarea`,
  `PuntoControl`, `Procedimiento`, `TipoEvento`, `RegistroAuditoria`, `PasoProtocolo`,
  `ProtocoloEmergencia`, `ContactoEmergencia`, `ResumenPlanta`.
  *Cumple*: modelo conceptual (actores, información relevante, reglas, restricciones).
- `app/src/main/java/.../dominio/Validadores.kt` — validaciones de formulario sin dependencias de
  Android (`ResultadoValidacion`, `Motivo`, `Validadores`).
  *Cumple*: validaciones, prevención de errores, y V&V (es testeable con JUnit local).
- `app/src/main/java/.../data/RepositorioSeguridad.kt` — única fuente de datos en memoria y reglas
  de negocio (7 sectores, 6 alertas, 4 incidentes, 7 tareas, 5 procedimientos, 4 protocolos,
  4 contactos) + operaciones (`reconocerAlerta`, `registrarIncidente`, `agregarAccionCorrectiva`,
  `completarAccion`, `puedeCerrarIncidente`, `cerrarIncidente`, `crearTarea`, `completarTarea`,
  `actualizarPuntoControl`, `registrarVerificacion`, `declararEmergencia`) y consultas derivadas
  (`resumen()`, `estadoDeSector()`).
  *Cumple*: modularidad, reglas de negocio, información para la toma de decisiones.
- `app/src/main/java/.../data/RepositorioAuditoria.kt` — registro de trazabilidad (quién, cuándo,
  qué operación, sobre qué entidad).
  *Cumple*: sección **Auditoría** de la consigna.
- `app/src/main/java/.../data/SesionUsuario.kt` — sesión en memoria y verificación centralizada de
  permisos (`puede(permiso)`).
  *Cumple*: operaciones restringidas.

#### Capa común de UI

- `app/src/main/java/.../ui/comun/ActividadBase.kt` — clase base de las pantallas secundarias;
  centraliza barra superior (título, subtítulo, flecha de volver) e inicialización del repositorio.
  *Cumple*: consistencia de interfaz.
- `app/src/main/java/.../ui/comun/Formato.kt` — utilidades de presentación: badges y franjas de
  color según severidad/estado, resolución de colores, traducción de motivos de validación a texto,
  snackbars.
  *Cumple*: consistencia visual y retroalimentación.

#### Pantallas nuevas (18 en total)

| Pantalla | Archivos Kotlin | Layout |
|---|---|---|
| Login | `ui/login/LoginActivity.kt` | `activity_login.xml` |
| Contenedor principal | `MainActivity.kt` | `activity_main.xml` |
| Tablero | `ui/tablero/TableroFragment.kt`, `TableroViewModel.kt` | `fragment_tablero.xml` |
| Alertas | `ui/alertas/AlertasFragment.kt`, `AlertasViewModel.kt`, `AlertaAdapter.kt` | `fragment_alertas.xml`, `item_alerta.xml` |
| Detalle de alerta | `ui/alertas/DetalleAlertaActivity.kt` | `activity_detalle_alerta.xml` |
| Incidentes | `ui/incidentes/IncidentesFragment.kt`, `IncidentesViewModel.kt`, `IncidenteAdapter.kt` | `fragment_incidentes.xml`, `item_incidente.xml` |
| Nuevo incidente | `ui/incidentes/NuevoIncidenteActivity.kt` | `activity_nuevo_incidente.xml` |
| Detalle de incidente | `ui/incidentes/DetalleIncidenteActivity.kt` | `activity_detalle_incidente.xml`, `item_accion.xml` |
| Nueva acción correctiva | `ui/incidentes/NuevaAccionActivity.kt` | `activity_nueva_accion.xml` |
| Tareas | `ui/tareas/TareasFragment.kt`, `TareasViewModel.kt`, `TareaAdapter.kt` | `fragment_tareas.xml`, `item_tarea.xml` |
| Nueva tarea | `ui/tareas/NuevaTareaActivity.kt` | `activity_nueva_tarea.xml` |
| Más | `ui/mas/MasFragment.kt` | `fragment_mas.xml` |
| Procedimientos | `ui/procedimientos/ProcedimientosActivity.kt`, `ProcedimientoAdapter.kt` | `activity_lista_simple.xml`, `item_procedimiento.xml` |
| Detalle de procedimiento | `ui/procedimientos/DetalleProcedimientoActivity.kt` | `activity_detalle_procedimiento.xml`, `item_punto_control.xml` |
| Sectores | `ui/sectores/SectoresActivity.kt`, `SectorAdapter.kt` | `activity_lista_simple.xml`, `item_sector.xml` |
| Detalle de sector | `ui/sectores/DetalleSectorActivity.kt` | `activity_detalle_sector.xml` |
| Auditoría | `ui/auditoria/AuditoriaActivity.kt`, `AuditoriaAdapter.kt` | `activity_lista_simple.xml`, `item_auditoria.xml` |
| Perfil y permisos | `ui/perfil/PerfilActivity.kt` | `activity_perfil.xml`, `item_permiso.xml` |
| Emergencia | `ui/emergencia/EmergenciaActivity.kt`, `ProtocoloAdapter.kt` | `activity_emergencia.xml`, `item_protocolo.xml`, `item_paso.xml`, `item_contacto.xml` |

#### Navegación nueva

- **Barra inferior de 5 destinos** (Tablero · Alertas · Incidentes · Tareas · Más) gestionada por
  `MainActivity` con `FragmentManager` sobre `FragmentContainerView`.
- **Login → MainActivity**: la app arranca en el login; `MainActivity` redirige al login si no hay
  sesión activa.
- **Más → Cerrar sesión → Login**, limpiando la pila de actividades.
- **Tablero → Alertas** mediante el botón *Ver todas*.
- **Tablero → accesos rápidos**: Nuevo incidente, Nueva tarea, Procedimientos, Auditoría.
- **Alerta → Detalle de alerta → Nuevo incidente** (precargado) y **→ Detalle de sector**.
- **Incidente → Detalle → Nueva acción correctiva**.
- **Sector → alertas e incidentes del sector → sus detalles**.
- **Botón flotante EMERGENCIA** accesible desde cualquiera de las 5 pestañas.
- **Barra superior → ícono de persona → Perfil**.

#### Componentes y recursos nuevos

- 7 adaptadores de `RecyclerView`: `AlertaAdapter`, `IncidenteAdapter`, `TareaAdapter`,
  `SectorAdapter`, `ProcedimientoAdapter`, `AuditoriaAdapter`, `ProtocoloAdapter`.
- 4 `ViewModel` con `LiveData`: `TableroViewModel`, `AlertasViewModel`, `IncidentesViewModel`,
  `TareasViewModel`, con sus enums de filtro (`FiltroAlertas`, `FiltroIncidentes`, `FiltroTareas`).
- `res/layout/view_barra.xml` — barra superior reutilizable de las pantallas de detalle.
- `res/layout/view_vacio.xml` — **estado vacío** reutilizable en todos los listados.
- `res/layout/activity_lista_simple.xml` — layout genérico de listado, reutilizado por
  Procedimientos, Sectores y Auditoría.
- `res/menu/menu_navegacion_inferior.xml` y `res/menu/menu_principal.xml`.
- 27 drawables vectoriales propios: íconos (`ic_tablero`, `ic_alerta`, `ic_incidente`, `ic_tarea`,
  `ic_mas`, `ic_emergencia`, `ic_escudo`, `ic_sector`, `ic_procedimiento`, `ic_auditoria`,
  `ic_persona`, `ic_agregar`, `ic_check`, `ic_bloqueado`, `ic_salir`, `ic_filtro`, `ic_reloj`,
  `ic_grafico`, `ic_telefono`, `ic_ubicacion`, `ic_vacio`, `ic_flecha_atras`, `ic_flecha_derecha`)
  y formas (`bg_badge`, `bg_franja`, `bg_circulo`, `bg_login_encabezado`).
- `res/values-night/colors.xml` — paleta para modo oscuro.

#### Pruebas

- `app/src/test/java/.../ValidadoresTest.kt` — **12 casos de prueba** sobre las validaciones:
  situaciones normales, datos inválidos y valores límite del rango.
  *Cumple*: sección **Verificación y Validación** de la consigna.

#### Funcionalidades de negocio

- **Semáforo de estado de planta** calculado (`CRÍTICO` / `PRECAUCIÓN` / `NORMAL`) a partir de las
  alertas activas, las tareas vencidas y el cumplimiento promedio.
- **Estado por sector** derivado de las alertas activas de cada sector.
- **Regla de cierre de incidentes**: no puede cerrarse un incidente con acciones correctivas
  pendientes; el botón se deshabilita y se explica el motivo.
- **Control de acceso por perfil**: 4 roles (`RESPONSABLE_SH`, `TECNICO_SH`, `SUPERVISOR_PLANTA`,
  `AUDITOR`) × 9 permisos, verificados en cada operación sensible.
- **Auditoría automática**: cada operación del repositorio registra su evento; no es posible
  ejecutar una operación sensible sin dejar rastro.
- **Escalamiento alerta → incidente** con precarga del formulario.
- **Confirmación explícita** antes de declarar una emergencia (acción irreversible).

---

### Changed

- **`app/src/main/java/.../MainActivity.kt`** — reescrita por completo. Dejó de ser la pantalla
  "Hello World!" de la plantilla para convertirse en el contenedor de la navegación principal:
  barra superior con menú, `FragmentContainerView`, barra inferior de 5 destinos, botón flotante de
  emergencia, guarda del destino en `onSaveInstanceState` y redirección al login sin sesión.
  *Motivo*: la consigna exige navegación entre pantallas y visión global.
- **`app/src/main/res/layout/activity_main.xml`** — pasó de un `ConstraintLayout` con un único
  `TextView` a un `CoordinatorLayout` con `AppBarLayout` + `MaterialToolbar` +
  `FragmentContainerView` + `BottomNavigationView` + `ExtendedFloatingActionButton`.
- **`app/src/main/AndroidManifest.xml`** — se declararon las **14 activities** y el
  `intent-filter` LAUNCHER se movió de `MainActivity` a `ui.login.LoginActivity`. Se agregó
  `windowSoftInputMode="adjustResize"` en las pantallas con formulario.
  *Motivo*: la pantalla inicial debe identificar al usuario para poder auditar las operaciones.
- **`app/src/main/res/values/colors.xml`** — se agregó la paleta institucional (azules), el
  semáforo de estados (normal / precaución / alto / crítico, cada uno con su variante suave) y los
  colores de superficie, texto y borde.
- **`app/src/main/res/values/strings.xml`** — pasó de 1 string (`app_name`) a **164**, todos los
  textos de la aplicación externalizados (incluidos los formateados con parámetros posicionales).
- **`app/src/main/res/values/themes.xml`** — el tema base pasa a `Theme.Material3.DayNight.NoActionBar`
  con los colores del proyecto, y se agregaron estilos reutilizables: `Texto.Titulo`,
  `Texto.Seccion`, `Texto.Cuerpo`, `Texto.Secundario`, `Tarjeta`, `Etiqueta`.
  *Motivo*: jerarquía tipográfica y consistencia visual entre pantallas.
- **`app/build.gradle.kts`** — se agregaron 5 dependencias (ver abajo).
- **`gradle/libs.versions.toml`** — se agregaron las versiones y los alias de las nuevas
  dependencias.

---

### Dependencias agregadas

Todas mediante el *version catalog* existente, sin cambiar las que ya estaban:

| Alias | Artefacto | Versión | Para qué |
|---|---|---|---|
| `androidx-fragment-ktx` | `androidx.fragment:fragment-ktx` | `1.6.2` | Fragments de la navegación inferior y delegado `by viewModels()` |
| `androidx-lifecycle-viewmodel-ktx` | `androidx.lifecycle:lifecycle-viewmodel-ktx` | `2.6.2` | `ViewModel` (MVVM) |
| `androidx-lifecycle-livedata-ktx` | `androidx.lifecycle:lifecycle-livedata-ktx` | `2.6.2` | `LiveData` observable desde las vistas |
| `androidx-lifecycle-runtime-ktx` | `androidx.lifecycle:lifecycle-runtime-ktx` | `2.6.2` | Ciclo de vida de los observadores |
| `androidx-recyclerview` | `androidx.recyclerview:recyclerview` | `1.3.2` | Todos los listados de la app |

**No** se agregaron Room, Retrofit, Navigation Component, Hilt/Dagger ni Compose: el entregable es
un prototipo navegable y ninguna de esas tecnologías aporta a lo que evalúa la consigna.

---

### Cambios de configuración

- No se modificaron `compileSdk` (37), `minSdk` (24), `targetSdk` (37), la compatibilidad Java (11),
  el AGP (9.4.0), el wrapper de Gradle (9.6.0) ni `settings.gradle.kts`.
- No se modificó `gradle.properties` ni `gradle/gradle-daemon-jvm.properties`.

---

### Fixed

- Se corrigió el uso de las propiedades sintéticas `strokeWidth` / `strokeColor` de
  `MaterialCardView` en `ProtocoloAdapter.kt`, reemplazándolas por las llamadas explícitas
  `setStrokeWidth(...)` / `setStrokeColor(...)`: el setter está sobrecargado y Kotlin no sintetiza
  la propiedad de forma confiable.
- Se reemplazó un `return@TareaAdapter` (retorno etiquetado sobre una lambda pasada al constructor)
  por una estructura `if/else` en `TareasFragment.kt`, para evitar depender de la resolución del
  label implícito.
- Se cambió `PerfilActivity` para que use `item_permiso.xml` en lugar de reutilizar
  `item_punto_control.xml`: el checkbox sugería que la matriz de permisos era editable, cuando es
  informativa. Se agregó `ic_bloqueado.xml` para los permisos denegados.
- Se limpiaron los listeners (`setOnCheckedChangeListener(null)`) antes de fijar el estado en
  `TareaAdapter` y en los checklists, para evitar disparos espurios al reciclar celdas.

---

### Mejoras visuales

- **Jerarquía visual por color**: franja lateral y badge de severidad en cada tarjeta de alerta e
  incidente; el usuario prioriza sin leer el texto completo.
- **Semáforo de estado** con indicador circular en el tablero y en cada sector.
- **Estados vacíos** con ícono y mensaje contextual en los 6 listados.
- **Retroalimentación** por `Snackbar` en todas las operaciones (éxito, rechazo por permisos,
  regla de negocio incumplida).
- **Errores de validación en el propio campo** (`TextInputLayout.error`), no sólo en un aviso global.
- **Barra de progreso de cumplimiento** con color según el estado del procedimiento.
- **Modo emergencia** con jerarquía visual máxima: franja roja, un objetivo por pantalla, pasos
  numerados en orden y contactos siempre visibles sin navegación adicional.
- **Soporte de modo oscuro** mediante `values-night/colors.xml`.

---

### Documentation

- Se creó `README.md` con descripción del proyecto, tecnologías y versiones reales, requisitos,
  pasos de ejecución, problemas habituales, estructura, clases principales, catálogo de pantallas,
  escenario completo para la defensa y estado de implementación.
- Se creó `CHANGELOG.md` (este archivo).
- Se creó `Cambios Ingeniería de Software 2.md` con el detalle técnico archivo por archivo.
- Se creó `prompt modification.md` con los 8 prompts que permiten reproducir estos cambios sobre el
  proyecto original.

---

### Nota sobre la verificación

El entorno donde se generó este entregable **no tiene Android SDK ni acceso a red**, por lo que no
fue posible ejecutar `./gradlew assembleDebug`. Se verificó de forma **estática**: XML bien
formados, ausencia de referencias rotas a recursos, correspondencia entre cada `findViewById(R.id.X)`
y el layout de su pantalla (incluyendo `<include>`), imports internos resolubles y existencia del
archivo `.kt` de cada activity declarada en el manifest.

## [2026-09-19] — Ajuste de flujo de prototipo y autenticación demo

### Summary

Se ajustó el flujo de acceso y navegación para mantener la experiencia visual del prototipo sin asumir una implementación funcional avanzada de autenticación o persistencia.

La aplicación conserva la estructura de login y la navegación entre pantallas, pero se deja como demo de interfaz y flujo de usuario, con datos precargados y comportamiento orientado a la demostración del producto.

### Changed

- Se mantiene la pantalla de login como primer punto de acceso del flujo de la aplicación.
- La autenticación queda en modo de demostración para permitir la navegación del prototipo.
- Se conserva la navegación entre pantallas y actividades del sistema para mostrar el recorrido del usuario.
- Se deja la lógica de negocio y persistencia como no activa, con datos simulados y comportamiento orientado a la presentación visual.
- Se mantiene la estructura del proyecto preparada para continuar con funcionalidad real en etapas posteriores.

### Notes

- La aplicación sigue siendo un prototipo navegable, orientado a la validación de interfaces y flujo de usuario.
- Los cambios están pensados para una base escalable y no para reemplazar la funcionalidad real del sistema en una etapa posterior.
