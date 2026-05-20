package com.tfg.mhwcompanion.data.ml

import android.graphics.RectF
import com.tfg.mhwcompanion.domain.model.ArmorPiece

data class DetectorResult(
    val armorClassId: Int,
    val label: String,
    val boundingBox: RectF,
    val confidence: Float
)

data class RecognizedArmorDetection(
    val armorClassId: Int,
    val detectorConfidence: Float,
    val predictedArmor: ArmorPiece?,
    val label: String,
    val boundingBox: RectF? = null
)

data class RecognitionResult(
    val detections: List<RecognizedArmorDetection>,
    val warningMessage: String? = null
)
