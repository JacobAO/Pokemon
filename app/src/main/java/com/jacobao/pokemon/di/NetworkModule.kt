package com.jacobao.pokemon.di

import com.jacobao.pokemon.BuildConfig
import com.jacobao.pokemon.data.PokeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  private const val TIMEOUT_SECONDS = 30L
  private const val POKE_API_BASE_URL = "https://pokeapi.co/api/v2/"

  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder()
      .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
      .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
      .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
      val logger = HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }
      logger.level = HttpLoggingInterceptor.Level.BODY
      builder.addInterceptor(logger)
    }

    return builder.build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(
    okHttpClient: OkHttpClient,
    json: Json,
  ): Retrofit = Retrofit.Builder()
    .baseUrl(POKE_API_BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

  @Provides
  @Singleton
  fun providePokeService(retrofit: Retrofit): PokeService = retrofit.create(PokeService::class.java)
}
