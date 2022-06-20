package com.example.uistateplayground.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepository @Inject constructor(private val api: Api) {

  fun getTopRatedMovies(): Flow<List<Movie>> {
    return flow {
      emit(api.getTopRated())
    }
  }

  fun getMovies(genre: MovieGenre): Flow<List<Movie>> {
    return flow {
      emit(api.getMoviesForGenre(genre.id))
    }
  }
}