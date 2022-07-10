package com.example.uistateplayground.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uistateplayground.WhileUiSubscribed
import com.example.uistateplayground.core.Result
import com.example.uistateplayground.core.asResult
import com.example.uistateplayground.data.model.Movie
import com.example.uistateplayground.data.model.MovieGenre
import com.example.uistateplayground.data.repo.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class HomeUiState(
  val topRatedMovies: UiState<List<Movie>>,
  val actionMovies: UiState<List<Movie>>,
  val animationMovies: UiState<List<Movie>>,
  val isRefreshing: Boolean,
  val isError: Boolean
)

@Immutable
sealed interface UiState<out T> {
  data class Success<T>(val data: T) : UiState<T>
  object Error : UiState<Nothing>
  object Loading : UiState<Nothing>
}

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val movieRepository: MovieRepository
) : ViewModel() {

  val exceptionHandler = CoroutineExceptionHandler { context, exception ->
    viewModelScope.launch {
      isError.emit(true)
    }
  }

  private val topRatedMovies: Flow<Result<List<Movie>>> =
    movieRepository.getTopRatedMoviesStream().asResult()

  private val actionMovies: Flow<Result<List<Movie>>> =
    movieRepository.getMoviesStream(MovieGenre.ACTION).asResult()

  private val animationMovies: Flow<Result<List<Movie>>> =
    movieRepository.getMoviesStream(MovieGenre.ANIMATION).asResult()

  private val isRefreshing = MutableStateFlow(false)

  private val isError = MutableStateFlow(false)

  val uiState: StateFlow<HomeUiState> = combine(
    topRatedMovies,
    actionMovies,
    animationMovies,
    isRefreshing,
    isError
  ) { topRatedResult, actionMoviesResult, animationMoviesResult, refreshing, errorOccurred ->

    val topRated: UiState<List<Movie>> = when (topRatedResult) {
      is Result.Success -> UiState.Success(topRatedResult.data)
      is Result.Loading -> UiState.Loading
      is Result.Error -> UiState.Error
    }

    val action: UiState<List<Movie>> = when (actionMoviesResult) {
      is Result.Success -> UiState.Success(actionMoviesResult.data)
      is Result.Loading -> UiState.Loading
      is Result.Error -> UiState.Error
    }

    val animation: UiState<List<Movie>> = when (animationMoviesResult) {
      is Result.Success -> UiState.Success(animationMoviesResult.data)
      is Result.Loading -> UiState.Loading
      is Result.Error -> UiState.Error
    }

    HomeUiState(
      topRated,
      action,
      animation,
      refreshing,
      errorOccurred
    )
  }
    .stateIn(
      scope = viewModelScope,
      started = WhileUiSubscribed,
      initialValue = HomeUiState(
        UiState.Loading,
        UiState.Loading,
        UiState.Loading,
        isRefreshing = false,
        isError = false
      )
    )

  fun onRefresh() {
    viewModelScope.launch(exceptionHandler) {
      with(movieRepository) {
        val refreshTopRatedDeferred = async { refreshTopRated() }
        val refreshActionMoviesDeferred = async { refreshGenre(MovieGenre.ACTION) }
        val refreshAnimationMoviesDeferred = async { refreshGenre(MovieGenre.ANIMATION) }
        isRefreshing.emit(true)
        try {
          awaitAll(
            refreshTopRatedDeferred,
            refreshActionMoviesDeferred,
            refreshAnimationMoviesDeferred
          )
        } finally {
          isRefreshing.emit(false)
        }
      }
    }
  }

  fun onErrorConsumed() {
    viewModelScope.launch {
      isError.emit(false)
    }
  }
}