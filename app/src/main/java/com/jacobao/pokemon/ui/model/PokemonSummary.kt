package com.jacobao.pokemon.ui.model

import com.jacobao.pokemon.data.model.PokemonSummaryDTO

/** UI data model for a pokemon in the list. */
data class PokemonSummary(
  val name: String, // unique ID
)

fun PokemonSummaryDTO.toModel(): PokemonSummary = PokemonSummary(
  name = name,
)
