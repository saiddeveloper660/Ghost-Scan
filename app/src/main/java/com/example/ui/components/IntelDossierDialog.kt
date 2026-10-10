package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PanelPosition
import com.example.model.TargetIntelData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IntelDossierDialog(
    data: TargetIntelData?,
    position: PanelPosition,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (data == null) return
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val themeColor = data.category?.color ?: Color(0xFF00E5FF)
    var consoleOutput by remember { mutableStateOf<String?>(null) }
    var isExecutingAction by remember { mutableStateOf(false) }

    val alignment = when (position) {
        PanelPosition.CENTER -> Alignment.Center
        PanelPosition.LEFT -> Alignment.CenterStart
        PanelPosition.RIGHT -> Alignment.CenterEnd
        PanelPosition.TOP -> Alignment.TopCenter
        PanelPosition.BOTTOM -> Alignment.BottomCenter
        PanelPosition.RANDOM -> listOf(
            Alignment.Center,
            Alignment.TopCenter,
            Alignment.BottomCenter
        ).random()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xB3000000))
            .clickable(onClick = onDismiss),
        contentAlignment = alignment
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth(0.92f)
                .clickable(enabled = false) {} // Prevent click-through dismiss
                .shadow(20.dp, spotColor = themeColor, ambientColor = themeColor)
                .border(1.5.dp, themeColor, RoundedCornerShape(8.dp))
                .testTag("intel_dossier_panel"),
            colors = CardDefaults.cardColors(containerColor = Color(0xF8010A04)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Title and Close 'X'
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = data.title.uppercase(),
                            color = themeColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                        if (data.isLinkReport) {
                            Text(
                                text = "DNS & GEO THREAT INTELLIGENCE",
                                color = Color(0xFFBFFFCF).copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            Text(
                                text = "CLASSIFIED TELEMETRY DOSSIER",
                                color = Color(0xFFBFFFCF).copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_dossier_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = themeColor
                        )
                    }
                }

                HorizontalDivider(
                    color = themeColor.copy(alpha = 0.35f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Render Fields
                if (data.fields.isEmpty()) {
                    Text(
                        text = "No fields enabled in configuration.",
                        color = Color(0xFFBFFFCF).copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    data.fields.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$label:",
                                color = themeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(0.38f)
                            )
                            Text(
                                text = value,
                                color = Color(0xFFBFFFCF),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(0.62f)
                            )
                        }
                    }
                }

                // Threat Score Meter
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33000000), RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFF22AA55).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THREAT INDEX:",
                        color = themeColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    val threatColor = when {
                        data.threatScore > 70 -> Color(0xFFFF3B3B)
                        data.threatScore > 40 -> Color(0xFFFFEA00)
                        else -> Color(0xFF39FF14)
                    }
                    Text(
                        text = "${data.threatScore}/100 [${if (data.threatScore > 70) "CRITICAL" else if (data.threatScore > 40) "MEDIUM" else "LOW"}]",
                        color = threatColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Interactive Action Buttons: Ping, Exploit, Copy
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val intelText = buildString {
                                appendLine("=== ${data.title} ===")
                                data.fields.forEach { appendLine("${it.first}: ${it.second}") }
                            }
                            clipboard.setPrimaryClip(ClipData.newPlainText("GhostScan Intel", intelText))
                            Toast.makeText(context, "Dossier copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_intel_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColor),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(themeColor))
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }

                    OutlinedButton(
                        onClick = {
                            isExecutingAction = true
                            consoleOutput = "PING ${data.title} ... 32 bytes ICMP:\n"
                            scope.launch {
                                for (i in 1..3) {
                                    delay(280)
                                    val rtt = Random.nextInt(12, 68)
                                    consoleOutput += "seq=$i bytes=32 time=${rtt}ms TTL=58\n"
                                }
                                consoleOutput += "--- ping statistics: 0% packet loss ---"
                                isExecutingAction = false
                            }
                        },
                        enabled = !isExecutingAction,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ping_node_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColor),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(themeColor))
                    ) {
                        Icon(Icons.Default.NetworkPing, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ping", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = {
                            isExecutingAction = true
                            consoleOutput = "INITIALIZING PAYLOAD INJECTION...\n"
                            scope.launch {
                                delay(300)
                                consoleOutput += "[+] Target socket acquired\n"
                                delay(350)
                                consoleOutput += "[+] Bypassing stack canary\n"
                                delay(300)
                                consoleOutput += "[*] Exploit payload delivered: SUCCESS"
                                isExecutingAction = false
                            }
                        },
                        enabled = !isExecutingAction,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("inject_payload_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Inject", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }

                // Live simulated terminal output if action run
                consoleOutput?.let { output ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF000502), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFF39FF14).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = output,
                            color = Color(0xFF39FF14),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
