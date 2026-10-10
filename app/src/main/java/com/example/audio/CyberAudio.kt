package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Procedural retro-cyber synthesizer and haptics engine for GHOST//SCAN.
 * Generates authentic sci-fi sound effects dynamically using AudioTrack.
 */
class CyberAudio(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    /**
     * Short blip beep for radar targets, button clicks, and terminal telemetry.
     */
    fun playBlip(frequency: Float = 880f, durationMs: Int = 45) {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequency, durationMs, envelope = true)
        }
    }

    /**
     * Frequency sweep chirp for radar line sweeps.
     */
    fun playSweepChirp() {
        if (!soundEnabled) return
        scope.launch {
            val numSamples = (sampleRate * 0.12).toInt()
            val samples = ShortArray(numSamples)
            val startFreq = 400.0
            val endFreq = 1200.0

            var phase = 0.0
            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * t
                phase += 2.0 * Math.PI * currentFreq / sampleRate
                val env = 1.0 - t
                val sample = (sin(phase) * 0.3 * env * Short.MAX_VALUE).toInt()
                samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawPcm(samples)
        }
    }

    /**
     * Cyber warble alarm siren for MIXED+ALERT intrusion detected state.
     */
    fun playAlarm() {
        if (!soundEnabled) return
        scope.launch {
            val numSamples = (sampleRate * 0.45).toInt()
            val samples = ShortArray(numSamples)
            var phase = 0.0
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 800.0 + 350.0 * sin(2.0 * Math.PI * 8.0 * t)
                phase += 2.0 * Math.PI * freq / sampleRate
                val sample = (sin(phase) * 0.45 * Short.MAX_VALUE).toInt()
                samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawPcm(samples)
        }
    }

    /**
     * High-speed matrix decryption arpeggio / cyber chatter.
     */
    fun playDecryptionBurst() {
        if (!soundEnabled) return
        scope.launch {
            val tones = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f, 1318.51f, 987.77f)
            for (freq in tones) {
                playTone(freq, durationMs = 28, envelope = true)
            }
        }
    }

    /**
     * Tactile keystroke cyber sound.
     */
    fun playKeyTap() {
        if (!soundEnabled) return
        scope.launch {
            playTone(1400f, durationMs = 15, envelope = true)
        }
    }

    private fun playTone(frequency: Float, durationMs: Int, envelope: Boolean) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        if (numSamples <= 0) return
        val samples = ShortArray(numSamples)
        val dPhase = 2.0 * Math.PI * frequency / sampleRate
        var phase = 0.0

        for (i in 0 until numSamples) {
            val env = if (envelope) {
                val t = i.toDouble() / numSamples
                if (t < 0.1) t * 10 else 1.0 - (t - 0.1) / 0.9
            } else 1.0

            val sample = (sin(phase) * 0.35 * env * Short.MAX_VALUE).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            phase += dPhase
        }
        playRawPcm(samples)
    }

    private fun playRawPcm(samples: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            // release after completion
            Thread.sleep((samples.size.toDouble() / sampleRate * 1000).toLong() + 20)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // AudioTrack failure handled safely
        }
    }

    /**
     * Haptic feedback vibrations
     */
    fun vibrateClick() {
        if (!hapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    fun vibrateTargetFound() {
        if (!hapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 45), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    fun vibrateAlert() {
        if (!hapticsEnabled || vibrator?.hasVibrator() != true) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 80, 120, 80, 140), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(250)
            }
        } catch (_: Exception) {}
    }
}
