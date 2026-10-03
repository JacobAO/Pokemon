package com.jacobao.pokemon.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Image URLs for a [PokemonDetail]. */
@Serializable
data class PokemonSprites(
  @SerialName("front_default")
  val frontDefaultUrl: String?,
)
