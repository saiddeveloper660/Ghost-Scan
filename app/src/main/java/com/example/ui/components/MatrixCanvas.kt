package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.model.CyberDataGenerator
import com.example.model.ScanMode
import kotlin.math.sin
import kotlin.random.Random

private val KEYWORDS = listOf(
    "0x", "function", "return", "sudo ", "ssh ", "chmod ", "void ", "0xFF",
    ".exe", "GET /", "SELECT *", "root@", "inject(", "bypass", "&&", "=>",
    "asm", "volatile", "sys_call", "mem_alloc", "xor eax,eax"
)

/**
 * Matrix background canvas with 4 live visualization modes:
 * CODE, NUMBERS, MIXED+ALERT, and GLITCH.
 */
@Composable
fun MatrixCanvas(
    mode: ScanMode,
    isAlertActive: Boolean,
    modifier: Modifier = Modifier
) {
    var frameTimeNanos by remember { mutableLongStateOf(0L) }
    var tick by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { nanos ->
                frameTimeNanos = nanos
                tick++
            }
        }
    }

    // Drops state for matrix rainfall
    val drops = remember { IntArray(120) { Random.nextInt(-40, 0) } }

    // Lines state for upward scrolling code stream
    val codeLines = remember { ArrayDeque<String>() }
    var codeScrollOffset by remember { mutableFloatStateOf(0f) }

    val textPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textSize = 28f
            typeface = Typeface.MONOSPACE
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            if (canvasWidth <= 0f || canvasHeight <= 0f) return@Canvas

            val colWidth = 26f
            val numCols = (canvasWidth / colWidth).toInt() + 1
            if (drops.size < numCols) {
                // Resize drops array if canvas is wider
                // simple in-place logic
            }

            when (mode) {
                ScanMode.CODE, ScanMode.GLITCH -> {
                    // Upward scrolling hacker terminal source code
                    val lineHeight = 30f
                    val maxLines = (canvasHeight / lineHeight).toInt() + 2

                    val speed = if (mode == ScanMode.GLITCH) 4.5f else 2.6f
                    codeScrollOffset += speed

                    if (codeScrollOffset >= lineHeight) {
                        codeScrollOffset = 0f
                        // Generate new line
                        var lineText = ""
                        val charsPerLine = (canvasWidth / 16f).toInt()
                        while (lineText.length < charsPerLine) {
                            if (Random.nextFloat() < 0.35f) {
                                lineText += KEYWORDS.random() + " "
                            } else {
                                lineText += CyberDataGenerator.randomHexStr(Random.nextInt(2, 6)) + " "
                            }
                        }
                        codeLines.addLast(lineText)
                        if (codeLines.size > maxLines) {
                            codeLines.removeFirst()
                        }
                    }

                    // Render lines
                    drawContext.canvas.nativeCanvas.apply {
                        val linesList = codeLines.toList()
                        linesList.forEachIndexed { index, line ->
                            val y = canvasHeight - (linesList.size - index) * lineHeight + lineHeight - codeScrollOffset

                            if (mode == ScanMode.GLITCH) {
                                val hue = ((index * 35 + tick * 3) % 360).toFloat()
                                val colorInt = android.graphics.Color.HSVToColor(floatArrayOf(hue, 0.9f, 0.95f))
                                textPaint.color = colorInt
                                textPaint.alpha = 240
                            } else {
                                val alpha = (60 + (195f * (index.toFloat() / maxLines.coerceAtLeast(1)))).toInt().coerceIn(40, 255)
                                textPaint.color = android.graphics.Color.argb(alpha, 57, 255, 20)
                            }

                            // Glitch horizontal displacement slice
                            val xOffset = if (mode == ScanMode.GLITCH && Random.nextFloat() < 0.08f) {
                                Random.nextInt(-40, 40).toFloat()
                            } else 0f

                            drawText(line, xOffset, y, textPaint)
                        }
                    }
                }

                ScanMode.NUMBERS, ScanMode.MIXED_ALERT -> {
                    // Cascading digital rain
                    val rowHeight = 30f
                    val maxRows = (canvasHeight / rowHeight).toInt()

                    drawContext.canvas.nativeCanvas.apply {
                        for (col in 0 until numCols) {
                            val dropIndex = col % drops.size
                            val currentY = drops[dropIndex]

                            if (currentY > 0) {
                                // Draw column characters above current head
                                val trailLength = 12
                                for (trail in 0 until trailLength) {
                                    val charRow = currentY - trail
                                    if (charRow >= 0 && charRow <= maxRows) {
                                        val charX = col * colWidth
                                        val charY = charRow * rowHeight

                                        val glyph = if (mode == ScanMode.NUMBERS) {
                                            Random.nextInt(0, 10).toString()
                                        } else {
                                            CyberDataGenerator.randomHexStr(1)
                                        }

                                        if (trail == 0) {
                                            // Bright glowing head character
                                            textPaint.color = android.graphics.Color.WHITE
                                            textPaint.alpha = 255
                                        } else {
                                            val fadeAlpha = ((1f - trail.toFloat() / trailLength) * 230).toInt().coerceIn(30, 255)
                                            if (mode == ScanMode.MIXED_ALERT && Random.nextFloat() < 0.16f) {
                                                // Alert red glyph
                                                textPaint.color = android.graphics.Color.argb(fadeAlpha, 255, 59, 59)
                                            } else {
                                                // Authentic neon matrix green
                                                textPaint.color = android.graphics.Color.argb(fadeAlpha, 57, 255, 20)
                                            }
                                        }
                                        drawText(glyph, charX, charY, textPaint)
                                    }
                                }
                            }

                            // Advance rain drop
                            if (tick % 2L == 0L) {
                                drops[dropIndex]++
                                if (drops[dropIndex] * rowHeight > canvasHeight && Random.nextFloat() > 0.965f) {
                                    drops[dropIndex] = Random.nextInt(-25, 0)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Intrusion Detected Strobe Overlay for MIXED+ALERT mode
        if (mode == ScanMode.MIXED_ALERT && isAlertActive) {
            val strobeAnim = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                strobeAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 350, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFF0000).copy(alpha = 0.25f + 0.35f * strobeAnim.value)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xCC000000))
                        .fillMaxSize()
                        .background(Color(0x55FF0000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠ INTRUSION DETECTED ⚠",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp
                    )
                }
            }
        }
    }
}
