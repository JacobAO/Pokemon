package com.jacobao.pokemon.ui.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PokemonListState(
  val isLoading: Boolean = true,
)

@HiltViewModel
class PokemonListViewModel @Inject constructor() : ViewModel() {
  private val _state = MutableStateFlow(PokemonListState())
  val state: StateFlow<PokemonListState> = _state.asStateFlow()
}
