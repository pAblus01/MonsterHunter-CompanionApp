package com.tfg.mhwcompanion.data.ml

import android.content.Context
import android.graphics.Bitmap
import com.tfg.mhwcompanion.data.repository.ArmorRepository
import com.tfg.mhwcompanion.domain.model.ArmorPiece
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArmorRecognitionRepositoryImpl(
    context: Context,
    private val armorRepository: ArmorRepository
) : ArmorRecognitionRepository {

    private val supportedSlots = listOf("head", "chest", "arms", "waist", "legs")
    private val inspector = AssetModelInspector(context)

    override suspend fun recognize(bitmap: Bitmap): Result<RecognitionResult> = withContext(Dispatchers.Default) {
        runCatching {
            val missingAssets = inspector.missingAssetPathsForSlots(supportedSlots)
            if (missingAssets.isNotEmpty()) {
                return@runCatching RecognitionResult(
                    slots = emptyList(),
                    warningMessage = buildMissingAssetsMessage(missingAssets)
                )
            }

            val availableArmor = armorRepository.getArmorPieces(limit = 200).getOrThrow()
            val mockedPredictions = buildMockPredictions(bitmap, availableArmor)
            RecognitionResult(
                slots = mockedPredictions,
                warningMessage = "Modelos detectados en assets. Sustituye la inferencia simulada por el intérprete TFLite final."
            )
        }
    }

    private fun buildMockPredictions(bitmap: Bitmap, availableArmor: List<ArmorPiece>): List<RecognizedArmorSlot> {
        if (bitmap.width <= 0 || bitmap.height <= 0) return emptyList()

        return supportedSlots.mapNotNull { slotType ->
            val candidates = availableArmor
                .filter { it.type.equals(slotType, ignoreCase = true) }
                .sortedByDescending { it.rarity }
                .take(3)

            val topPredictions = candidates.mapIndexed { index, item ->
                ClassifierPrediction(
                    label = "${slotType}__${item.name.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')}__${item.id}",
                    confidence = 0.85f - (index * 0.1f)
                )
            }

            val primary = candidates.firstOrNull() ?: return@mapNotNull null
            RecognizedArmorSlot(
                slotType = slotType,
                detectorConfidence = 0.90f,
                predictedArmor = primary,
                topPredictions = topPredictions
            )
        }
    }

    private fun buildMissingAssetsMessage(missingAssets: List<String>): String {
        return "Faltan modelos en assets: ${missingAssets.joinToString()}"
    }
}
