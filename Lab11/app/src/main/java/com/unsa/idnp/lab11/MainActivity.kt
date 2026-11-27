package com.unsa.idnp.lab11

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.unsa.idnp.lab11.service.TimerService
import com.unsa.idnp.lab11.ui.theme.Lab11TimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab11TimerTheme {
                val context = LocalContext.current

                val startForegroundTimer = {
                    val intent = Intent(context, TimerService::class.java).apply {
                        action = TimerService.ACTION_START
                    }
                    ContextCompat.startForegroundService(context, intent)
                }

                val stopForegroundTimer = {
                    val intent = Intent(context, TimerService::class.java).apply {
                        action = TimerService.ACTION_STOP
                    }
                    context.startService(intent)
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    if (granted) {
                        startForegroundTimer()
                    }
                }

                Scaffold { innerPadding ->
                    TimerScreen(
                        modifier = Modifier.padding(innerPadding),
                        onStartClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    startForegroundTimer()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                startForegroundTimer()
                            }
                        },
                        onStopClick = stopForegroundTimer
                    )
                }
            }
        }
    }
}

@Composable
fun TimerScreen(
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val elapsed by TimerService.elapsedFlow.collectAsState()
    val isRunning by TimerService.isRunningFlow.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRunning) "Servicio en primer plano activo" else "Temporizador detenido",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = formatElapsed(elapsed),
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onStartClick, enabled = !isRunning) {
                Text("Iniciar Temporizador")
            }
            OutlinedButton(onClick = onStopClick, enabled = isRunning) {
                Text("Detener")
            }
        }
    }
}

private fun formatElapsed(totalSeconds: Long): String {
    val hrs = totalSeconds / 3600
    val mins = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hrs, mins, secs)
}
