package com.threed.modelviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember


data class ModelState(
    val id: Long,
    val modelPath: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val scale: Float = 1f,
    val rotationX: Float = 0f,
    val rotationY: Float = 0f,
    val zoom: Float = 1f,
    val interactionMode: Boolean = false,
    val labelsVisible: Boolean = false
)


