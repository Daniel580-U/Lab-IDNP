package com.unsa.idnp.lab10.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Worker encargado de ejecutar una tarea diferible de sincronización y mantenimiento
 * en segundo plano, registrando marcas de tiempo observables en Logcat y retornando
 * datos de salida para la UI.
 */
class SyncDataWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "SyncDataWorker"
        const val KEY_TIMESTAMP = "sync_timestamp"
        const val KEY_STATUS_MSG = "sync_status_msg"
        const val KEY_WORK_MODE = "work_mode"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val mode = inputData.getString(KEY_WORK_MODE) ?: "ONE_TIME"
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val startTime = formatter.format(Date())

            Log.d(TAG, "[$mode] Iniciando sincronización en segundo plano - Fecha: $startTime (Hilo: ${Thread.currentThread().name})")

            // Simulación de operación de sincronización / mantenimiento fuera del hilo principal
            delay(2000L)

            val endTime = formatter.format(Date())
            Log.d(TAG, "[$mode] Sincronización completada exitosamente - Fecha: $endTime")

            val outputData = workDataOf(
                KEY_TIMESTAMP to endTime,
                KEY_STATUS_MSG to "Registros sincronizados correctamente ($endTime)"
            )
            Result.success(outputData)
        } catch (e: Exception) {
            Log.e(TAG, "Error durante la ejecución del Worker: ${e.message}", e)
            Result.retry()
        }
    }
}
