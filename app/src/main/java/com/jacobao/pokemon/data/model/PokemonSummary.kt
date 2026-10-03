package com.jacobao.pokemon.data.model

import kotlinx.serialization.Serializable

/** A named reference to a Pokemon as returned by the PokeAPI list endpoint. */
@Serializable
data class PokemonSummary(
  val name: String, // unique ID
  val url: String,
)
