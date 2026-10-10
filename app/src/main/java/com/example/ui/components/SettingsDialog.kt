package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConfigSettings
import com.example.model.PanelPosition

private val FIELD_LABELS = listOf(
    "name" to "Name",
    "phone" to "Phone",
    "email" to "Email",
    "password" to "Password",
    "address" to "Address",
    "ip" to "IP Address",
    "mac" to "MAC",
    "login" to "Last login",
    "threat" to "Threat Score",
    "signal" to "Signal RSSI"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    config: ConfigSettings,
    onSaveConfig: (ConfigSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fieldVisibility by remember { mutableStateOf(config.fieldVisibility) }
    var fieldOverrides by remember { mutableStateOf(config.fieldOverrides) }
    var panelPosition by remember { mutableStateOf(config.panelPosition) }
    var itemsPerScan by remember { mutableStateOf(config.itemsPerScan) }
    var soundEnabled by remember { mutableStateOf(config.soundEnabled) }
    var hapticsEnabled by remember { mutableStateOf(config.hapticsEnabled) }
    var scanlinesOverlay by remember { mutableStateOf(config.scanlinesOverlay) }

    var posExpanded by remember { mutableStateOf(false) }
    var cntExpanded by remember { mutableStateOf(false) }

    val accent = Color(0xFF39FF14)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xD9000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 440.dp)
                .fillMaxWidth(0.95f)
                .clickable(enabled = false) {}
                .border(1.5.dp, accent, RoundedCornerShape(8.dp))
                .testTag("settings_modal"),
            colors = CardDefaults.cardColors(containerColor = Color(0xF8020A05)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = accent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CUSTOMIZE",
                            color = accent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = accent)
                    }
                }

                Text(
                    text = "Tick what shows up. Fill a box to force that value (leave blank = random).",
                    color = Color(0xFFBFFFCF).copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                HorizontalDivider(color = accent.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                // Fields Checklist and Custom Overrides
                Text(
                    text = "INTEL DOSSIER FIELDS",
                    color = accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(4.dp))

                FIELD_LABELS.forEach { (key, label) ->
                    val isChecked = fieldVisibility[key] ?: true
                    val overrideValue = fieldOverrides[key] ?: ""

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    fieldVisibility = fieldVisibility.toMutableMap().apply { put(key, checked) }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = accent,
                                    uncheckedColor = Color.Gray,
                                    checkmarkColor = Color.Black
                                ),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = label,
                                color = Color(0xFFBFFFCF),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (isChecked) {
                            OutlinedTextField(
                                value = overrideValue,
                                onValueChange = { newVal ->
                                    fieldOverrides = fieldOverrides.toMutableMap().apply { put(key, newVal) }
                                },
                                placeholder = {
                                    Text("random", color = Color(0x66BFFFCF), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 36.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accent,
                                    unfocusedBorderColor = Color(0x4422AA55),
                                    focusedTextColor = Color(0xFFBFFFCF),
                                    unfocusedTextColor = Color(0xFFBFFFCF),
                                    cursorColor = accent
                                )
                            )
                        }
                    }
                }

                HorizontalDivider(color = accent.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                // Panel Position Selector
                Text(
                    text = "PANEL POSITION",
                    color = accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = posExpanded,
                    onExpandedChange = { posExpanded = it }
                ) {
                    OutlinedTextField(
                        value = panelPosition.label,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent,
                            unfocusedBorderColor = Color(0x4422AA55),
                            focusedTextColor = Color(0xFFBFFFCF),
                            unfocusedTextColor = Color(0xFFBFFFCF)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = posExpanded,
                        onDismissRequest = { posExpanded = false },
                        modifier = Modifier.background(Color(0xFF020A05))
                    ) {
                        PanelPosition.values().forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos.label, color = Color(0xFFBFFFCF), fontFamily = FontFamily.Monospace) },
                                onClick = {
                                    panelPosition = pos
                                    posExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Items Per Scan Selector
                Text(
                    text = "ITEMS PER SCAN",
                    color = accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = cntExpanded,
                    onExpandedChange = { cntExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "$itemsPerScan targets",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cntExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accent,
                            unfocusedBorderColor = Color(0x4422AA55),
                            focusedTextColor = Color(0xFFBFFFCF),
                            unfocusedTextColor = Color(0xFFBFFFCF)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = cntExpanded,
                        onDismissRequest = { cntExpanded = false },
                        modifier = Modifier.background(Color(0xFF020A05))
                    ) {
                        listOf(5, 7, 10, 15).forEach { count ->
                            DropdownMenuItem(
                                text = { Text("$count targets", color = Color(0xFFBFFFCF), fontFamily = FontFamily.Monospace) },
                                onClick = {
                                    itemsPerScan = count
                                    cntExpanded = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = accent.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                // Audio / Haptic Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects (Procedural Audio)", color = Color(0xFFBFFFCF), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = accent, checkedTrackColor = accent.copy(alpha = 0.5f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Feedback (Vibration)", color = Color(0xFFBFFFCF), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { hapticsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = accent, checkedTrackColor = accent.copy(alpha = 0.5f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Scanline CRT Overlay", color = Color(0xFFBFFFCF), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Switch(
                        checked = scanlinesOverlay,
                        onCheckedChange = { scanlinesOverlay = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = accent, checkedTrackColor = accent.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Save / Done Button
                Button(
                    onClick = {
                        val newConfig = ConfigSettings(
                            fieldVisibility = fieldVisibility,
                            fieldOverrides = fieldOverrides,
                            panelPosition = panelPosition,
                            itemsPerScan = itemsPerScan,
                            soundEnabled = soundEnabled,
                            hapticsEnabled = hapticsEnabled,
                            scanlinesOverlay = scanlinesOverlay
                        )
                        onSaveConfig(newConfig)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_config_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black)
                ) {
                    Text("DONE", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                }
            }
        }
    }
}
