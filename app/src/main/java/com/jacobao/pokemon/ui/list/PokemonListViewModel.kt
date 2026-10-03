package com.jacobao.pokemon.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jacobao.pokemon.data.model.PokemonSummary
import com.jacobao.pokemon.data.paging.PokemonPagingSource
import com.jacobao.pokemon.data.repository.PokeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class PokemonListViewModel @Inject constructor(
  pokeRepo: PokeRepo,
) : ViewModel() {

  // Cached in the ViewModel scope so loaded pages survive recomposition and configuration changes.
  // This ViewModel belongs to the root nav entry, so the cache lasts for the app's lifetime.
  val pokemon: Flow<PagingData<PokemonSummary>> = Pager(
    config = PagingConfig(
      pageSize = PAGE_SIZE,
      initialLoadSize = PAGE_SIZE,
      prefetchDistance = PAGE_SIZE, // load 1 page in advance to ensure smooth scrolling
    ),
    pagingSourceFactory = { PokemonPagingSource(pokeRepo) },
  ).flow.cachedIn(viewModelScope)

  private companion object {
    const val PAGE_SIZE = 20
  }
}
