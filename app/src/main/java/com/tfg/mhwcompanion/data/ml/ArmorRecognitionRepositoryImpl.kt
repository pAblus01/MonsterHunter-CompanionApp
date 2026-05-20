package com.tfg.mhwcompanion.data.ml

import android.content.Context
import android.graphics.Bitmap
import com.tfg.mhwcompanion.data.repository.ArmorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArmorRecognitionRepositoryImpl(
    context: Context,
    private val armorRepository: ArmorRepository
) : ArmorRecognitionRepository {

    private val inspector = AssetModelInspector(context)
    private val detector = runCatching { YoloTfliteArmorDetector(context) }.getOrNull()

    override suspend fun recognize(bitmap: Bitmap): Result<RecognitionResult> = withContext(Dispatchers.Default) {
        runCatching {
            val missingAssets = inspector.missingAssetPaths()
            if (missingAssets.isNotEmpty()) {
                return@runCatching RecognitionResult(
                    detections = emptyList(),
                    warningMessage = buildMissingAssetsMessage(missingAssets)
                )
            }

            val activeDetector = detector ?: return@runCatching RecognitionResult(
                detections = emptyList(),
                warningMessage = "No se pudo inicializar el intérprete TFLite. Revisa el modelo y las etiquetas en assets."
            )

            val detections = activeDetector.detect(bitmap).map { detection ->
                val armorPiece = armorRepository.getArmorPieceById(detection.armorClassId).getOrNull()
                RecognizedArmorDetection(
                    armorClassId = detection.armorClassId,
                    detectorConfidence = detection.confidence,
                    predictedArmor = armorPiece,
                    label = detection.label,
                    boundingBox = detection.boundingBox
                )
            }
            RecognitionResult(
                detections = detections,
                warningMessage = if (detections.isEmpty()) {
                    "No se detectó ninguna armadura."
                } else {
                    null
                }
            )
        }
    }

    private fun buildMissingAssetsMessage(missingAssets: List<String>): String {
        return "Faltan modelos en assets: ${missingAssets.joinToString()}"
    }
}
