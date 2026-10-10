package com.example.ui.components

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LinkTracerBar(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    onTraceClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cyanColor = Color(0xFF00E5FF)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = urlInput,
                onValueChange = onUrlChange,
                placeholder = {
                    Text(
                        text = "Paste link / IP to trace…",
                        color = Color(0x77BFFFCF),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = cyanColor,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                            if (!clipText.isNullOrBlank()) {
                                onUrlChange(clipText.trim())
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste from clipboard",
                            tint = cyanColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("url_trace_input"),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cyanColor,
                    unfocusedBorderColor = Color(0x6622AA55),
                    focusedContainerColor = Color(0xD9000000),
                    unfocusedContainerColor = Color(0xB3000000),
                    focusedTextColor = Color(0xFFBFFFCF),
                    unfocusedTextColor = Color(0xFFBFFFCF),
                    cursorColor = cyanColor
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = onTraceClick,
                modifier = Modifier
                    .height(54.dp)
                    .testTag("trace_button"),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cyanColor,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "TRACE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }

        // Quick Preset targets chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "TARGETS:",
                color = cyanColor.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            listOf(
                "target-bank.internal",
                "ghost-node.darknet",
                "recon-satellite.net",
                "192.168.1.254"
            ).forEach { preset ->
                Text(
                    text = preset,
                    color = cyanColor,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .background(Color(0x2200E5FF), RoundedCornerShape(3.dp))
                        .border(0.7.dp, cyanColor.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .clickable {
                            onUrlChange(preset)
                        }
                )
            }
        }
    }
}
