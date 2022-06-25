package com.example.uistateplayground.data.source

import androidx.room.*
import com.example.uistateplayground.data.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
  /**
   * Gets top rated [movies] from the db
   */
  @Query(value = "SELECT * FROM movie WHERE genreId is null")
  fun getTopRatedMoviesStream(): Flow<List<MovieEntity>>

  /**
   * Gets [movies] from the db for a given genre
   */
  @Query(value = "SELECT * FROM movie WHERE genreId = :genreId")
  fun getGenreMoviesStream(genreId: String): Flow<List<MovieEntity>>

  @Query(value = "DELETE FROM movie WHERE genreId = :genreId")
  suspend fun deleteMoviesForGenre(genreId: String)

  @Query(value = "DELETE FROM movie")
  suspend fun deleteMovies()

  /**
   * Inserts [movies] into the db if they don't exist, and ignores those that do
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertOrIgnoreMovies(movies: List<MovieEntity>): List<Long>

  @Transaction
  suspend fun deleteAndInsert(genreId: String? = null, movies: List<MovieEntity>) {
    if (genreId != null) {
      deleteMoviesForGenre(genreId)
    } else {
      deleteMovies()
    }
    insertOrIgnoreMovies(movies)
  }
}