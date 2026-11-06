# Recordatorios

Actividad 1 de Programación Mobile.

Es una aplicación de recordatorios hecha con Kotlin y Jetpack Compose. Muestra una lista de tareas con su categoría, prioridad y estado. Se puede filtrar por prioridad o por tareas pendientes y completadas.

Por ahora usa seis tareas de ejemplo que están en el código. Todavía no guarda los cambios ni se conecta a internet.

## Cómo abrirla

Abrir la carpeta en Android Studio y esperar que termine la sincronización de Gradle. Se necesita el SDK de Android 36 y el JDK incluido en Android Studio. Después se elige un emulador o un teléfono y se ejecuta `app`.

## Archivos principales

- `Recordatorio.kt`: los modelos y las validaciones.
- `DatosSimulados.kt`: las tareas de ejemplo, las colecciones y los filtros.
- `PantallaRecordatorios.kt`: la pantalla de la lista.
- `MainActivity.kt`: el inicio de la aplicación.

También hay pruebas para las validaciones, los filtros y la pantalla. La aplicación se probó en un emulador con Android 16.
