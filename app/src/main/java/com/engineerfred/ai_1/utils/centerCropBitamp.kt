package com.engineerfred.ai_1.utils

import android.graphics.Bitmap

fun Bitmap.centerCrop(
    desiredWidth: Int,
    desiredHeight: Int
) : Bitmap {
    //where to start cropping from the x-axis
    val xStart = (width - desiredWidth) / 2
    //where to start cropping from the y-axis
    val yStart = (height - desiredHeight) / 2

    if ( xStart < 0  || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)

}