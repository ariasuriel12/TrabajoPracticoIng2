# Aplicación de Seguridad e Higiene

Proyecto realizado para la materia Ingeniería de Software II.

La aplicación consiste en un prototipo Android orientado a la gestión de seguridad e higiene dentro de una planta. En esta etapa se trabajó principalmente sobre las interfaces y la navegación, por lo que la información utilizada es estática y no existe conexión con servicios externos.

## Funcionalidades implementadas

Actualmente el proyecto cuenta con las siguientes vistas:

- Inicio de sesión
- Tablero principal
- Alertas
- Detalle de alerta
- Incidentes
- Detalle de incidente
- Nueva acción correctiva
- Tareas
- Nueva tarea
- Procedimientos
- Detalle de procedimiento
- Sectores
- Detalle de sector
- Auditoría
- Perfil
- Emergencia
- Menú de opciones

Las diferentes secciones se encuentran conectadas mediante la navegación de Android, permitiendo recorrer el flujo de la aplicación.

## Tecnologías y estructura

El proyecto está desarrollado para Android utilizando `Activities` y `Fragments`.

Para esta versión se utilizan datos de prueba cargados en memoria. No se implementó persistencia ni comunicación con un servidor.

La estructura principal del proyecto es:

```text
ui/        Pantallas, activities y fragments
data/      Datos utilizados para las pruebas
model/     Clases que representan la información
res/       Layouts, recursos gráficos, colores y textos
```

Se buscó mantener separadas las distintas partes del proyecto para facilitar futuras modificaciones.

## Navegación

Al iniciar la aplicación se muestra la pantalla de login. El acceso es únicamente demostrativo, por lo que no se realiza una validación contra ningún sistema externo.

Una vez dentro, se accede al menú principal desde donde se pueden consultar:

- Tablero
- Alertas
- Incidentes
- Tareas
- Más opciones

Desde las listas se puede ingresar al detalle de cada elemento y acceder al resto de las secciones disponibles.

## Pantallas principales

### Login

Permite ingresar un legajo, contraseña y seleccionar un perfil. Los datos no se validan contra una cuenta real.

### Tablero

Presenta información general mediante tarjetas y valores de ejemplo, simulando el estado de una planta.

### Alertas e incidentes

Muestran listados de elementos registrados. Cada elemento cuenta con una pantalla de detalle.

### Tareas

Incluye el listado de tareas y una pantalla para crear una nueva tarea.

### Procedimientos y sectores

Permiten consultar procedimientos y sectores de la planta junto con la información correspondiente a cada uno.

### Auditoría

Contiene la interfaz destinada a la consulta de auditorías.

### Perfil

Muestra los datos correspondientes al usuario que ingresó a la aplicación.

### Emergencia

Cuenta con una pantalla específica para situaciones de emergencia, pensada como parte del flujo visual de la aplicación.

## Alcance de esta versión

El proyecto está enfocado en la parte visual y en el recorrido entre las distintas pantallas.

Por el momento no se incluye:

- Autenticación real
- Persistencia de datos
- Base de datos
- Backend
- Consumo de APIs
- Validaciones contra datos reales
- Sincronización de información
- Gestión real de las operaciones

Por este motivo, algunos valores y registros permanecen iguales durante la ejecución.

## Estado del proyecto

La aplicación cuenta con las interfaces principales y la navegación entre ellas. La funcionalidad que depende de datos reales o servicios externos queda pendiente para una etapa posterior.

La estructura actual permite incorporar esa lógica posteriormente sin tener que rehacer las pantallas existentes.

## Ejecución

### Desde Android Studio

1. Abrir el proyecto desde Android Studio.
2. Esperar a que finalice la sincronización de Gradle.
3. Seleccionar un emulador o dispositivo Android.
4. Ejecutar la aplicación.

### Desde línea de comandos

**Iniciar un emulador:**

```bash
~/Android/Sdk/emulator/emulator -avd Pixel_5 -gpu swiftshader_indirect -no-snapshot
```

**Compilar e instalar la aplicación:**

```bash
# Compilar el proyecto
./gradlew build

# Instalar el APK en el emulador/dispositivo
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Ejecutar la aplicación
adb shell am start -n com.example.trabajopracticoing2/.MainActivity
```

## Trabajo futuro

Algunas tareas que pueden incorporarse en próximas etapas son:

- Implementar autenticación.
- Incorporar una base de datos.
- Conectar la aplicación con un backend.
- Agregar validaciones.
- Persistir las acciones realizadas por el usuario.
- Reemplazar los datos de prueba por información real.
