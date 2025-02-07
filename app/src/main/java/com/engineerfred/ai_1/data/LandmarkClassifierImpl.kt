package com.engineerfred.ai_1.data

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.engineerfred.ai_1.domain.LandMarkClassifier
import com.engineerfred.ai_1.domain.Classification
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class LandmarkClassifierImpl(
    private val context: Context
) : LandMarkClassifier {

    private var classifier: ImageClassifier? = null

    override fun classify(bitmap: Bitmap, rotation: Int): List<Classification> {
        if ( classifier == null ) setUpClassifier()

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImg = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        //optional
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val result = classifier?.classify(tensorImg, imageProcessingOptions)

        return result?.flatMap { classifications ->
            classifications.categories.map { category ->
                    //create our classification object
                Classification(
                    name = category.displayName ?: "Unknown",
                    confidence = category.score
                )
            }
        }?.distinctBy { it.name } ?: emptyList()
    }

    private fun setUpClassifier() {
        val baseOpts = BaseOptions.builder()
            .setNumThreads(2)
            .build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOpts)
            .setMaxResults(1)
            .setScoreThreshold(0.5F) //WHICH SCORE TO INCLUDE CLASSIFICATION
            .build()

        try {
            classifier = ImageClassifier.createFromFileAndOptions(context, "landmarks.tflite", options)
        }catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getOrientationFromRotation(rotation: Int) : ImageProcessingOptions.Orientation {
        return  when(rotation){
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

}
