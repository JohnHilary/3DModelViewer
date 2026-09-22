package com.threed.modelviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelViewerScreen() {
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
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar(
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Model Studio",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "3D MODEL VIEWER",
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = Color(0xFF9BAAC5)
                    )
                }
            },
            actions = {
                Box(
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    IconButton(
                        onClick = { showModelList = true },

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Add Model",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF111A2B),
                titleContentColor = Color.White
            )
        )
    }) { paddingValues ->
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

}