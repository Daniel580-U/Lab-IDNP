package com.unsa.idnp.lab10.work

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.unsa.idnp.lab10.worker.SyncDataWorker
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Funciones de programación de trabajos diferibles y periódicos con WorkManager.
 */
object SyncWorkScheduler {

    const val UNIQUE_PERIODIC_WORK_NAME = "PeriodicSyncWork"
    private const val TAG = "SyncWorkScheduler"

    fun buildSyncConstraints(
        requireNetwork: Boolean,
        requireBatteryNotLow: Boolean
    ): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(
                if (requireNetwork) NetworkType.CONNECTED else NetworkType.NOT_REQUIRED
            )
            .setRequiresBatteryNotLow(requireBatteryNotLow)
            .build()
    }

    fun enqueueOneTimeSync(context: Context, constraints: Constraints): UUID {
        val syncRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(SyncDataWorker.KEY_WORK_MODE to "ONE_TIME"))
            .addTag("sync_one_time")
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
        Log.d(TAG, "OneTimeWorkRequest encolado con ID: ${syncRequest.id}")
        return syncRequest.id
    }

    fun enqueuePeriodicSync(context: Context, constraints: Constraints): UUID {
        val periodicRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .setInputData(workDataOf(SyncDataWorker.KEY_WORK_MODE to "PERIODIC"))
            .addTag("sync_periodic")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
        Log.d(TAG, "PeriodicWorkRequest encolado (intervalo mínimo 15 min) con ID: ${periodicRequest.id}")
        return periodicRequest.id
    }

    fun cancelPeriodicSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_PERIODIC_WORK_NAME)
        Log.d(TAG, "PeriodicWorkRequest cancelado: $UNIQUE_PERIODIC_WORK_NAME")
    }
}
