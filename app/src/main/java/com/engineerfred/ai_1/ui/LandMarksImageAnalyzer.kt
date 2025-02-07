package com.engineerfred.ai_1.ui

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.engineerfred.ai_1.domain.Classification
import com.engineerfred.ai_1.domain.LandMarkClassifier
import com.engineerfred.ai_1.utils.centerCrop

class LandMarksImageAnalyzer(
    private val classifier: LandMarkClassifier,
    private val onResults: (List<Classification>) -> Unit
): ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if ( frameSkipCounter % 60 == 0 ) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image.toBitmap().centerCrop(321,321)

            val result = classifier.classify(bitmap, rotationDegrees)
            onResults(result)
        }
        frameSkipCounter += 1
        image.close()
    }
}