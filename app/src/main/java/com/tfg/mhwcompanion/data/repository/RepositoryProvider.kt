package com.tfg.mhwcompanion.data.repository

import com.tfg.mhwcompanion.data.remote.NetworkModule

object RepositoryProvider {
    val armorRepository: ArmorRepository by lazy {
        ArmorRepositoryImpl(NetworkModule.armorApiService)
    }
}