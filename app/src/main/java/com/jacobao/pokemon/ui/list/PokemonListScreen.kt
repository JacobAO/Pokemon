package com.jacobao.pokemon.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.jacobao.pokemon.ui.theme.PokemonTheme
import kotlinx.serialization.Serializable

@Serializable
data object PokemonList : NavKey

@Composable
fun PokemonListScreenWrapper(
  viewModel: PokemonListViewModel,
  onPokemonClick: (pokemonId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  PokemonListScreen(
    state = state,
    onPokemonClick = onPokemonClick,
    modifier = modifier,
  )
}

@Composable
fun PokemonListScreen(
  state: PokemonListState,
  onPokemonClick: (pokemonId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  // TODO: implement Pokemon list UI
  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    TextButton(
      onClick = { onPokemonClick("pokeID") },
    ) {
      Text("Detail")
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PokemonListScreenPreview() {
  PokemonTheme {
    PokemonListScreen(
      state = PokemonListState(),
      onPokemonClick = {},
    )
  }
}
