package com.jacobao.pokemon.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jacobao.pokemon.data.model.PokemonSummary
import com.jacobao.pokemon.data.repository.PokeRepo
import timber.log.Timber

/**
 * Pages through the full Pokemon list, keyed by offset. Always starts from the beginning of the
 * list and only appends, so there is no previous key.
 *
 * Note that the API returns a full URL for the next page with the offset pre-calculated. However
 * we will calcuate the next URL ourselves so we always load from the known URL instead of loading
 * a URL from a potentially untrusted API that I didn't create
 */
class PokemonPagingSource(
  private val pokeRepo: PokeRepo,
) : PagingSource<Int, PokemonSummary>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonSummary> {
    val offset = params.key ?: 0
    return try {
      val response = pokeRepo.getPokemonPage(offset = offset, limit = params.loadSize)
      val lastLoadedOffset = offset + response.results.size
      
      // API returns null for next if there is no more data, but this behavior doesn't seem to be
      // explicitly documented, so check the count as well.
      val nextKey = if (response.next == null || lastLoadedOffset >= response.count) {
        null
      } else {
        offset + response.results.size
      }
      
      // expect API to return items with unique name, but filter duplicates as a backup since
      // the UI uses it as a lazy list key and therefore would crash on duplicates
      val uniqueResults = response.results.distinctBy { it.name }
      
      LoadResult.Page(
        data = uniqueResults,
        prevKey = null,
        nextKey = nextKey,
      )
    } catch (e: Exception) {
      Timber.e(e, "Failed to load Pokemon at offset %d", offset)
      LoadResult.Error(e)
    }
  }

  // Refreshes restart from the beginning of the list.
  override fun getRefreshKey(state: PagingState<Int, PokemonSummary>): Int? = null
}
