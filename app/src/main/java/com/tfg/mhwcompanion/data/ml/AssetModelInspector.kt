package com.tfg.mhwcompanion.data.ml

import android.content.Context

class AssetModelInspector(
    private val context: Context
) {
    fun hasRequiredAssets(): Boolean {
        return missingAssetPaths().isEmpty()
    }

    fun missingAssetPaths(): List<String> {
        return listOf(
            ModelAssetNames.DETECTOR_MODEL,
            ModelAssetNames.DETECTOR_LABELS
        ).filterNot(::hasAsset)
    }

    private fun hasAsset(path: String): Boolean {
        return runCatching {
            context.assets.open(path).close()
            true
        }.getOrElse { false }
    }
}
