package com.tfg.mhwcompanion.feature.sets

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tfg.mhwcompanion.domain.model.FavoriteArmorSet

class LocalFavoriteSetStore(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter<List<FavoriteArmorSet>>(
        Types.newParameterizedType(List::class.java, FavoriteArmorSet::class.java)
    )

    fun getFavorites(): List<FavoriteArmorSet> {
        val rawValue = preferences.getString(KEY_FAVORITES, null) ?: return emptyList()
        return adapter.fromJson(rawValue).orEmpty()
    }

    fun saveFavorites(items: List<FavoriteArmorSet>) {
        preferences.edit()
            .putString(KEY_FAVORITES, adapter.toJson(items))
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "favorite_sets_store"
        private const val KEY_FAVORITES = "favorites"
    }
}