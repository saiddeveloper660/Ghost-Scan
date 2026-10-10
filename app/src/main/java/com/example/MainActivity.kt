package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScanMode
import com.example.model.TargetCategory
import com.example.ui.components.CyberRadar
import com.example.ui.components.DecryptionOverlay
import com.example.ui.components.IntelDossierDialog
import com.example.ui.components.LinkTracerBar
import com.example.ui.components.MatrixCanvas
import com.example.ui.components.ScanlineOverlay
import com.example.ui.components.SettingsDialog
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberTextGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GhostScanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GhostScanApp()
            }
        }
    }
}

@Composable
fun GhostScanApp(viewModel: GhostScanViewModel = viewModel()) {
    val scanMode by viewModel.scanMode.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val activeCategory by viewModel.activeCategory.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val statusText by viewModel.statusText.collectAsState()
    val radarTargets by viewModel.radarTargets.collectAsState()
    val isAlertActive by viewModel.isAlertActive.collectAsState()

    val isDecrypting by viewModel.isDecrypting.collectAsState()
    val decryptTargetTitle by viewModel.decryptTargetTitle.collectAsState()
    val activeIntelData by viewModel.activeIntelData.collectAsState()

    val config by viewModel.config.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val traceInput by viewModel.traceInput.collectAsState()

    val currentThemeColor = activeCategory?.color ?: CyberNeonGreen

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBg)
    ) {
        // Dynamic Matrix Rain / Code Waterfall Background
        MatrixCanvas(
            mode = scanMode,
            isAlertActive = isAlertActive,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle CRT Scanline overlay texture
        if (config.scanlinesOverlay) {
            ScanlineOverlay(modifier = Modifier.fillMaxSize())
        }

        // Main Foreground UI
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = CyberTextGreen,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header: App Title, Live Clock, and Customize Gear
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GHOST//SCAN",
                            color = currentThemeColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 3.sp,
                            modifier = Modifier.testTag("app_title")
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentTime,
                            color = CyberTextGreen.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        IconButton(
                            onClick = { viewModel.openSettings() },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("gear_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = currentThemeColor
                            )
                        }
                    }
                }

                // Modes Selection Row: CODE, NUMBERS, MIXED+ALERT, GLITCH
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScanMode.values().forEach { mode ->
                        val isSelected = scanMode == mode
                        Button(
                            onClick = { viewModel.setScanMode(mode) },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) currentThemeColor else Color(0xBF000000),
                                contentColor = if (isSelected) Color.Black else CyberTextGreen
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isSelected) currentThemeColor else Color(0x6622AA55)
                                )
                            ),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("mode_button_${mode.name}")
                        ) {
                            Text(
                                text = mode.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Target Categories Row: Device, Wi-Fi, Bluetooth, Camera, Cloud
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TargetCategory.values().forEach { cat ->
                        val isSelected = activeCategory == cat
                        Button(
                            onClick = { viewModel.startScan(cat) },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) cat.color else Color(0xCC000000),
                                contentColor = if (isSelected) Color.Black else cat.color
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(cat.color)
                            ),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("cat_button_${cat.name}")
                        ) {
                            Text(
                                text = cat.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Cyber Radar Component (Takes primary display area)
                CyberRadar(
                    activeCategory = activeCategory,
                    isScanning = isScanning,
                    scanProgress = scanProgress,
                    statusText = statusText,
                    targets = radarTargets,
                    onTargetClick = { target ->
                        viewModel.openTargetIntel(target)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                // Network Link & IP Tracer Input Bar
                LinkTracerBar(
                    urlInput = traceInput,
                    onUrlChange = { viewModel.updateTraceInput(it) },
                    onTraceClick = { viewModel.traceLink() },
                    accentColor = currentThemeColor
                )
            }
        }

        // Fullscreen Decryption Animation Overlay
        DecryptionOverlay(
            targetTitle = decryptTargetTitle,
            isVisible = isDecrypting
        )

        // Decrypted Intel Dossier Panel / Dialog
        if (activeIntelData != null) {
            IntelDossierDialog(
                data = activeIntelData,
                position = config.panelPosition,
                onDismiss = { viewModel.dismissIntelDialog() }
            )
        }

        // Settings / Customize Dialog Modal
        if (isSettingsOpen) {
            SettingsDialog(
                config = config,
                onSaveConfig = { newConfig ->
                    viewModel.saveConfig(newConfig)
                },
                onDismiss = { viewModel.closeSettings() }
            )
        }
    }
}
