package com.engineerfred.ai_1.ui.screens

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraPreview(
    lifecycleCameraController: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = {
            PreviewView(it).apply {
                this.controller = lifecycleCameraController
                lifecycleCameraController.bindToLifecycle(lifeCycleOwner)
            }
        }
    )
}