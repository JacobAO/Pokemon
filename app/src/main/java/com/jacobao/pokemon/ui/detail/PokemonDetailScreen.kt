package com.jacobao.pokemon.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import com.jacobao.pokemon.R
import com.jacobao.pokemon.data.model.PokemonDetail
import com.jacobao.pokemon.data.model.PokemonSprites
import com.jacobao.pokemon.navigation.LocalBackButtonVisibility
import com.jacobao.pokemon.ui.common.LoadErrorMessage
import com.jacobao.pokemon.ui.theme.PokemonTheme
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetailRoute(val pokemonName: String) : NavKey

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
    onRefresh = viewModel::load,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
  state: PokemonDetailState,
  onBack: () -> Unit,
  onRefresh: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(state.pokemonName) },
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
    val contentModifier = Modifier
      .fillMaxSize()
      .padding(innerPadding)
    when {
      // A failed refresh keeps showing previously loaded data.
      state.pokemonDetail != null -> {
        PullToRefreshBox(
          isRefreshing = state.isLoading,
          onRefresh = onRefresh,
          modifier = contentModifier,
        ) {
          PokemonDetailContent(
            pokemonDetail = state.pokemonDetail,
            modifier = Modifier.fillMaxSize(),
          )
        }
      }

      state.loadFailed -> {
        Box(
          modifier = contentModifier,
          contentAlignment = Alignment.Center,
        ) {
          LoadErrorMessage(
            message = stringResource(R.string.couldnt_load_pokemon_details),
            onRetry = onRefresh,
          )
        }
      }

      else -> {
        Box(
          modifier = contentModifier,
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      }
    }
  }
}

@Composable
private fun PokemonDetailContent(
  pokemonDetail: PokemonDetail,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    StringField(label = stringResource(R.string.pokemon_id), value = pokemonDetail.id?.toString())
    StringField(label = stringResource(R.string.height_decimeters), value = pokemonDetail.height?.toString())
    StringField(label = stringResource(R.string.weight_hectograms), value = pokemonDetail.weight?.toString())
    SpriteField(spriteUrl = pokemonDetail.sprites?.frontDefaultUrl)
  }
}

@Composable
private fun LabelContent(
  modifier: Modifier = Modifier,
  label: String,
  content: @Composable (ColumnScope.() -> Unit),
) {
  Column(modifier = modifier) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    content()
  }
}

@Composable
private fun StringField(
  modifier: Modifier = Modifier,
  label: String,
  value: String?,
) {
  LabelContent(
    label = label,
    modifier = modifier,
  ) {
    Text(
      text = value ?: stringResource(R.string.unknown),
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@Composable
private fun SpriteField(
  modifier: Modifier = Modifier,
  spriteUrl: String?,
) {
  LabelContent(
    label = stringResource(R.string.front_default_sprite),
    modifier = modifier,
  ) {
    if (spriteUrl != null) {
      AsyncImage(
        model = spriteUrl,
        contentDescription = stringResource(R.string.front_default_sprite),
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.sizeIn(minWidth = 200.dp, minHeight = 200.dp),
      )
    } else {
      Text(text = stringResource(R.string.unknown), style = MaterialTheme.typography.bodyLarge)
    }
  }
}

private val previewPokemonDetail = PokemonDetail(
  id = 1,
  name = "bulbasaur",
  height = 7,
  weight = 69,
  sprites = PokemonSprites(
    frontDefaultUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
  ),
)

@Preview(showBackground = true)
@Composable
private fun PokemonDetailScreenPreview() {
  PokemonTheme {
    PokemonDetailScreen(
      state = PokemonDetailState(
        pokemonName = previewPokemonDetail.name,
        pokemonDetail = previewPokemonDetail,
        isLoading = false,
      ),
      onBack = {},
      onRefresh = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun PokemonDetailScreenLoadingPreview() {
  PokemonTheme {
    PokemonDetailScreen(
      state = PokemonDetailState(pokemonName = "bulbasaur"),
      onBack = {},
      onRefresh = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun PokemonDetailScreenErrorPreview() {
  PokemonTheme {
    PokemonDetailScreen(
      state = PokemonDetailState(pokemonName = "bulbasaur", isLoading = false, loadFailed = true),
      onBack = {},
      onRefresh = {},
    )
  }
}
