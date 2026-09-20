
package com.threed.modelviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.sceneview.SceneView
import io.github.sceneview.rememberModelInstance

@Composable
fun ModelContainer(
    modifier: Modifier = Modifier,
    model: ModelState,
    engine: com.google.android.filament.Engine,
    modelLoader: io.github.sceneview.loaders.ModelLoader,
    onChange: (ModelState) -> Unit,
    onClose: () -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .size(
                width = model.width.dp,
                height = model.height.dp
            )
            .zIndex(model.id.toFloat())
            .clip(RoundedCornerShape(12.dp))
            .border(
                2.dp,
                Color.White,
                RoundedCornerShape(12.dp)
            )
            .background(Color.Black)
    ) {
        val modelInstance = rememberModelInstance(
            modelLoader,
            model.modelPath
        )

        SceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader
        ) {
            modelInstance?.let { instance ->
                ModelNode(
                    modelInstance = instance,
                    scaleToUnits = 1.0f,
                    scale = io.github.sceneview.math.Scale(model.scale)
                )
            }
        }

        if (!model.interactionMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(model.id, model.interactionMode) {
                        var currentWidth = model.width
                        var currentHeight = model.height

                        detectTransformGestures { _, _, zoomChange, _ ->
                            if (zoomChange != 1f) {
                                currentWidth =
                                    (currentWidth * zoomChange).coerceIn(140f, 600f)
                                currentHeight =
                                    (currentHeight * zoomChange).coerceIn(140f, 600f)

                                onChange(
                                    model.copy(
                                        width = currentWidth,
                                        height = currentHeight
                                    )
                                )
                            }
                        }
                    }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(45.dp)
                .background(Color.DarkGray)
                .pointerInput(model.id, model.interactionMode) {
                    if (!model.interactionMode) {
                        var currentX = model.x
                        var currentY = model.y

                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()

                                val deltaX = with(density) {
                                    dragAmount.x.toDp().value
                                }
                                val deltaY = with(density) {
                                    dragAmount.y.toDp().value
                                }

                                currentX = (currentX + deltaX).coerceAtLeast(0f)
                                currentY = (currentY + deltaY).coerceAtLeast(0f)

                                onChange(
                                    model.copy(
                                        x = currentX,
                                        y = currentY
                                    )
                                )
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (model.interactionMode) "3D MODE" else "DRAG TO MOVE • PINCH TO RESIZE",
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(6.dp)
        ) {
            Button(
                onClick = {
                    onChange(
                        model.copy(
                            interactionMode = !model.interactionMode
                        )
                    )
                }
            ) {
                Text("3D")
            }

            Button(
                onClick = {
                    onChange(
                        model.copy(
                            labelsVisible = !model.labelsVisible
                        )
                    )
                }
            ) {
                Text("Label")
            }

            Button(
                onClick = onClose
            ) {
                Text("X")
            }
        }
    }
}