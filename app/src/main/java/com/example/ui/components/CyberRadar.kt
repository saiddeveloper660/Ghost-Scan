package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RadarTarget
import com.example.model.TargetCategory
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun CyberRadar(
    activeCategory: TargetCategory?,
    isScanning: Boolean,
    scanProgress: Float,
    statusText: String,
    targets: List<RadarTarget>,
    onTargetClick: (RadarTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColor = activeCategory?.color ?: Color(0xFF22AA55)

    // Pulsing animation for radar target beacons
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xE6010D05))
            .border(1.5.dp, themeColor.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
            .testTag("cyber_radar_view")
    ) {
        val totalWidth = maxWidth
        val totalHeight = maxHeight

        // Canvas for tactical HUD: Grid lines, Concentric Range Circles, Crosshairs, Degree text, and Linear Sweep
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val maxRadius = min(w, h) * 0.46f

            // Tactical Grid lines (38px spacing like original app)
            val gridStep = 42f
            var gx = 0f
            while (gx < w) {
                drawLine(
                    color = Color.White.copy(alpha = 0.045f),
                    start = Offset(gx, 0f),
                    end = Offset(gx, h),
                    strokeWidth = 1f
                )
                gx += gridStep
            }
            var gy = 0f
            while (gy < h) {
                drawLine(
                    color = Color.White.copy(alpha = 0.045f),
                    start = Offset(0f, gy),
                    end = Offset(w, gy),
                    strokeWidth = 1f
                )
                gy += gridStep
            }

            // Concentric Range Rings (25%, 50%, 75%, 100%)
            for (step in 1..4) {
                val r = maxRadius * (step / 4f)
                drawCircle(
                    color = themeColor.copy(alpha = if (step == 4) 0.35f else 0.18f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = if (step == 4) 1.5f else 1f)
                )
            }

            // Crosshairs
            drawLine(
                color = themeColor.copy(alpha = 0.22f),
                start = Offset(cx - maxRadius, cy),
                end = Offset(cx + maxRadius, cy),
                strokeWidth = 1f
            )
            drawLine(
                color = themeColor.copy(alpha = 0.22f),
                start = Offset(cx, cy - maxRadius),
                end = Offset(cx, cy + maxRadius),
                strokeWidth = 1f
            )

            // Center targeting reticle
            drawCircle(
                color = themeColor.copy(alpha = 0.7f),
                radius = 3.5f,
                center = Offset(cx, cy)
            )

            // Tactical corner brackets
            val bracketLen = 22f
            val bracketMargin = 8f
            val bracketColor = themeColor.copy(alpha = 0.9f)
            val strokeW = 2.5f

            // Top-left
            drawLine(bracketColor, Offset(bracketMargin, bracketMargin), Offset(bracketMargin + bracketLen, bracketMargin), strokeW)
            drawLine(bracketColor, Offset(bracketMargin, bracketMargin), Offset(bracketMargin, bracketMargin + bracketLen), strokeW)

            // Top-right
            drawLine(bracketColor, Offset(w - bracketMargin - bracketLen, bracketMargin), Offset(w - bracketMargin, bracketMargin), strokeW)
            drawLine(bracketColor, Offset(w - bracketMargin, bracketMargin), Offset(w - bracketMargin, bracketMargin + bracketLen), strokeW)

            // Bottom-left
            drawLine(bracketColor, Offset(bracketMargin, h - bracketMargin - bracketLen), Offset(bracketMargin, h - bracketMargin), strokeW)
            drawLine(bracketColor, Offset(bracketMargin, h - bracketMargin), Offset(bracketMargin + bracketLen, h - bracketMargin), strokeW)

            // Bottom-right
            drawLine(bracketColor, Offset(w - bracketMargin - bracketLen, h - bracketMargin), Offset(w - bracketMargin, h - bracketMargin), strokeW)
            drawLine(bracketColor, Offset(w - bracketMargin, h - bracketMargin - bracketLen), Offset(w - bracketMargin, h - bracketMargin), strokeW)

            // Dynamic Sweeping Beam (Linear radar scan wave)
            if (isScanning) {
                val sweepX = w * scanProgress
                // Beam gradient trail
                val trailWidth = 50f
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, themeColor.copy(alpha = 0.35f), themeColor),
                        startX = (sweepX - trailWidth).coerceAtLeast(0f),
                        endX = sweepX
                    ),
                    topLeft = Offset((sweepX - trailWidth).coerceAtLeast(0f), 0f),
                    size = androidx.compose.ui.geometry.Size(trailWidth, h)
                )

                // High-intensity leading beam line
                drawLine(
                    color = Color.White,
                    start = Offset(sweepX, 0f),
                    end = Offset(sweepX, h),
                    strokeWidth = 2.5f
                )
            }
        }

        // Target Nodes / Blips placed inside the radar
        targets.forEach { target ->
            val targetColor = target.category.color
            val posX = (totalWidth * target.xPercent)
            val posY = (totalHeight * target.yPercent)

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (posX.toPx() - 36.dp.toPx()).roundToInt(),
                            (posY.toPx() - 16.dp.toPx()).roundToInt()
                        )
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, color = targetColor)
                    ) {
                        onTargetClick(target)
                    }
                    .testTag("radar_target_${target.id}"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Pulsing Dot beacon with outer ripple
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(28.dp)
                    ) {
                        // Expanding halo ring
                        Box(
                            modifier = Modifier
                                .size(24.dp * pulseAnim.value)
                                .clip(CircleShape)
                                .background(targetColor.copy(alpha = (0.55f / pulseAnim.value).coerceIn(0.1f, 0.6f)))
                        )
                        // Core glowing dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(targetColor)
                        )
                    }

                    // Target label badge
                    Text(
                        text = target.label,
                        color = targetColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(Color(0xDD000000), RoundedCornerShape(3.dp))
                            .border(0.8.dp, targetColor.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        // Status bar & Progress indicator at bottom of radar
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    color = themeColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                if (targets.isNotEmpty()) {
                    Text(
                        text = "[${targets.size} NODES ACTIVE]",
                        color = themeColor.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Radar scan progress bar
            if (isScanning) {
                LinearProgressIndicator(
                    progress = { scanProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = themeColor,
                    trackColor = themeColor.copy(alpha = 0.15f)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(themeColor.copy(alpha = 0.3f))
                )
            }
        }
    }
}
