package com.engineerfred.ai_1.domain

import android.graphics.Bitmap

interface LandMarkClassifier {
    fun classify(bitmap: Bitmap, rotation: Int) : List<Classification>
}