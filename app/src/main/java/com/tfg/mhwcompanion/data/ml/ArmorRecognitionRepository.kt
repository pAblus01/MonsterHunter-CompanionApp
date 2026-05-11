package com.tfg.mhwcompanion.data.ml

import android.graphics.Bitmap

interface ArmorRecognitionRepository {
    suspend fun recognize(bitmap: Bitmap): Result<RecognitionResult>
}
