package com.jacobao.pokemon.data

import com.jacobao.pokemon.data.model.PokemonDetailDTO
import com.jacobao.pokemon.data.model.PokemonPageDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit definition of the PokeAPI endpoints. Paths are relative to the versioned base URL
 * supplied by the network module, e.g. `https://pokeapi.co/api/v2/`.
 */
interface PokeService {

  @GET("pokemon")
  suspend fun getPokemonPage(
    @Query("offset") offset: Int,
    @Query("limit") limit: Int,
  ): PokemonPageDTO

  @GET("pokemon/{name}/")
  suspend fun getPokemon(@Path("name") name: String): PokemonDetailDTO
}