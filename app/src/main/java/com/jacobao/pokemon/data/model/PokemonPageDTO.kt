package com.jacobao.pokemon.data.model

import kotlinx.serialization.Serializable

/**
 * A page of results from a PokeAPI list endpoint. [next] is a URL for the next page but is only
 * used to detect the last page. See [com.jacobao.pokemon.ui.list.PokemonPagingSource] for details
 */
@Serializable
data class PokemonPageDTO(
  val count: Int,
  val next: String?,
  val results: List<PokemonSummaryDTO>,
)
