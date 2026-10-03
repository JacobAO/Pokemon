package com.jacobao.pokemon.di

import com.jacobao.pokemon.data.PokeRepo
import com.jacobao.pokemon.data.PokeRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindPokeRepo(impl: PokeRepoImpl): PokeRepo
}
