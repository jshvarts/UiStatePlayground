package com.example.uistateplayground.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

private const val POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342/"

@JsonClass(generateAdapter = true)
data class Movie(
  val title: String,
  @Json(name = "poster_path") val posterPath: String
) {
  val posterUrl: String by lazy { POSTER_IMAGE_BASE_URL + posterPath }
}

fun Movie.asEntity(genreId: String? = null) = MovieEntity(
  title = title,
  posterPath = posterPath,
  genreId = genreId
)
