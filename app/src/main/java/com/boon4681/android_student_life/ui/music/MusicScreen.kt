package com.boon4681.android_student_life.ui.music

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.boon4681.android_student_life.R

@Composable
fun MusicScreen() {
    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(false) }

    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.music)
    }

    DisposableEffect(Unit) {
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
        }
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Student Music Player",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(30.dp))

        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(32.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Student Song",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = if (isPlaying) "Playing" else "Stopped",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                    } else {
                        mediaPlayer.start()
                        isPlaying = true
                    }
                }
            ) {
                Text(if (isPlaying) "Pause" else "Play")
            }

            OutlinedButton(
                onClick = {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                    isPlaying = false
                }
            ) {
                Text("Stop")
            }
        }
    }
}
