package com.jacobao.pokemon.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacobao.pokemon.data.PokeRepo
import com.jacobao.pokemon.ui.model.PokemonDetail
import com.jacobao.pokemon.ui.model.toModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class PokemonDetailState(
  val pokemonName: String,
  val pokemonDetail: PokemonDetail? = null,
  val isLoading: Boolean = true,
  val loadFailed: Boolean = false,
)

@HiltViewModel(assistedFactory = PokemonDetailViewModel.Factory::class)
class PokemonDetailViewModel @AssistedInject constructor(
  @Assisted private val pokemonName: String,
  private val pokeRepo: PokeRepo,
) : ViewModel() {
  private val _state = MutableStateFlow(PokemonDetailState(pokemonName = pokemonName))
  val state: StateFlow<PokemonDetailState> = _state.asStateFlow()
  
  // Emits when a refresh fails while previously loaded data is still being shown.
  private val _refreshFailed = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
  val refreshFailed: SharedFlow<Unit> = _refreshFailed.asSharedFlow()
  
  private var loadJob: Job? = null
  
  init {
    load()
  }
  
  /** Loads the Pokemon, replacing any load already in progress */
  fun load() {
    loadJob?.cancel()
    loadJob = viewModelScope.launch(Dispatchers.IO) {
      _state.update { it.copy(isLoading = true, loadFailed = false) }
      try {
        val pokemon = pokeRepo.getPokemon(name = pokemonName)
        _state.update { it.copy(pokemonDetail = pokemon.toModel(), isLoading = false) }
      } catch (e: CancellationException) {
        // let canceled job quit silently
        throw e
      } catch (e: Exception) {
        Timber.e(e, "Failed to load Pokemon %s", pokemonName)
        _state.update { it.copy(isLoading = false, loadFailed = true) }
        if (_state.value.pokemonDetail != null) {
          _refreshFailed.emit(Unit)
        }
      }
    }
  }
  
  @AssistedFactory
  interface Factory {
    fun create(pokemonName: String): PokemonDetailViewModel
  }
}
