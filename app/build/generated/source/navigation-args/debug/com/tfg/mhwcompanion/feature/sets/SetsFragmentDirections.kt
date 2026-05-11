package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import androidx.navigation.NavDirections
import com.tfg.mhwcompanion.R
import kotlin.Int
import kotlin.String

public class SetsFragmentDirections private constructor() {
  private data class ActionSetsFragmentToSetDetailFragment(
    public val setName: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_setsFragment_to_setDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("setName", this.setName)
        return result
      }
  }

  private data class ActionSetsFragmentToFavoriteSetDetailFragment(
    public val favoriteName: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_setsFragment_to_favoriteSetDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("favoriteName", this.favoriteName)
        return result
      }
  }

  public companion object {
    public fun actionSetsFragmentToSetDetailFragment(setName: String): NavDirections =
        ActionSetsFragmentToSetDetailFragment(setName)

    public fun actionSetsFragmentToFavoriteSetDetailFragment(favoriteName: String): NavDirections =
        ActionSetsFragmentToFavoriteSetDetailFragment(favoriteName)
  }
}
