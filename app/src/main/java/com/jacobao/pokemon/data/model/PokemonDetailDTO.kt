package com.jacobao.pokemon.data.model

import kotlinx.serialization.Serializable

/** Details for a single Pokemon as returned by the PokeAPI `pokemon/{name}` endpoint. */
@Serializable
data class PokemonDetailDTO(
  val id: Int?,
  val name: String,
  val height: Int?,
  val weight: Int?,
  val sprites: PokemonSpritesDTO?,
)
