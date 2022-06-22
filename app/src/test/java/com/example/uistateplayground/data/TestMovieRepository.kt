package com.example.uistateplayground.data

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestMovieRepository : MovieRepository {
  /**
   * The backing hot flow for the list of top movies for testing.
   */
  private val topRatedFlow: MutableSharedFlow<List<Movie>> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  /**
   * The backing hot flow for the list of action movies for testing.
   */
  private val actionMoviesFlow: MutableSharedFlow<List<Movie>> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  /**
   * The backing hot flow for the list of animation movies for testing.
   */
  private val animationMoviesFlow: MutableSharedFlow<List<Movie>> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  override fun getTopRatedMoviesStream(): Flow<List<Movie>> = topRatedFlow

  override fun getMoviesStream(genre: MovieGenre): Flow<List<Movie>> = when (genre) {
    MovieGenre.ACTION -> actionMoviesFlow
    MovieGenre.ANIMATION -> animationMoviesFlow
  }

  /**
   * A test-only API to allow controlling the list of top rated from tests.
   */
  fun sendTopRatedMovies(movies: List<Movie>) {
    topRatedFlow.tryEmit(movies)
  }

  /**
   * A test-only API to allow controlling the list of action movies from tests.
   */
  fun sendActionMovies(movies: List<Movie>) {
    actionMoviesFlow.tryEmit(movies)
  }

  /**
   * A test-only API to allow controlling the list of animation movies from tests.
   */
  fun sendAnimationMovies(movies: List<Movie>) {
    animationMoviesFlow.tryEmit(movies)
  }
}