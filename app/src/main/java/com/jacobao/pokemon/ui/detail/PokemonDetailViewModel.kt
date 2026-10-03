package com.jacobao.pokemon.ui.detail

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PokemonDetailState(
  val pokemonId: String,
  val isLoading: Boolean = true,
)

@HiltViewModel(assistedFactory = PokemonDetailViewModel.Factory::class)
class PokemonDetailViewModel @AssistedInject constructor(
  @Assisted pokemonId: String,
) : ViewModel() {
  private val _state = MutableStateFlow(PokemonDetailState(pokemonId = pokemonId))
  val state: StateFlow<PokemonDetailState> = _state.asStateFlow()

  @AssistedFactory
  interface Factory {
    fun create(pokemonId: String): PokemonDetailViewModel
  }
}
