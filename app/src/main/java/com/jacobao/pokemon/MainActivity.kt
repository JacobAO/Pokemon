package com.jacobao.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jacobao.pokemon.navigation.ListDetailScene
import com.jacobao.pokemon.navigation.rememberListDetailSceneStrategy
import com.jacobao.pokemon.ui.detail.PokemonDetail
import com.jacobao.pokemon.ui.detail.PokemonDetailScreenWrapper
import com.jacobao.pokemon.ui.detail.PokemonDetailViewModel
import com.jacobao.pokemon.ui.list.PokemonList
import com.jacobao.pokemon.ui.list.PokemonListScreenWrapper
import com.jacobao.pokemon.ui.list.PokemonListViewModel
import com.jacobao.pokemon.ui.theme.PokemonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PokemonTheme {
        val backStack = rememberNavBackStack(PokemonList)
        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

        NavDisplay(
          backStack = backStack,
          onBack = { backStack.removeLastOrNull() },
          modifier = Modifier.fillMaxSize(),
          sceneStrategies = listOf(listDetailStrategy),
          entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
          ),
          entryProvider = entryProvider {
            entry<PokemonList>(metadata = ListDetailScene.listPane()) {
              PokemonListScreenWrapper(
                viewModel = hiltViewModel<PokemonListViewModel>(),
                onPokemonClick = { pokemonId -> backStack.showDetail(pokemonId) },
              )
            }
            entry<PokemonDetail>(metadata = ListDetailScene.detailPane()) { key ->
              // ViewModelStoreNavEntryDecorator scopes this to the entry, so each
              // PokemonDetail key gets its own ViewModel without needing a ViewModel key.
              PokemonDetailScreenWrapper(
                viewModel = hiltViewModel<PokemonDetailViewModel, PokemonDetailViewModel.Factory>(
                  creationCallback = { factory -> factory.create(key.pokemonId) },
                ),
                onBack = { backStack.removeLastOrNull() },
              )
            }
          },
        )
      }
    }
  }
}

/** Replaces any existing detail entry so the back stack holds at most one detail at a time. */
private fun NavBackStack<NavKey>.showDetail(pokemonId: String) {
  removeAll { it is PokemonDetail }
  add(PokemonDetail(pokemonId))
}
