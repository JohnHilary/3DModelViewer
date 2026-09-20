package com.threed.modelviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.threed.modelviewer.ui.theme._3DModelViewerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            _3DModelViewerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    ModelViewerScreen(paddingValues)
                }
            }
        }
    }
}

