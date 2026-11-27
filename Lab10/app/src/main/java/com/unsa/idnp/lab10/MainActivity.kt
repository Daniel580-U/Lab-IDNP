package com.unsa.idnp.lab10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.unsa.idnp.lab10.work.SyncWorkScheduler
import com.unsa.idnp.lab10.worker.SyncDataWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                WorkManagerLabScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkManagerLabScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var requireNetwork by remember { mutableStateOf(true) }
    var requireBatteryNotLow by remember { mutableStateOf(true) }
    var activeWorkId by remember { mutableStateOf<UUID?>(null) }
    var activeWorkLabel by remember { mutableStateOf("Ninguno") }
    var immediateUiStatus by remember { mutableStateOf("Sin acción inmediata ejecutada") }

    val workInfoState = activeWorkId?.let { id ->
        WorkManager.getInstance(context)
            .getWorkInfoByIdFlow(id)
            .collectAsStateWithLifecycle(initialValue = null)
            .value
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lab 10 - WorkManager") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Configuración de restricciones (Constraints)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ConstraintToggleRow(
                label = "Requerir conexión a red (NetworkType.CONNECTED)",
                checked = requireNetwork,
                onCheckedChange = { requireNetwork = it }
            )

            ConstraintToggleRow(
                label = "Batería no baja (requiresBatteryNotLow)",
                checked = requireBatteryNotLow,
                onCheckedChange = { requireBatteryNotLow = it }
            )

            HorizontalDivider()

            Text(
                text = "Programación con WorkManager",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    val constraints = SyncWorkScheduler.buildSyncConstraints(
                        requireNetwork = requireNetwork,
                        requireBatteryNotLow = requireBatteryNotLow
                    )
                    activeWorkId = SyncWorkScheduler.enqueueOneTimeSync(context, constraints)
                    activeWorkLabel = "OneTimeWorkRequest"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ejecutar OneTimeWorkRequest")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val constraints = SyncWorkScheduler.buildSyncConstraints(
                            requireNetwork = requireNetwork,
                            requireBatteryNotLow = requireBatteryNotLow
                        )
                        activeWorkId = SyncWorkScheduler.enqueuePeriodicSync(context, constraints)
                        activeWorkLabel = "PeriodicWorkRequest (15 min)"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Programar periódico")
                }

                OutlinedButton(
                    onClick = {
                        SyncWorkScheduler.cancelPeriodicSync(context)
                        activeWorkLabel = "Periódico cancelado"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar periódico")
                }
            }

            WorkStatusCard(
                workLabel = activeWorkLabel,
                workId = activeWorkId,
                workInfo = workInfoState
            )

            HorizontalDivider()

            Text(
                text = "Comparación: operación inmediata en UI",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = {
                    scope.launch {
                        immediateUiStatus = "Guardando registro local en UI..."
                        delay(400L)
                        val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(Date())
                        immediateUiStatus = "Guardado local inmediato completado ($now)"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simular operación inmediata en UI")
            }

            Text(
                text = immediateUiStatus,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tip Logcat: filtra por tag:SyncDataWorker para evidenciar la ejecución en segundo plano.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ConstraintToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun WorkStatusCard(
    workLabel: String,
    workId: UUID?,
    workInfo: WorkInfo?
) {
    val stateLabel = when (workInfo?.state) {
        WorkInfo.State.ENQUEUED -> "ENQUEUED (En cola)"
        WorkInfo.State.RUNNING -> "RUNNING (Ejecutando...)"
        WorkInfo.State.SUCCEEDED -> "SUCCEEDED (Completado)"
        WorkInfo.State.FAILED -> "FAILED (Fallido)"
        WorkInfo.State.BLOCKED -> "BLOCKED (Bloqueado por restricciones)"
        WorkInfo.State.CANCELLED -> "CANCELLED (Cancelado)"
        null -> "Sin trabajo activo"
    }

    val outputMessage = workInfo?.outputData?.getString(SyncDataWorker.KEY_STATUS_MSG)
    val outputTimestamp = workInfo?.outputData?.getString(SyncDataWorker.KEY_TIMESTAMP)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Estado del trabajo",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Tipo: $workLabel", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "ID: ${workId?.toString() ?: "-"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Estado: $stateLabel",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (outputMessage != null) {
                Text(
                    text = "Resultado: $outputMessage",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (outputTimestamp != null) {
                Text(
                    text = "Marca de tiempo: $outputTimestamp",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
