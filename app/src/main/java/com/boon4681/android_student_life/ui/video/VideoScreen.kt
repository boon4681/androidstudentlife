package com.boon4681.android_student_life.ui.video

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.boon4681.android_student_life.R

@Composable
fun VideoScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Campus Video",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AndroidView(
            factory = {
                VideoView(context).apply {
                    val uri = Uri.parse(
                        "android.resource://" +
                            context.packageName +
                            "/" +
                            R.raw.campus
                    )

                    setVideoURI(uri)

                    setMediaController(
                        MediaController(context).also {
                            it.setAnchorView(this)
                        }
                    )

                    start()
                }
            },
            onRelease = { it.stopPlayback() },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )

        Text(
            text = "แตะที่วิดีโอเพื่อแสดงปุ่มควบคุม",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
