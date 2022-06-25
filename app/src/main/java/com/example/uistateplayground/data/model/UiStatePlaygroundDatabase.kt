package com.example.uistateplayground.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.uistateplayground.data.source.MovieDao

@Database(
  entities = [
    MovieEntity::class
  ],
  version = 1,
  exportSchema = true
)
abstract class UiStatePlaygroundDatabase : RoomDatabase() {
  abstract fun movieDao(): MovieDao
}
