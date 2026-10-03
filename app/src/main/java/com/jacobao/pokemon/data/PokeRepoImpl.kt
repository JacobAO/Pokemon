package com.jacobao.pokemon.data

import com.jacobao.pokemon.data.model.PokemonDetailDTO
import com.jacobao.pokemon.data.model.PokemonPageDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PokeRepoImpl @Inject constructor(
  private val pokeService: PokeService,
) : PokeRepo {
  
  override suspend fun getPokemonPage(offset: Int, limit: Int): PokemonPageDTO =
    withContext(Dispatchers.IO) {
      pokeService.getPokemonPage(offset = offset, limit = limit)
    }
  
  override suspend fun getPokemon(name: String): PokemonDetailDTO =
    withContext(Dispatchers.IO) {
      pokeService.getPokemon(name = name)
    }
}
