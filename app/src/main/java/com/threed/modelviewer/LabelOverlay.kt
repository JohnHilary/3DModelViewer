package com.threed.modelviewer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun LabelOverlay(
    positions: List<LabelPosition>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(20f)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            positions.forEach { label ->
                drawLine(
                    color = Color.White,
                    start = Offset(
                        label.lineStartX,
                        label.lineStartY
                    ),
                    end = Offset(
                        label.x,
                        label.y
                    ),
                    strokeWidth = 2f
                )

                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(
                        label.lineStartX,
                        label.lineStartY
                    )
                )
            }
        }

        positions.forEach { label ->
            Text(
                text = label.text,
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            label.x.toInt(),
                            label.y.toInt()
                        )
                    }
                    .background(
                        Color(0xDD202020),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(
                        horizontal = 6.dp,
                        vertical = 4.dp
                    )
            )
        }
    }
}