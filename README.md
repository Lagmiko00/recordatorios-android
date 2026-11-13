# Recordatorios

Actividad 2 de Programación Mobile.

La app muestra los recordatorios, permite agregar uno y abre el detalle al tocar una tarjeta. El formulario pide título, categoría y prioridad. La descripción es opcional. Los registros nuevos duran mientras la app está en memoria.

## Cómo abrirla

Abre la carpeta en Android Studio con el SDK 36 y el JDK incluido en el programa. Espera la sincronización de Gradle, selecciona un emulador o un teléfono y ejecuta el módulo `app`. El APK de pruebas puede instalarse junto a la versión de la Actividad 1.

## Cómo está organizada

- `modelo` contiene los recordatorios, las categorías, las prioridades y los estados.
- `datos` contiene los ejemplos y el repositorio en memoria.
- `presentacion` contiene un ViewModel y un UiState por pantalla.
- `ui` contiene las pantallas, los componentes compartidos y la navegación.

La app parte en `lista`. El formulario está en `crear` y el detalle en `detalle/{id}`, donde `id` es el identificador numérico del recordatorio.

## Probar los estados

Con el APK debug instalado y un emulador seleccionado, puedes iniciar la app con `adb`. Cambia `vacio` por `error_carga` o `error_guardado` para simular un fallo en el primer intento. El siguiente intento funciona. Omite `--es escenario vacio` para abrir el listado normal.

```powershell
adb shell am start -S -n cl.curso.recordatorios.actividad2/cl.curso.recordatorios.MainActivity --es escenario vacio
```
