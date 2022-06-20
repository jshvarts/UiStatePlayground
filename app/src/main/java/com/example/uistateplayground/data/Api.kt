package com.example.uistateplayground.data

import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

  @GET("movie/top_rated")
  @WrapperMovieResults
  suspend fun getTopRated(): List<Movie>

  @GET("discover/movie")
  @WrapperMovieResults
  suspend fun getMoviesForGenre(@Query("with_genres") ids: String): List<Movie>
}