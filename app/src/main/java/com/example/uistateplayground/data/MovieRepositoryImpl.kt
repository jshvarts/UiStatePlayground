package com.example.uistateplayground.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(private val api: Api) : MovieRepository {

  override fun getTopRatedMoviesStream(): Flow<List<Movie>> {
    return flow {
      emit(api.getTopRated())
    }
  }

  override fun getMoviesStream(genre: MovieGenre): Flow<List<Movie>> {
    return flow {
      emit(api.getMoviesForGenre(genre.id))
    }
  }
}