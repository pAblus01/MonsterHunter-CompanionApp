package com.tfg.mhwcompanion.data.ml

object ModelAssetNames {
    const val MODEL_ROOT = "ml"
    const val DETECTOR_MODEL = "$MODEL_ROOT/detector.tflite"
    const val DETECTOR_LABELS = "$MODEL_ROOT/detector_labels.txt"

    fun classifierModel(slotType: String): String = "$MODEL_ROOT/classifier_${slotType.lowercase()}.tflite"

    fun classifierLabels(slotType: String): String = "$MODEL_ROOT/classifier_${slotType.lowercase()}_labels.txt"
}
