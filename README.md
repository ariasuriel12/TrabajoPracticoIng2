# Trabajo Práctico - Prototipo de interfaz Android

Este proyecto fue desarrollado como trabajo práctico para la materia de Ingeniería de Software II y está pensado desde una perspectiva de estudiante.

La intención principal no es entregar un sistema completo funcionando en producción, sino crear una base visual y navegable de una aplicación Android. En otras palabras, este proyecto se enfoca en las pantallas, el flujo de usuario y la estructura general de la interfaz.

## Objetivo del proyecto

Construir un prototipo de aplicación para la gestión de seguridad e higiene en una planta industrial.

La app simula una herramienta que permite navegar por distintas secciones como:
- tablero general
- alertas
- incidentes
- tareas
- procedimientos
- sectores
- perfil de usuario
- emergencia

## Alcance real del trabajo

Este proyecto está centrado en la parte visual y de experiencia de usuario.

No se busca dejar una aplicación con funcionalidades reales de negocio, ni una integración completa con backend, base de datos o servicios externos.

Por eso, la app se presenta como un prototipo de interfaz, con datos simulados y navegación básica.

## Qué sí se está dejando

- pantallas bien estructuradas
- flujo de navegación entre secciones
- diseño visual coherente
- uso de componentes de Android
- organización del proyecto en carpetas por responsabilidad
- base para que, en una etapa posterior, se agregue lógica real

## Qué NO se está implementando

Como proyecto académico y de nivel junior, no se pretende incluir todavía:
- login real
- autenticación con backend
- persistencia en base de datos
- conexión con APIs
- validaciones de negocio complejas
- permisos reales de usuario con seguridad real
- trazabilidad real de acciones
- operaciones funcionales completas

## Regla clave del proyecto: UI sin funcionalidad real

El objetivo de este trabajo es dejar la app en una etapa de prototipo visual.

Eso significa que:
- no se asume que el usuario pueda loguearse realmente
- no se persisten ni se modifican datos reales
- no se conectan servicios externos
- no se validan operaciones de negocio en producción
- la navegación es demostrativa, no productiva

La app debe verse como una base para continuar en una segunda etapa, cuando se agregue la lógica del sistema.

## Idea de diseño para mantenerlo escalable

La app está pensada para que la capa visual quede separada de la lógica de negocio.

Esto significa que, más adelante, se puede ir agregando:
1. datos reales
2. repositorios
3. validaciones
4. modelos de dominio más robustos
5. backend o servicios
6. lógica de flujo de usuario real

De esta forma, la interfaz no queda atada a una funcionalidad específica y el proyecto puede crecer sin romper su estructura base.

## Estructura general del proyecto

El proyecto está organizado para ser entendido fácilmente por un estudiante:

- `ui/` : actividades, fragmentos y pantallas
- `data/` : datos de ejemplo o mock data
- `model/` : modelos del dominio
- `dominio/` : validaciones y reglas simples
- `res/` : layouts, drawables, strings, themes y recursos visuales

## Estado del proyecto

Este proyecto se encuentra en etapa de prototipo visual.

La prioridad es mostrar la idea del sistema, la navegación y la organización de la interfaz, más que una aplicación terminada con comportamiento real.

## Cómo correr la app

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar la app en un emulador o dispositivo Android.

## Recomendación para la entrega

La presentación del proyecto debe hacerse como una propuesta visual y navegable, no como un sistema finalizado.

Se debe explicar que la app está hecha para demostrar diseño, estructura y flujo de pantallas, y que la funcionalidad real puede agregarse en etapas posteriores.

## Conclusión

Este trabajo sirve como base para aprender Android, diseño de interfaces y organización de un proyecto. La idea principal es dejar una app clara, ordenada y escalable, con foco en la experiencia visual y en la estructura del flujo de la aplicación, sin asumir todavía una implementación funcional completa.
