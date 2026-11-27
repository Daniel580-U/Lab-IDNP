# Lab 10 - Tareas en segundo plano con WorkManager

Proyecto Android (Kotlin + Jetpack Compose) correspondiente a la **Práctica de Laboratorio N° 10** de *Introducción al Desarrollo de Nuevas Plataformas (2026-B)*.

## Estructura principal

- `app/src/main/java/com/unsa/idnp/lab10/worker/SyncDataWorker.kt`: `CoroutineWorker` que ejecuta una tarea de sincronización en `Dispatchers.IO`, registra marcas de tiempo en `Logcat` (`tag:SyncDataWorker`) y retorna `Result.success(outputData)`.
- `app/src/main/java/com/unsa/idnp/lab10/work/SyncWorkScheduler.kt`: Utilidad para construir `Constraints` (`NetworkType.CONNECTED`, `requiresBatteryNotLow`) y encolar `OneTimeWorkRequest` y `PeriodicWorkRequest` (intervalo mínimo de 15 minutos).
- `app/src/main/java/com/unsa/idnp/lab10/MainActivity.kt`: Interfaz en Jetpack Compose que permite programar trabajos, alternar restricciones, observar el `WorkInfo.State` en tiempo real y comparar con una operación inmediata en UI.

## Cómo ejecutar

1. Abrir la carpeta `repo/` en **Android Studio**.
2. Esperar la sincronización de Gradle (el IDE descargará las dependencias de `androidx.work:work-runtime-ktx:2.9.1`).
3. Ejecutar en un emulador o dispositivo físico.
4. Abrir **Logcat** y filtrar por `SyncDataWorker` para observar la ejecución en segundo plano.
5. Abrir **App Inspection > Background Task Inspector** para inspeccionar el estado y restricciones de los trabajos.

## Cuándo elegir WorkManager frente a una operación inmediata en la UI

- **Operación inmediata (corrutina en UI / `ViewModel`):** Ideal para acciones que el usuario espera ver al instante mientras la pantalla está activa (ej. guardar un registro local o cargar una lista).
- **WorkManager:** Ideal para trabajo diferible que debe garantizarse incluso si el usuario cierra la aplicación o reinicia el dispositivo (ej. sincronización de registros con servidor remoto bajo restricciones de red y batería).
