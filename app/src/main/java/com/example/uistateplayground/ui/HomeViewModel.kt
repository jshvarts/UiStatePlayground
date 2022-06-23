package com.example.uistateplayground.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uistateplayground.core.Result
import com.example.uistateplayground.core.asResult
import com.example.uistateplayground.data.Movie
import com.example.uistateplayground.data.MovieGenre
import com.example.uistateplayground.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
  val topRatedMovies: TopRatedMoviesUiState,
  val actionMovies: ActionMoviesUiState,
  val animationMovies: AnimationMoviesUiState,
  val isRefreshing: Boolean
)

sealed interface TopRatedMoviesUiState {
  data class Success(val movies: List<Movie>) : TopRatedMoviesUiState
  object Error : TopRatedMoviesUiState
  object Loading : TopRatedMoviesUiState
}

sealed interface ActionMoviesUiState {
  data class Success(val movies: List<Movie>) : ActionMoviesUiState
  object Error : ActionMoviesUiState
  object Loading : ActionMoviesUiState
}

sealed interface AnimationMoviesUiState {
  data class Success(val movies: List<Movie>) : AnimationMoviesUiState
  object Error : AnimationMoviesUiState
  object Loading : AnimationMoviesUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val movieRepository: MovieRepository
) : ViewModel() {
  private val topRatedMovies: Flow<Result<List<Movie>>> =
    movieRepository.getTopRatedMoviesStream().asResult()

  private val actionMovies: Flow<Result<List<Movie>>> =
    movieRepository.getMoviesStream(MovieGenre.ACTION).asResult()

  private val animationMovies: Flow<Result<List<Movie>>> =
    movieRepository.getMoviesStream(MovieGenre.ANIMATION).asResult()

  private val isRefreshing = MutableStateFlow(false)

  private var _uiState = MutableStateFlow<HomeUiState>(
    HomeUiState(
      TopRatedMoviesUiState.Loading,
      ActionMoviesUiState.Loading,
      AnimationMoviesUiState.Loading,
      isRefreshing = false
    )
  )
  val uiState = _uiState.asStateFlow()

  init {
    observeStateChanges()
  }

  fun onRefresh() {
    isRefreshing.tryEmit(true)
    observeStateChanges()
    isRefreshing.tryEmit(false)
  }

  private fun observeStateChanges() {
    viewModelScope.launch {
      combine(
        movieRepository.getTopRatedMoviesStream().asResult(),
        movieRepository.getMoviesStream(MovieGenre.ACTION).asResult(),
        movieRepository.getMoviesStream(MovieGenre.ANIMATION).asResult(),
        isRefreshing
      ) { topRatedResult, actionMoviesResult, animationMoviesResult, refreshing ->

        val topRated: TopRatedMoviesUiState = when (topRatedResult) {
          is Result.Success -> TopRatedMoviesUiState.Success(topRatedResult.data)
          is Result.Loading -> TopRatedMoviesUiState.Loading
          is Result.Error -> TopRatedMoviesUiState.Error
        }

        val action: ActionMoviesUiState = when (actionMoviesResult) {
          is Result.Success -> ActionMoviesUiState.Success(actionMoviesResult.data)
          is Result.Loading -> ActionMoviesUiState.Loading
          is Result.Error -> ActionMoviesUiState.Error
        }

        val animation: AnimationMoviesUiState = when (animationMoviesResult) {
          is Result.Success -> AnimationMoviesUiState.Success(animationMoviesResult.data)
          is Result.Loading -> AnimationMoviesUiState.Loading
          is Result.Error -> AnimationMoviesUiState.Error
        }

        HomeUiState(
          topRated,
          action,
          animation,
          refreshing
        )
      }.collect { homeUiState ->
        _uiState.value = homeUiState
      }
    }
  }
}