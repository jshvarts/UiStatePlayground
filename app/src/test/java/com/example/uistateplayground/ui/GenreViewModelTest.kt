package com.example.uistateplayground.ui

import app.cash.turbine.test
import com.example.uistateplayground.data.TestMovieRepository
import com.example.uistateplayground.data.model.Movie
import com.example.uistateplayground.data.model.MovieGenre
import com.example.uistateplayground.util.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GenreViewModelTest {
  @get:Rule
  val dispatcherRule = TestDispatcherRule()

  private val moviesRepository = TestMovieRepository()

  private lateinit var viewModel: GenreViewModel

  @Before
  fun setup() {
    viewModel = GenreViewModel(moviesRepository)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `when uiState is success matches from Repository`() = runTest {
    viewModel.uiState.test {
      viewModel.fetchMovies(MovieGenre.ACTION)
      moviesRepository.sendActionMovies(testInputMovies)

      Assert.assertTrue(awaitItem().genreState is GenreUiState.Loading)
      Assert.assertEquals(testInputMovies, (awaitItem().genreState as GenreUiState.Success).movies)
    }
  }
}

private val testInputMovies = listOf(
  Movie(
    "movie a",
    "/movie-a.jpg"
  ),
  Movie(
    "movie b",
    "/movie-b.jpg"
  )
)