package com.threed.modelviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun LabelOverlay(
    positions: List<LabelPosition>,
    density: androidx.compose.ui.unit.Density
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(20f)
    ) {
        positions.forEach { label ->
            Text(
                text = label.text,
                color = Color.White,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            label.x.toInt(),
                            label.y.toInt()
                        )
                    }
                    .background(Color.Black)
                    .padding(6.dp)
            )
        }
    }
}