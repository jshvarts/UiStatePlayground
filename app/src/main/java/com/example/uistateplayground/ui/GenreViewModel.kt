package com.example.uistateplayground.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uistateplayground.core.Result
import com.example.uistateplayground.core.asResult
import com.example.uistateplayground.data.model.Movie
import com.example.uistateplayground.data.model.MovieGenre
import com.example.uistateplayground.data.repo.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GenreUiState {
  data class Success(val movies: List<Movie>) : GenreUiState
  object Error : GenreUiState
  object Loading : GenreUiState
}

data class GenreScreenUiState(
  val genreState: GenreUiState
)

@HiltViewModel
class GenreViewModel @Inject constructor(
  private val movieRepository: MovieRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(GenreScreenUiState(GenreUiState.Loading))
  val uiState = _uiState.asStateFlow()

  fun fetchMovies(genre: MovieGenre) {
    viewModelScope.launch {
      movieRepository.getMoviesStream(genre).asResult()
        .collect { result ->
          val genreUiState = when (result) {
            is Result.Success -> GenreUiState.Success(result.data)
            is Result.Loading -> GenreUiState.Loading
            is Result.Error -> GenreUiState.Error
          }

          _uiState.value = GenreScreenUiState(genreUiState)
        }
    }
  }
}