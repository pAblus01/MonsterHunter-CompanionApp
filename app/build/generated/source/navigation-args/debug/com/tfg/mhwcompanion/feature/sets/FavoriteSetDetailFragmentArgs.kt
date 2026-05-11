package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class FavoriteSetDetailFragmentArgs(
  public val favoriteName: String,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("favoriteName", this.favoriteName)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("favoriteName", this.favoriteName)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): FavoriteSetDetailFragmentArgs {
      bundle.setClassLoader(FavoriteSetDetailFragmentArgs::class.java.classLoader)
      val __favoriteName : String?
      if (bundle.containsKey("favoriteName")) {
        __favoriteName = bundle.getString("favoriteName")
        if (__favoriteName == null) {
          throw IllegalArgumentException("Argument \"favoriteName\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"favoriteName\" is missing and does not have an android:defaultValue")
      }
      return FavoriteSetDetailFragmentArgs(__favoriteName)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle):
        FavoriteSetDetailFragmentArgs {
      val __favoriteName : String?
      if (savedStateHandle.contains("favoriteName")) {
        __favoriteName = savedStateHandle["favoriteName"]
        if (__favoriteName == null) {
          throw IllegalArgumentException("Argument \"favoriteName\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"favoriteName\" is missing and does not have an android:defaultValue")
      }
      return FavoriteSetDetailFragmentArgs(__favoriteName)
    }
  }
}
