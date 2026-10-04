package com.jacobao.pokemon.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.jacobao.pokemon.R
import com.jacobao.pokemon.ui.common.LoadErrorMessage
import com.jacobao.pokemon.ui.model.PokemonSummary
import com.jacobao.pokemon.ui.theme.PokemonTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object PokemonListRoute : NavKey

@Composable
fun PokemonListScreenWrapper(
  viewModel: PokemonListViewModel,
  onPokemonClick: (pokemonName: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val pokemon = viewModel.pokemon.collectAsLazyPagingItems()
  val snackbarHostState = remember { SnackbarHostState() }
  val refreshFailedMessage = stringResource(R.string.couldnt_refresh_pokemon)
  
  LaunchedEffect(pokemon, snackbarHostState) {
    // Only react to a refresh that just failed, so an error state that is already present when
    // this effect starts (e.g. after a configuration change) doesn't show the snackbar again.
    var wasRefreshing = false
    snapshotFlow { pokemon.loadState.refresh }.collect { refreshState ->
      // With no items the full-screen error is shown instead.
      if (wasRefreshing && refreshState is LoadState.Error && pokemon.itemCount > 0) {
        // Replace any visible snackbar rather than queueing repeated failures.
        snackbarHostState.currentSnackbarData?.dismiss()
        launch { snackbarHostState.showSnackbar(refreshFailedMessage) }
      }
      wasRefreshing = refreshState is LoadState.Loading
    }
  }
  
  PokemonListScreen(
    pokemon = pokemon,
    onPokemonClick = onPokemonClick,
    snackbarHostState = snackbarHostState,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
  pokemon: LazyPagingItems<PokemonSummary>,
  onPokemonClick: (pokemonName: String) -> Unit,
  modifier: Modifier = Modifier,
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(title = { Text(stringResource(R.string.all_the_pokemon)) })
    },
  ) { innerPadding ->
    val refreshState = pokemon.loadState.refresh
    val contentModifier = Modifier
      .fillMaxSize()
      .padding(innerPadding)
    
    when {
      // Only take over the whole screen while there is nothing to show.
      pokemon.itemCount == 0 && refreshState is LoadState.Loading -> {
        Box(
          modifier = contentModifier,
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      }
      
      pokemon.itemCount == 0 && refreshState is LoadState.Error -> {
        Box(
          modifier = contentModifier,
          contentAlignment = Alignment.Center,
        ) {
          LoadErrorMessage(
            message = stringResource(R.string.couldnt_load_pokemon),
            onRetry = pokemon::refresh,
          )
        }
      }
      
      else -> {
        PullToRefreshBox(
          isRefreshing = refreshState is LoadState.Loading,
          onRefresh = pokemon::refresh,
          modifier = contentModifier,
        ) {
          PokemonListContent(
            pokemon = pokemon,
            onPokemonClick = onPokemonClick,
            modifier = Modifier.fillMaxSize(),
          )
        }
      }
    }
  }
}

@Composable
private fun PokemonListContent(
  pokemon: LazyPagingItems<PokemonSummary>,
  onPokemonClick: (pokemonName: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(modifier = modifier) {
    items(
      count = pokemon.itemCount,
      // note name is assumed to be unique within the entire list or this will crash
      key = pokemon.itemKey { it.name },
    ) { index ->
      val item = pokemon[index] ?: return@items
      PokemonListItem(
        pokemon = item,
        onClick = { onPokemonClick(item.name) },
      )
      HorizontalDivider()
    }
    
    when (pokemon.loadState.append) {
      is LoadState.Loading -> item(key = "append_loading") {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      }
      
      is LoadState.Error -> item(key = "append_error") {
        LoadErrorMessage(
          message = stringResource(R.string.couldnt_load_more_pokemon),
          onRetry = pokemon::retry,
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        )
      }
      
      is LoadState.NotLoading -> Unit
    }
  }
}

@Composable
private fun PokemonListItem(
  pokemon: PokemonSummary,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clickable(
        onClick = onClick,
        role = Role.Button,
      )
      .padding(horizontal = 16.dp, vertical = 16.dp),
  ) {
    Text(text = pokemon.name, style = MaterialTheme.typography.titleLarge)
  }
}

private val previewPokemon = listOf("bulbasaur", "ivysaur", "venusaur").mapIndexed { _, name ->
  PokemonSummary(name = name)
}

@Preview(showBackground = true)
@Composable
private fun PokemonListScreenPreview() {
  PokemonTheme {
    PokemonListScreen(
      pokemon = flowOf(PagingData.from(previewPokemon)).collectAsLazyPagingItems(),
      onPokemonClick = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun PokemonListScreenAppendErrorPreview() {
  val loadStates = LoadStates(
    refresh = LoadState.NotLoading(endOfPaginationReached = false),
    prepend = LoadState.NotLoading(endOfPaginationReached = true),
    append = LoadState.Error(Exception()),
  )
  PokemonTheme {
    PokemonListScreen(
      pokemon = flowOf(PagingData.from(previewPokemon, loadStates)).collectAsLazyPagingItems(),
      onPokemonClick = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun PokemonListScreenRefreshErrorPreview() {
  val loadStates = LoadStates(
    refresh = LoadState.Error(Exception()),
    prepend = LoadState.NotLoading(endOfPaginationReached = false),
    append = LoadState.NotLoading(endOfPaginationReached = false),
  )
  PokemonTheme {
    PokemonListScreen(
      pokemon = flowOf(PagingData.empty<PokemonSummary>(loadStates)).collectAsLazyPagingItems(),
      onPokemonClick = {},
    )
  }
}
