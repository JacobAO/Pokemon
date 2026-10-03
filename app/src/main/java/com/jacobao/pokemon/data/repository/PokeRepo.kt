package com.jacobao.pokemon.data.repository

import com.jacobao.pokemon.data.model.PokemonDetail
import com.jacobao.pokemon.data.model.PokemonPage

/**
 * Single entry point for PokeAPI data
 */
interface PokeRepo {

  /** Loads up to [limit] Pokemon starting at [offset] in the full Pokemon list. */
  suspend fun getPokemonPage(offset: Int, limit: Int): PokemonPage

  /** Loads details for the Pokemon with the given [name]. */
  suspend fun getPokemon(name: String): PokemonDetail
}
