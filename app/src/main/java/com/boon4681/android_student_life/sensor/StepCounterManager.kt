package com.boon4681.android_student_life.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlin.math.sqrt

class StepCounterManager(
    private val context: Context
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val hasStepSensor = stepSensor != null

    var steps by mutableIntStateOf(if (stepSensor == null) detectedSteps else 0)
        private set

    var accelX by mutableFloatStateOf(0f)
        private set

    var accelY by mutableFloatStateOf(0f)
        private set

    var accelZ by mutableFloatStateOf(0f)
        private set

    fun start() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                steps = event.values[0].toInt()
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accelX = event.values[0]
                accelY = event.values[1]
                accelZ = event.values[2]
                if (!hasStepSensor) {
                    detectStep(sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ))
                }
            }
        }
    }

    private fun detectStep(magnitude: Float) {
        if (!aboveThreshold && magnitude > STEP_HIGH) {
            aboveThreshold = true
            detectedSteps++
            steps = detectedSteps
        } else if (aboveThreshold && magnitude < STEP_LOW) {
            aboveThreshold = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    companion object {
        private const val STEP_HIGH = 12f
        private const val STEP_LOW = 10f
        private var detectedSteps = 0
        private var aboveThreshold = false
    }
}
