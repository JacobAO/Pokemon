package com.jacobao.pokemon.ui.model

import com.jacobao.pokemon.data.model.PokemonDetailDTO

/** UI data model for a single Pokemon. */
data class PokemonDetail(
  val id: Int?,
  val name: String,
  val height: Int?,
  val weight: Int?,
  val frontDefaultSprite: String?,
)

fun PokemonDetailDTO.toModel(): PokemonDetail = PokemonDetail(
  id = id,
  name = name,
  height = height,
  weight = weight,
  frontDefaultSprite = sprites?.frontDefaultUrl,
)
