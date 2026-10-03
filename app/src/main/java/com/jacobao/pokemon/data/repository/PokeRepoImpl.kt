package com.jacobao.pokemon.data.repository

import com.jacobao.pokemon.data.remote.PokeService
import javax.inject.Inject

class PokeRepoImpl @Inject constructor(
  private val pokeService: PokeService,
) : PokeRepo
