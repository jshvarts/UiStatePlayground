package com.example.uistateplayground.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class MovieEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  @ColumnInfo(name = "poster_path")
  val posterPath: String
)

fun MovieEntity.asExternalModel() = Movie(
  title = title,
  posterPath = posterPath
)
