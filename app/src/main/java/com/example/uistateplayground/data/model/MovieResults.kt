package com.example.uistateplayground.data.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson

data class MovieResults(
  val results: List<Movie>
)

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class WrapperMovieResults

class MoviesJsonConverter {
  @WrapperMovieResults
  @FromJson
  fun fromJson(json: MovieResults): List<Movie> {
    return json.results
  }

  @ToJson
  fun toJson(@WrapperMovieResults value: List<Movie>): MovieResults {
    throw UnsupportedOperationException()
  }
}