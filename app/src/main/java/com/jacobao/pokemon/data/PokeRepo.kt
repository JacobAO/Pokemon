package com.jacobao.pokemon.data

import com.jacobao.pokemon.data.model.PokemonDetailDTO
import com.jacobao.pokemon.data.model.PokemonPageDTO

/**
 * Single entry point for PokeAPI data
 */
interface PokeRepo {
  
  /** Loads up to [limit] Pokemon starting at [offset] in the full Pokemon list. */
  suspend fun getPokemonPage(offset: Int, limit: Int): PokemonPageDTO
  
  /** Loads details for the Pokemon with the given [name]. */
  suspend fun getPokemon(name: String): PokemonDetailDTO
}
