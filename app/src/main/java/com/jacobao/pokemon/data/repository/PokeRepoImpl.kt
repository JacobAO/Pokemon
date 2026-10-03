package com.jacobao.pokemon.data.repository

import com.jacobao.pokemon.data.model.PokemonDetail
import com.jacobao.pokemon.data.model.PokemonPage
import com.jacobao.pokemon.data.remote.PokeService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokeRepoImpl @Inject constructor(
  private val pokeService: PokeService,
) : PokeRepo {

  override suspend fun getPokemonPage(offset: Int, limit: Int): PokemonPage =
    withContext(Dispatchers.IO) {
      pokeService.getPokemonPage(offset = offset, limit = limit)
    }

  override suspend fun getPokemon(name: String): PokemonDetail =
    withContext(Dispatchers.IO) {
      pokeService.getPokemon(name = name)
    }
}
