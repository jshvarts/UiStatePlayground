package com.example.uistateplayground.data

import kotlinx.coroutines.flow.Flow

interface MovieRepository {
  fun getTopRatedMoviesStream(): Flow<List<Movie>>
  fun getMoviesStream(genre: MovieGenre): Flow<List<Movie>>
}