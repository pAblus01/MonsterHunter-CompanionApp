package com.tfg.mhwcompanion.data.ml

import android.content.Context

class AssetModelInspector(
    private val context: Context
) {
    fun hasRequiredAssets(): Boolean {
        return hasAsset(ModelAssetNames.DETECTOR_MODEL)
    }

    fun missingAssetPathsForSlots(slotTypes: Collection<String>): List<String> {
        val expectedPaths = buildList {
            add(ModelAssetNames.DETECTOR_MODEL)
            slotTypes.forEach { slotType ->
                add(ModelAssetNames.classifierModel(slotType))
                add(ModelAssetNames.classifierLabels(slotType))
            }
        }

        return expectedPaths.filterNot(::hasAsset)
    }

    private fun hasAsset(path: String): Boolean {
        return runCatching {
            context.assets.open(path).close()
            true
        }.getOrElse { false }
    }
}
