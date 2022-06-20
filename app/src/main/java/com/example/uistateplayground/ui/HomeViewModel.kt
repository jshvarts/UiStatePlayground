package com.example.uistateplayground.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uistateplayground.WhileUiSubscribed
import com.example.uistateplayground.data.Movie
import com.example.uistateplayground.data.MovieGenre
import com.example.uistateplayground.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
  val topRatedMovies: List<Movie> = emptyList(),
  val actionMovies: List<Movie> = emptyList(),
  val animationMovies: List<Movie> = emptyList(),
  val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
  movieRepository: MovieRepository
) : ViewModel() {

  val uiState: StateFlow<HomeUiState> = combine(
    movieRepository.getTopRatedMovies(),
    movieRepository.getMovies(MovieGenre.ACTION),
    movieRepository.getMovies(MovieGenre.ANIMATION)
  ) { topRated, action, animation ->
    HomeUiState(
      topRated,
      action,
      animation
    )
  }
    .stateIn(
      scope = viewModelScope,
      started = WhileUiSubscribed,
      initialValue = HomeUiState(isLoading = true)
    )
}