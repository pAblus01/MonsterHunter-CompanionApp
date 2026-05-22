package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.IntArray
import kotlin.jvm.JvmStatic

public data class BuildDetailFragmentArgs(
  public val armorPieceIds: IntArray,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putIntArray("armorPieceIds", this.armorPieceIds)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("armorPieceIds", this.armorPieceIds)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): BuildDetailFragmentArgs {
      bundle.setClassLoader(BuildDetailFragmentArgs::class.java.classLoader)
      val __armorPieceIds : IntArray?
      if (bundle.containsKey("armorPieceIds")) {
        __armorPieceIds = bundle.getIntArray("armorPieceIds")
        if (__armorPieceIds == null) {
          throw IllegalArgumentException("Argument \"armorPieceIds\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"armorPieceIds\" is missing and does not have an android:defaultValue")
      }
      return BuildDetailFragmentArgs(__armorPieceIds)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): BuildDetailFragmentArgs {
      val __armorPieceIds : IntArray?
      if (savedStateHandle.contains("armorPieceIds")) {
        __armorPieceIds = savedStateHandle["armorPieceIds"]
        if (__armorPieceIds == null) {
          throw IllegalArgumentException("Argument \"armorPieceIds\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"armorPieceIds\" is missing and does not have an android:defaultValue")
      }
      return BuildDetailFragmentArgs(__armorPieceIds)
    }
  }
}
