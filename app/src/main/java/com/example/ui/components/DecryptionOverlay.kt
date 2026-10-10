package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CyberDataGenerator
import kotlinx.coroutines.delay
import kotlin.random.Random

private val DECRYPT_COLORS = listOf(
    Color(0xFF39FF14), // Neon green
    Color(0xFF00E5FF), // Neon cyan
    Color(0xFFFF3DF2), // Neon magenta
    Color(0xFFFFEA00), // Neon yellow
    Color(0xFFFF3B3B), // Red alert
    Color(0xFFB266FF), // Neon purple
    Color(0xFFFF9D00)  // Neon amber
)

@Composable
fun DecryptionOverlay(
    targetTitle: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        var randomMatrixLines by remember { mutableStateOf(listOf<String>()) }
        var subStatus by remember { mutableStateOf("RESOLVING ENCRYPTION CIPHER…") }

        LaunchedEffect(isVisible) {
            if (!isVisible) return@LaunchedEffect
            val subStatuses = listOf(
                "DECRYPTING SHA-512 HASH…",
                "EXTRACTING SSL HANDSHAKE…",
                "BYPASSING WAF RULES…",
                "PARSING TELEMETRY INTEL…"
            )
            var count = 0
            while (true) {
                // Generate randomized hex matrix streams
                randomMatrixLines = (1..18).map {
                    CyberDataGenerator.randomHexStr(36)
                }
                subStatus = subStatuses[(count / 3) % subStatuses.size]
                count++
                delay(65)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF0000000))
                .testTag("decryption_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Rapid background hex stream
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                randomMatrixLines.forEachIndexed { i, line ->
                    val color = DECRYPT_COLORS[(i + (line.hashCode().coerceAtLeast(0))) % DECRYPT_COLORS.size]
                    Text(
                        text = line,
                        color = color.copy(alpha = 0.5f + (i % 3) * 0.2f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }

            // Central Decrypting Banner
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .background(Color(0xF5020A05), RoundedCornerShape(8.dp))
                    .border(1.5.dp, Color(0xFF39FF14), RoundedCornerShape(8.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFF39FF14),
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "DECRYPTING $targetTitle",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subStatus,
                        color = Color(0xFF39FF14),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
