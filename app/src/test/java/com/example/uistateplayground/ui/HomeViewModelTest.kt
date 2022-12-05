package com.example.uistateplayground.ui

import app.cash.turbine.test
import com.example.uistateplayground.data.TestMovieRepository
import com.example.uistateplayground.data.model.Movie
import com.example.uistateplayground.util.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
  @get:Rule
  val dispatcherRule = TestDispatcherRule()

  private val moviesRepository = TestMovieRepository()

  private lateinit var viewModel: HomeViewModel

  @Before
  fun setup() {
    viewModel = HomeViewModel(moviesRepository)
  }

  @Test
  fun `when uiStateTopRatedMovies is initialized then shows loading`() = runTest {
    viewModel.uiState.test {
      assertEquals(UiState.Loading, awaitItem().topRatedMovies)
    }
  }

  @Test
  fun `when uiStateActionMovies is initialized then shows loading`() = runTest {
    viewModel.uiState.test {
      assertEquals(UiState.Loading, awaitItem().actionMovies)
    }
  }

  @Test
  fun `when uiStateAnimationMovies is initialized then shows loading`() = runTest {
    viewModel.uiState.test {
      assertEquals(UiState.Loading, awaitItem().animationMovies)
    }
  }

  @Test
  fun `when uiHomeState is initialized then shows correct state`() = runTest {
    viewModel.uiState.test {
      val initialState = awaitItem()
      assertEquals(UiState.Loading, initialState.topRatedMovies)
      assertEquals(UiState.Loading, initialState.actionMovies)
      assertEquals(UiState.Loading, initialState.animationMovies)
      assertFalse(initialState.isRefreshing)
    }
  }

  //@Ignore("Test that refreshing emits state with refreshing true followed by refreshing false")
  @Test
  fun `when refreshing then emits correct state`() = runTest {
    viewModel.uiState.test {
      viewModel.onRefresh()
      assertFalse(awaitItem().isRefreshing)
    }
  }

  @Test
  fun `when uiStateTopRatedMovies emits success then matches from Repository`() = runTest {
    viewModel.uiState.test {
      moviesRepository.sendTopRatedMovies(testInputTopRatedMovies)

      assertTrue(awaitItem().topRatedMovies is UiState.Loading)

      assertEquals(
        testInputTopRatedMovies,
        (awaitItem().topRatedMovies as UiState.Success).data
      )
    }
  }

  @Test
  fun `when uiStateActionMovies emits success then matches from Repository`() = runTest {
    viewModel.uiState.test {
      moviesRepository.sendActionMovies(testInputActionMovies)

      assertTrue(awaitItem().actionMovies is UiState.Loading)

      assertEquals(
        testInputActionMovies,
        (awaitItem().actionMovies as UiState.Success).data
      )
    }
  }

  @Test
  fun `when uiStateAnimationMovies emits success then matches from Repository`() = runTest {
    viewModel.uiState.test {
      moviesRepository.sendAnimationMovies(testInputAnimationMovies)

      assertTrue(awaitItem().animationMovies is UiState.Loading)

      assertEquals(
        testInputAnimationMovies,
        (awaitItem().animationMovies as UiState.Success).data
      )
    }
  }

  @Test
  fun `when movie ui states emit success then home uiState emits success for each`() =
    runTest {
      viewModel.uiState.test {
        moviesRepository.sendTopRatedMovies(testInputTopRatedMovies)
        moviesRepository.sendActionMovies(testInputActionMovies)
        moviesRepository.sendAnimationMovies(testInputAnimationMovies)

        // skip loading state
        awaitItem()

        val uiState = awaitItem()
        assertTrue(uiState.topRatedMovies is UiState.Success)
        assertTrue(uiState.actionMovies is UiState.Success)
        assertTrue(uiState.animationMovies is UiState.Success)
      }
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