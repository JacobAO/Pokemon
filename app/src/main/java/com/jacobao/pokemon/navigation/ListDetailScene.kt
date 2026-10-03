package com.jacobao.pokemon.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

/**
 * A [Scene] that displays a list and a detail [NavEntry] side-by-side in a 40/60 split.
 */
data class ListDetailScene<T : Any>(
  override val key: Any,
  override val previousEntries: List<NavEntry<T>>,
  val listEntry: NavEntry<T>,
  val detailEntry: NavEntry<T>,
) : Scene<T> {
  override val entries: List<NavEntry<T>> = listOf(listEntry, detailEntry)
  override val content: @Composable (() -> Unit) = {
    Row(modifier = Modifier.fillMaxSize()) {
      Column(modifier = Modifier.weight(0.4f)) {
        listEntry.Content()
      }

      // The list is always visible next to the detail, so the detail doesn't need a back button.
      CompositionLocalProvider(LocalBackButtonVisibility provides false) {
        Column(modifier = Modifier.weight(0.6f)) {
          AnimatedContent(
            targetState = detailEntry,
            contentKey = { entry -> entry.contentKey },
            transitionSpec = {
              slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
            },
          ) { entry ->
            entry.Content()
          }
        }
      }
    }
  }

  companion object {
    /** Marks a [NavEntry] as displayable in the list pane of a [ListDetailScene]. */
    fun listPane() = metadata {
      put(ListKey, true)
    }

    /** Marks a [NavEntry] as displayable in the detail pane of a [ListDetailScene]. */
    fun detailPane() = metadata {
      put(DetailKey, true)
    }
  }

  object ListKey : NavMetadataKey<Boolean>
  object DetailKey : NavMetadataKey<Boolean>
}

/**
 * Whether a detail entry should show a back button. `false` when it is displayed alongside the
 * list in a [ListDetailScene].
 */
val LocalBackButtonVisibility = compositionLocalOf { true }

@Composable
fun <T : Any> rememberListDetailSceneStrategy(): ListDetailSceneStrategy<T> {
  val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
  return remember(windowSizeClass) { ListDetailSceneStrategy(windowSizeClass) }
}

/**
 * Returns a [ListDetailScene] when the window is at least medium width, the last entry is a detail
 * entry, and a list entry is in the back stack. Otherwise returns `null` so entries are shown one
 * at a time.
 *
 * The scene key is the list entry's content key, so changing the detail entry animates within the
 * scene rather than replacing the whole scene.
 */
class ListDetailSceneStrategy<T : Any>(
  private val windowSizeClass: WindowSizeClass,
) : SceneStrategy<T> {

  override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
    if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) return null

    val detailEntry = entries.lastOrNull()
      ?.takeIf { it.metadata.contains(ListDetailScene.DetailKey) }
      ?: return null
    val listEntry = entries.findLast { it.metadata.contains(ListDetailScene.ListKey) }
      ?: return null

    return ListDetailScene(
      key = listEntry.contentKey,
      previousEntries = entries.dropLast(1),
      listEntry = listEntry,
      detailEntry = detailEntry,
    )
  }
}
