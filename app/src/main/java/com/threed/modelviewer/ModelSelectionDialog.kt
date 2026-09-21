package com.threed.modelviewer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale.getDefault

@Composable
fun ModelSelectionDialog(
    models: List<String>,
    onModelSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Model")
        },
        text = {
            Column {

                models.forEach { modelPath ->

                    Button(
                        onClick = {
                            onModelSelected(modelPath)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            modelPath
                                .substringAfterLast("/")
                                .removeSuffix(".glb")
                                .uppercase(getDefault())
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}