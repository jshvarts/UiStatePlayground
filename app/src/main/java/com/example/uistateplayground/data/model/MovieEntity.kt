package com.example.uistateplayground.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class MovieEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  @ColumnInfo(name = "poster_path")
  val posterPath: String,
  val genreId: String?,
)

fun MovieEntity.asExternalModel() = Movie(
  title = title,
  posterPath = posterPath,
)
