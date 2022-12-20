package com.example.uistateplayground.ui

import app.cash.turbine.test
import com.example.uistateplayground.data.TestMovieRepository
import com.example.uistateplayground.data.model.Movie
import com.example.uistateplayground.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val moviesRepository = TestMovieRepository()

  private lateinit var viewModel: HomeViewModel

  @Before
  fun setup() {
    viewModel = HomeViewModel(moviesRepository)
  }

  @Test
  fun `when uiStateTopRatedMovies is initialized then shows loading`() = runTest {
    viewModel.uiState.test {
      assertEquals(TopRatedMoviesUiState.Loading, awaitItem().topRatedMovies)
    }
  }

  @Test
  fun `when uiStateActionMovies is initialized then shows loading`() = runTest {
    viewModel.uiState.test {
      assertEquals(ActionMoviesUiState.Loading, awaitItem().actionMovies)
    }
  }

  @Test
  fun `when uiStateAnimationMovies is initialized then shows loading`() = runTest {
    viewModel.uiState.test {
      assertEquals(AnimationMoviesUiState.Loading, awaitItem().animationMovies)
    }
  }

  @Test
  fun `when uiHomeState is initialized then shows correct state`() = runTest {
    viewModel.uiState.test {
      val initialState = awaitItem()
      assertEquals(TopRatedMoviesUiState.Loading, initialState.topRatedMovies)
      assertEquals(ActionMoviesUiState.Loading, initialState.actionMovies)
      assertEquals(AnimationMoviesUiState.Loading, initialState.animationMovies)
      assertFalse(initialState.isRefreshing)
    }
  }

  @Test
  fun `when uiStateTopRatedMovies emits success then matches from Repository`() = runTest {
    viewModel.uiState.test {
      moviesRepository.sendTopRatedMovies(testInputTopRatedMovies)

      assertTrue(awaitItem().topRatedMovies is TopRatedMoviesUiState.Loading)

      assertEquals(
        testInputTopRatedMovies,
        (awaitItem().topRatedMovies as TopRatedMoviesUiState.Success).movies
      )
    }
  }

  @Test
  fun `when uiStateActionMovies emits success then matches from Repository`() = runTest {
    viewModel.uiState.test {
      moviesRepository.sendActionMovies(testInputActionMovies)

      assertTrue(awaitItem().actionMovies is ActionMoviesUiState.Loading)

      assertEquals(
        testInputActionMovies,
        (awaitItem().actionMovies as ActionMoviesUiState.Success).movies
      )
    }
  }

  @Test
  fun `when uiStateAnimationMovies emits success then matches from Repository`() = runTest {
    viewModel.uiState.test {
      moviesRepository.sendAnimationMovies(testInputAnimationMovies)

      assertTrue(awaitItem().animationMovies is AnimationMoviesUiState.Loading)

      assertEquals(
        testInputAnimationMovies,
        (awaitItem().animationMovies as AnimationMoviesUiState.Success).movies
      )
    }
  }

  @Test
  fun `when movie ui states emit success then home uiState emits success for each`() =
    runTest {
      val collectJob = launch {
        viewModel.uiState.collect()

        viewModel.uiState.test {
          moviesRepository.sendTopRatedMovies(testInputTopRatedMovies)
          moviesRepository.sendActionMovies(testInputActionMovies)
          moviesRepository.sendAnimationMovies(testInputAnimationMovies)

          // skip loading state
          awaitItem()

          val uiState = awaitItem()
          assertTrue(uiState.topRatedMovies is TopRatedMoviesUiState.Success)
          assertTrue(uiState.actionMovies is ActionMoviesUiState.Success)
          assertTrue(uiState.animationMovies is AnimationMoviesUiState.Success)
        }
      }
      collectJob.cancel()
    }
}

private val testInputTopRatedMovies = listOf(
  Movie(
    "movie a",
    "/movie-a.jpg"
  ),
  Movie(
    "movie b",
    "/movie-b.jpg"
  )
)

private val testInputActionMovies = listOf(
  Movie(
    "movie c",
    "/movie-c.jpg"
  )
)

private val testInputAnimationMovies = listOf(
  Movie(
    "movie a",
    "/movie-a.jpg"
  ),
  Movie(
    "movie b",
    "/movie-b.jpg"
  ),
  Movie(
    "movie d",
    "/movie-d.jpg"
  )
)