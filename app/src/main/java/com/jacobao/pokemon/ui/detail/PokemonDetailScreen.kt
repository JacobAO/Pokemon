package com.jacobao.pokemon.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.jacobao.pokemon.R
import com.jacobao.pokemon.navigation.LocalBackButtonVisibility
import com.jacobao.pokemon.ui.theme.PokemonTheme
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetail(val pokemonId: String) : NavKey

@Composable
fun PokemonDetailScreenWrapper(
  viewModel: PokemonDetailViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  PokemonDetailScreen(
    state = state,
    onBack = onBack,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
  state: PokemonDetailState,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(state.pokemonId) },
        navigationIcon = {
          // Hidden when the list is visible alongside this screen in a list-detail layout.
          if (LocalBackButtonVisibility.current) {
            IconButton(onClick = onBack) {
              Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back),
              )
            }
          }
        },
      )
    },
  ) { innerPadding ->
    // TODO: implement Pokemon detail UI
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.Center,
    ) {
      Text("Pokemon Detail: ${state.pokemonId}")
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PokemonDetailScreenPreview() {
  PokemonTheme {
    PokemonDetailScreen(
      state = PokemonDetailState(pokemonId = "1"),
      onBack = {},
    )
  }
}
