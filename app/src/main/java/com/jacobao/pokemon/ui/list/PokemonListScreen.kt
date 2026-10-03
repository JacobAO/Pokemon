package com.jacobao.pokemon.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.jacobao.pokemon.data.model.PokemonSummary
import com.jacobao.pokemon.ui.theme.PokemonTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable

@Serializable
data object PokemonList : NavKey

@Composable
fun PokemonListScreenWrapper(
  viewModel: PokemonListViewModel,
  onPokemonClick: (pokemonId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val pokemon = viewModel.pokemon.collectAsLazyPagingItems()
  PokemonListScreen(
    pokemon = pokemon,
    onPokemonClick = onPokemonClick,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
  pokemon: LazyPagingItems<PokemonSummary>,
  onPokemonClick: (pokemonId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
    val refreshState = pokemon.loadState.refresh
    PullToRefreshBox(
      // The full-screen loading state already shows progress when there are no items, so only show
      // the pull to refresh loading state when there are items
      isRefreshing = refreshState is LoadState.Loading && pokemon.itemCount > 0,
      onRefresh = pokemon::refresh,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    ) {
      when {
        // Only take over the whole screen while there is nothing to show.
        pokemon.itemCount == 0 && refreshState is LoadState.Loading -> {
          FullScreenScrollable {
            CircularProgressIndicator()
          }
        }

        pokemon.itemCount == 0 && refreshState is LoadState.Error -> {
          FullScreenScrollable {
            LoadErrorMessage(
              message = stringResource(R.string.couldnt_load_pokemon),
              onRetry = pokemon::refresh,
            )
          }
        }

        else -> {
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

/**
 * Centers [content] in a scrollable container that fills the screen, so pull to refresh can be
 * triggered from non-list states.
 */
@Composable
private fun FullScreenScrollable(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  LazyColumn(modifier = modifier.fillMaxSize()) {
    item {
      Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
        content()
      }
    }
  }
}

@Composable
private fun PokemonListContent(
  pokemon: LazyPagingItems<PokemonSummary>,
  onPokemonClick: (pokemonId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(modifier = modifier) {
    items(
      count = pokemon.itemCount,
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
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    Text(text = pokemon.name, style = MaterialTheme.typography.titleMedium)
    Text(
      text = pokemon.url,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun LoadErrorMessage(
  message: String,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(text = message, textAlign = TextAlign.Center)
    Button(onClick = onRetry) {
      Text(stringResource(R.string.retry))
    }
  }
}

private val previewPokemon = listOf("bulbasaur", "ivysaur", "venusaur").mapIndexed { index, name ->
  PokemonSummary(name = name, url = "https://pokeapi.co/api/v2/pokemon/${index + 1}/")
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
