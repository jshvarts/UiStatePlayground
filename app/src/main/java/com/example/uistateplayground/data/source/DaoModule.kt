package com.example.uistateplayground.data.source

import com.example.uistateplayground.data.model.UiStatePlaygroundDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
  @Provides
  fun providesAuthorDao(
    database: UiStatePlaygroundDatabase,
  ): MovieDao = database.movieDao()
}