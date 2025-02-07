package com.engineerfred.ai_1

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.engineerfred.ai_1.data.LandmarkClassifierImpl
import com.engineerfred.ai_1.domain.Classification
import com.engineerfred.ai_1.ui.LandMarksImageAnalyzer
import com.engineerfred.ai_1.ui.screens.CameraPreview
import com.engineerfred.ai_1.ui.theme.AI_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCameraPermission()
        enableEdgeToEdge()
        //cameraX for image processing
        //tensorflow lite
        setContent {
            Scaffold { paddingValues ->
                MyApp(applicationContext, paddingValues)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if ( requestCode == 241 ) {
            if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                // Permission granted, you can now access the camera
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                showPermissionDeniedDialog()
            }
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun checkCameraPermission() {
        if ( hasCameraPermission() ) return
        if ( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ) {
            // Show a rationale to the user if they click don't ask again
            AlertDialog.Builder(this)
                .setTitle("Camera Permission Needed")
                .setMessage("This app needs camera access to scan images. Please allow it.")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.CAMERA), 241
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            // Directly request permission if it's the first time or the user selected "Don't ask again"
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 241
            )
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("You have denied camera access permanently. Enable it in settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}


@Composable
private fun MyApp(
    context: Context,
    paddingValues: PaddingValues
) {
    AI_1Theme {

        var classifications by remember {
            mutableStateOf(emptyList<Classification>())
        }
        val analyzer = remember {
            LandMarksImageAnalyzer(
                classifier = LandmarkClassifierImpl(context),
                onResults = {
                    classifications = it
                }
            )
        }

        val lfCameraController = remember {
            LifecycleCameraController(context).apply {
                setEnabledUseCases(CameraController.IMAGE_ANALYSIS) //we are using cameraX for image analysis, not taking picture or videos
                setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    analyzer
                )
            }
        }

        Box(
            Modifier.fillMaxSize().padding(paddingValues)
        ) {
            CameraPreview(lfCameraController, modifier = Modifier.fillMaxSize())
            classifications.forEach { classification ->
                Text(
                    text = classification.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}