package com.boon4681.android_student_life.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.boon4681.android_student_life.R
import com.boon4681.android_student_life.sensor.StepCounterManager
import com.boon4681.android_student_life.ui.animation.LoadingAnimation
import com.boon4681.android_student_life.ui.sensor.STEP_GOAL
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onCameraClick: () -> Unit,
    onMusicClick: () -> Unit,
    onVideoClick: () -> Unit,
    onSensorClick: () -> Unit
) {
    val context = LocalContext.current

    val manager = remember { StepCounterManager(context) }

    val canReadSteps =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED

    DisposableEffect(canReadSteps) {
        if (canReadSteps) {
            manager.start()
        }
        onDispose {
            manager.stop()
        }
    }

    var started by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        started = true
        delay(2000)
        loading = false
    }

    var showMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = started,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -it }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WavingHand,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Hello, Student",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        StepCard(steps = manager.steps, started = started)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { showMessage = !showMessage }) {
                Text(if (showMessage) "Hide Message" else "Show Message")
            }
        }

        AnimatedVisibility(
            visible = showMessage,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    text = "Keep Going!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureTile(Icons.Default.PhotoCamera, "Camera", 0, started, onCameraClick, Modifier.weight(1f))
            FeatureTile(Icons.Default.MusicNote, "Music", 1, started, onMusicClick, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureTile(Icons.Default.Movie, "Video", 2, started, onVideoClick, Modifier.weight(1f))
            FeatureTile(Icons.Default.Sensors, "Sensor", 3, started, onSensorClick, Modifier.weight(1f))
        }

        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingAnimation(
                    rawRes = R.raw.loading,
                    modifier = Modifier.size(120.dp)
                )
                Text("Loading...", style = MaterialTheme.typography.bodyMedium)
            }
        }

        AnimatedVisibility(
            visible = !loading && manager.steps >= 1000,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingAnimation(
                    rawRes = R.raw.walking,
                    modifier = Modifier.size(120.dp)
                )
                Text("Walking", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun StepCard(steps: Int, started: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(false) }

    val cardHeight by animateDpAsState(
        targetValue = if (expanded) 220.dp else 120.dp,
        label = "cardHeight"
    )

    val progress by animateFloatAsState(
        targetValue = if (started) (steps / STEP_GOAL.toFloat()).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(1200),
        label = "progress"
    )

    val transition = updateTransition(targetState = selected, label = "cardTransition")

    val scale by transition.animateFloat(label = "scale") { state ->
        if (state) 1.05f else 1f
    }

    val alpha by transition.animateFloat(label = "alpha") { state ->
        if (state) 1f else 0.85f
    }

    val color by transition.animateColor(label = "color") { state ->
        if (state) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable { selected = !selected },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "%,d Steps".format(steps),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Collapse" else "Expand")
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                trackColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Goal: %,d steps".format(STEP_GOAL))
                    Text("Progress: ${(progress * 100).toInt()}%")
                    Text("Remaining: %,d steps".format((STEP_GOAL - steps).coerceAtLeast(0)))
                    Text(
                        text = "แตะการ์ดเพื่อเลือก (Transition)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureTile(
    icon: ImageVector,
    label: String,
    index: Int,
    started: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(started) {
        if (started) {
            delay(150L * index + 300L)
            visible = true
        }
    }

    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.7f)
        ) {
            ElevatedCard(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
