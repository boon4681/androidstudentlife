package com.boon4681.android_student_life.ui.sensor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.boon4681.android_student_life.sensor.StepCounterManager

const val STEP_GOAL = 10000

@Composable
fun StepCounterScreen() {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    val manager = remember {
        StepCounterManager(context)
    }

    DisposableEffect(hasPermission) {
        if (hasPermission) {
            manager.start()
        }
        onDispose {
            manager.stop()
        }
    }

    val progress = (manager.steps / STEP_GOAL.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Today's Steps",
            style = MaterialTheme.typography.headlineMedium
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(20.dp)
        )

        Text(
            text = "%,d".format(manager.steps),
            style = MaterialTheme.typography.displayLarge
        )

        Text("steps")

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Today's Activity",
            style = MaterialTheme.typography.titleMedium
        )

        Text("Goal: %,d steps".format(STEP_GOAL))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )

        Text("${(progress * 100).toInt()}%")

        Text(if (progress >= 1f) "Goal reached!" else "Keep Going!")

        if (!hasPermission) {
            Text(
                text = "ต้องอนุญาต Physical activity ก่อนจึงจะนับก้าวได้",
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = { permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION) }) {
                Text("Allow")
            }
        } else if (!manager.hasStepSensor) {
            Text(
                text = "เครื่องนี้ไม่มี Step Counter Sensor จึงนับก้าวจาก Accelerometer แทน",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Accelerometer (m/s²)",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("X: %.2f".format(manager.accelX))
                Text("Y: %.2f".format(manager.accelY))
                Text("Z: %.2f".format(manager.accelZ))
            }
        }
    }
}
