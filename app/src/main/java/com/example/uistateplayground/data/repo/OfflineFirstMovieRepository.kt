package com.example.uistateplayground.data.repo

import com.example.uistateplayground.data.model.*
import com.example.uistateplayground.data.source.Api
import com.example.uistateplayground.data.source.MovieDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class OfflineFirstMovieRepository @Inject constructor(
  private val dao: MovieDao,
  private val api: Api
) : MovieRepository {

  override fun getTopRatedMoviesStream(): Flow<List<Movie>> {
    return dao.getTopRatedMoviesStream().map { entityMovies ->
      entityMovies.map(MovieEntity::asExternalModel)
    }.onEach {
      if (it.isEmpty()) {
        refreshTopRated()
      }
    }
  }

  override fun getMoviesStream(genre: MovieGenre): Flow<List<Movie>> {
    return dao.getGenreMoviesStream(genre.id).map { entityMovies ->
      entityMovies.map(MovieEntity::asExternalModel)
    }.onEach {
      if (it.isEmpty()) {
        refreshGenre(genre)
      }
    }
  }

  override suspend fun refreshTopRated() {
    api.getTopRated()
      .shuffled()
      .also { externalMovies ->
        dao.deleteAndInsert(movies = externalMovies.map(Movie::asEntity))
      }
  }

  override suspend fun refreshGenre(genre: MovieGenre) {
    api.getMoviesForGenre(genre.id)
      .shuffled()
      .also { externalMovies ->
        dao.deleteAndInsert(
          genre.id, externalMovies.map { it.asEntity(genreId = genre.id) }
        )
      }
  }
}