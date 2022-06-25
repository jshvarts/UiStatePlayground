package com.example.uistateplayground.data.source

import com.example.uistateplayground.data.repo.MovieRepository
import com.example.uistateplayground.data.repo.OfflineFirstMovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
  @Binds
  fun bindsMoviesRepository(moviesRepository: OfflineFirstMovieRepository): MovieRepository
}