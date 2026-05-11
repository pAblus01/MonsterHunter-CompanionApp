package com.tfg.mhwcompanion.data.repository

import android.content.Context
import com.tfg.mhwcompanion.data.ml.ArmorRecognitionRepository
import com.tfg.mhwcompanion.data.ml.ArmorRecognitionRepositoryImpl
import com.tfg.mhwcompanion.data.remote.NetworkModule

object RepositoryProvider {
    val armorRepository: ArmorRepository by lazy {
        ArmorRepositoryImpl(NetworkModule.armorApiService)
    }

    fun armorRecognitionRepository(context: Context): ArmorRecognitionRepository {
        return ArmorRecognitionRepositoryImpl(
            context = context.applicationContext,
            armorRepository = armorRepository
        )
    }
}