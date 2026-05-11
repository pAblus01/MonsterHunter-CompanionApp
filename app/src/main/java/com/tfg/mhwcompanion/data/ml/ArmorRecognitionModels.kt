package com.tfg.mhwcompanion.data.ml

import android.graphics.RectF
import com.tfg.mhwcompanion.domain.model.ArmorPiece

data class DetectorResult(
    val slotType: String,
    val boundingBox: RectF,
    val confidence: Float
)

data class ClassifierPrediction(
    val label: String,
    val confidence: Float
)

data class RecognizedArmorSlot(
    val slotType: String,
    val detectorConfidence: Float,
    val predictedArmor: ArmorPiece?,
    val topPredictions: List<ClassifierPrediction>
)

data class RecognitionResult(
    val slots: List<RecognizedArmorSlot>,
    val warningMessage: String? = null
)
