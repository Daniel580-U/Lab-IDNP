# Lab 11 - Servicio en Primer Plano y Notificación Persistente con Temporizador

Proyecto Android (Kotlin + Jetpack Compose) correspondiente a la **Práctica de Laboratorio N° 11** de **Introducción al Desarrollo de Nuevas Plataformas (IDNP)** — UNSA 2026-B.

## Funcionalidades implementadas
- **Servicio en primer plano (`TimerService`):** Declarado en `AndroidManifest.xml` con `foregroundServiceType="specialUse"` y permisos `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE` y `POST_NOTIFICATIONS`.
- **Canal de notificación (`NotificationChannel`):** Configurado con `IMPORTANCE_LOW` para actualizaciones silenciosas cada segundo.
- **Notificación persistente:** Muestra el título `"Temporizador en ejecución"`, el tiempo transcurrido en formato `HH:MM:SS`, `setOngoing(true)` y un botón de acción **Detener**.
- **Control seguro de ciclo de vida:** Inicio desde la interfaz (con solicitud de permiso `POST_NOTIFICATIONS` en Android 13+), continuidad en segundo plano y detención limpia (`timerJob?.cancel()`, `stopForeground(STOP_FOREGROUND_REMOVE)`, `stopSelf()`).

## Checklist de versión para publicación
- [x] **Nombre de la app:** `Lab11 Temporizador` (`res/values/strings.xml`)
- [x] **Icono:** Adaptive icon (`ic_launcher` / `ic_launcher_round`)
- [x] **Versión:** `versionCode = 1`, `versionName = "1.0.0"` (`app/build.gradle.kts`)
- [x] **Pruebas finales:** Inicio, segundo plano y detención verificados
- [x] **Repositorio limpio:** `.gitignore` configurado sin artefactos de compilación
