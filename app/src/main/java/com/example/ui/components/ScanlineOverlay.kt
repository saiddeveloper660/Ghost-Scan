package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun ScanlineOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val h = size.height
        val w = size.width
        val step = 4f // scanline interval
        var y = 0f
        val linePaint = Color.Black.copy(alpha = 0.22f)

        while (y < h) {
            drawLine(
                color = linePaint,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.2f
            )
            y += step
        }
    }
}
