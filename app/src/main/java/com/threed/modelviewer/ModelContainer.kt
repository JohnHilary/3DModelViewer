package com.threed.modelviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.sceneview.SceneView
import io.github.sceneview.collision.Vector3
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberModelInstance
@Composable
fun ModelContainer(
    modifier: Modifier = Modifier,
    model: ModelState,
    engine: com.google.android.filament.Engine,
    modelLoader: io.github.sceneview.loaders.ModelLoader,
    labels: List<GlbLabel>,
    onChange: (ModelState) -> Unit,
    onClose: () -> Unit
) {
    val density = LocalDensity.current
    val cameraNode = rememberCameraNode(engine)

    var modelNode by remember(model.id) {
        mutableStateOf<io.github.sceneview.node.ModelNode?>(null)
    }

    var labelPositions by remember(model.id) {
        mutableStateOf<List<LabelPosition>>(emptyList())
    }

    var currentWidth by remember(model.id) {
        mutableStateOf(model.width)
    }

    var currentHeight by remember(model.id) {
        mutableStateOf(model.height)
    }
    val latestModel = rememberUpdatedState(model)
    val latestOnChange = rememberUpdatedState(onChange)
    Box(
        modifier = modifier
            .offset(
                x = model.x.dp,
                y = model.y.dp
            )
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
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            onFrame = {
                if (model.labelsVisible && modelNode != null) {
                    val node = modelNode!!

                    labelPositions = labels.map { label ->
                        val worldPosition = node.getWorldPosition(
                            io.github.sceneview.math.Position(
                                label.translationX,
                                label.translationY,
                                label.translationZ
                            )
                        )

                        val screenPosition =
                            cameraNode.worldToScreenPoint(
                                Vector3(
                                    worldPosition.x,
                                    worldPosition.y,
                                    worldPosition.z
                                )
                            )

                        LabelPosition(
                            text = label.text,
                            x = screenPosition.x + 14f,
                            y = screenPosition.y - 18f,
                            lineStartX = screenPosition.x,
                            lineStartY = screenPosition.y
                        )
                    }
                } else {
                    labelPositions = emptyList()
                }
            }
        ) {
            modelInstance?.let { instance ->
                ModelNode(
                    modelInstance = instance,
                    scaleToUnits = 1.0f,
                    scale = io.github.sceneview.math.Scale(model.scale),
                    apply = {
                        modelNode = this
                    }
                )
            }
        }

        if (model.labelsVisible) {
            LabelOverlay(
                positions = labelPositions
            )
        }

        if (!model.interactionMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(model.id, model.interactionMode) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)

                            var previousDistance = 0f
                            var gestureWidth = latestModel.value.width
                            var gestureHeight = latestModel.value.height

                            while (true) {
                                val event = awaitPointerEvent()
                                val pressed = event.changes.filter { it.pressed }

                                if (pressed.isEmpty()) {
                                    break
                                }

                                if (pressed.size == 2) {
                                    val first = pressed[0].position
                                    val second = pressed[1].position

                                    val dx = second.x - first.x
                                    val dy = second.y - first.y

                                    val distance = kotlin.math.sqrt(
                                        dx * dx + dy * dy
                                    )

                                    if (previousDistance > 0f && distance > 0f) {
                                        val scaleChange = distance / previousDistance

                                        if (kotlin.math.abs(scaleChange - 1f) > 0.001f) {
                                            gestureWidth =
                                                (gestureWidth * scaleChange)
                                                    .coerceIn(140f, 600f)

                                            gestureHeight =
                                                (gestureHeight * scaleChange)
                                                    .coerceIn(140f, 600f)

                                            latestOnChange.value(
                                                latestModel.value.copy(
                                                    width = gestureWidth,
                                                    height = gestureHeight
                                                )
                                            )
                                        }
                                    }

                                    previousDistance = distance
                                } else {
                                    previousDistance = 0f
                                }
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
                        var currentX = latestModel.value.x
                        var currentY = latestModel.value.y

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

                                latestOnChange.value(
                                    latestModel.value.copy(
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
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                text = if (model.interactionMode) {
                    "3D MODE"
                } else {
                    "DRAG  / PINCH TO RESIZE"
                },
                color = Color.White
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
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