package com.threed.modelviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader


@Composable
fun ModelViewerScreen(paddingValues: PaddingValues) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val context = LocalContext.current
    val availableModels = listOf(
        "models/Bulb.glb",
        "models/Fiagena.glb",
        "models/Lungs.glb",
        "models/Microscope.glb",
        "models/solarsystem.glb"
    )

    var models by remember {
        mutableStateOf<List<ModelState>>(emptyList())
    }

    var showModelList by remember {
        mutableStateOf(false)
    }
    fun getLabels(modelPath: String): List<GlbLabel> {
        return GlbLabelReader.readLabels(
            context = context,
            assetPath = modelPath
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color.Black)
    ) {
        models.forEach { model ->
            ModelContainer(
                modifier = Modifier
                    .offset(
                        x = model.x.dp,
                        y = model.y.dp
                    )
                    .size(
                        width = model.width.dp,
                        height = model.height.dp
                    ),
                model = model,
                engine = engine,
                modelLoader = modelLoader,
                onChange = { updatedModel ->
                    models = models.map {
                        if (it.id == updatedModel.id) updatedModel else it
                    }
                },
                onClose = {
                    models = models.filter { it.id != model.id }
                },
             labels = getLabels(model.modelPath)
            )
        }

        Button(
            onClick = {
                showModelList = true
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Add Model")
        }

        if (showModelList) {
            ModelSelectionDialog(
                models = availableModels,
                onModelSelected = { modelPath ->
                    val newModel = ModelState(
                        id = System.currentTimeMillis(),
                        modelPath = modelPath,
                        x = 50f + models.size * 40f,
                        y = 100f + models.size * 40f,
                        width = 250f,
                        height = 250f
                    )

                    val labels = getLabels(modelPath)

                    android.util.Log.d(
                        "GLB_LABELS",
                        "$modelPath labels: ${labels.map { it.text }}"
                    )

                    models = models + newModel
                    showModelList = false
                },
                onDismiss = {
                    showModelList = false
                }
            )
        }
    }

}