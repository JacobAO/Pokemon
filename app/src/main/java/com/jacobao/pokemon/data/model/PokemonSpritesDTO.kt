package com.jacobao.pokemon.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Image URLs for a [PokemonDetailDTO]. */
@Serializable
data class PokemonSpritesDTO(
  @SerialName("front_default")
  val frontDefaultUrl: String?,
)
