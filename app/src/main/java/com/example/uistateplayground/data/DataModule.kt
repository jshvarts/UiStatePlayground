package com.example.uistateplayground.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
  @Binds
  fun bindsMoviesRepository(moviesRepository: MovieRepositoryImpl): MovieRepository
}