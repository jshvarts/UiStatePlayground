package com.example.uistateplayground.data.repo

import com.example.uistateplayground.data.model.Movie
import com.example.uistateplayground.data.model.MovieGenre
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
  fun getTopRatedMoviesStream(): Flow<List<Movie>>
  fun getMoviesStream(genre: MovieGenre): Flow<List<Movie>>
  suspend fun refreshTopRated()
  suspend fun refreshGenre(genre: MovieGenre)
}