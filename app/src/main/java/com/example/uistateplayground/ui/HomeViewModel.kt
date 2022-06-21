package com.example.uistateplayground.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uistateplayground.WhileUiSubscribed
import com.example.uistateplayground.core.Result
import com.example.uistateplayground.core.asResult
import com.example.uistateplayground.data.Movie
import com.example.uistateplayground.data.MovieGenre
import com.example.uistateplayground.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
  val topRatedMovies: TopRatedMoviesUiState,
  val actionMovies: ActionMoviesUiState,
  val animationMovies: AnimationMoviesUiState
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
  movieRepository: MovieRepository
) : ViewModel() {
  private val topRatedMovies: Flow<Result<List<Movie>>> =
    movieRepository.getTopRatedMoviesStream().asResult()

  private val actionMovies: Flow<Result<List<Movie>>> =
    movieRepository.getMoviesStream(MovieGenre.ACTION).asResult()

  private val animationMovies: Flow<Result<List<Movie>>> =
    movieRepository.getMoviesStream(MovieGenre.ANIMATION).asResult()

  val uiState: StateFlow<HomeUiState> = combine(
    topRatedMovies,
    actionMovies,
    animationMovies
  ) { topRatedResult, actionMoviesResult, animationMoviesResult ->

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
      animation
    )
  }
    .stateIn(
      scope = viewModelScope,
      started = WhileUiSubscribed,
      initialValue = HomeUiState(
        TopRatedMoviesUiState.Loading,
        ActionMoviesUiState.Loading,
        AnimationMoviesUiState.Loading
      )
    )
}