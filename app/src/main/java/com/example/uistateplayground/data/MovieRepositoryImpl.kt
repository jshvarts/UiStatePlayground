package com.example.uistateplayground.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class MovieRepositoryImpl @Inject constructor(private val api: Api) : MovieRepository {

  override fun getTopRatedMoviesStream(): Flow<List<Movie>> {
    return flow {
      emit(api.getTopRated())
    }
  }

  override fun getMoviesStream(genre: MovieGenre): Flow<List<Movie>> {
    // test swipe refresh by returning data for random genres
    //    val genre = when (Random.nextInt(1, 10)) {
    //      1 -> "28"
    //      2 -> "12"
    //      3 -> "16"
    //      4 -> "35"
    //      5 -> "80"
    //      else -> "99"
    //    }
    return flow {
      emit(api.getMoviesForGenre(genre.id))
    }
  }
}