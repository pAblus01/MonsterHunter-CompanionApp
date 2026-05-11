package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class SetDetailFragmentArgs(
  public val setName: String,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("setName", this.setName)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("setName", this.setName)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): SetDetailFragmentArgs {
      bundle.setClassLoader(SetDetailFragmentArgs::class.java.classLoader)
      val __setName : String?
      if (bundle.containsKey("setName")) {
        __setName = bundle.getString("setName")
        if (__setName == null) {
          throw IllegalArgumentException("Argument \"setName\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"setName\" is missing and does not have an android:defaultValue")
      }
      return SetDetailFragmentArgs(__setName)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): SetDetailFragmentArgs {
      val __setName : String?
      if (savedStateHandle.contains("setName")) {
        __setName = savedStateHandle["setName"]
        if (__setName == null) {
          throw IllegalArgumentException("Argument \"setName\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"setName\" is missing and does not have an android:defaultValue")
      }
      return SetDetailFragmentArgs(__setName)
    }
  }
}
