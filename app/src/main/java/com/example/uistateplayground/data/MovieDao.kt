package com.example.uistateplayground.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
  @Query(
    value = """
        SELECT * FROM movie
        WHERE id = :id
    """
  )
  fun getMovieStream(id: Int): Flow<MovieEntity>

  @Query(value = "SELECT * FROM movie")
  fun getMoviesStream(): Flow<List<MovieEntity>>

  /**
   * Inserts [movies] into the db if they don't exist, and ignores those that do
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertOrIgnoreMovies(movies: List<MovieEntity>): List<Long>

  /**
   * Updates [movies] in the db that match the primary key, and no-ops if they don't
   */
  @Update
  suspend fun updateMovies(movies: List<MovieEntity>)

  /**
   * Inserts or updates [movies] in the db under the specified primary keys
   */
  @Transaction
  suspend fun upsertMovies(entities: List<MovieEntity>) = upsert(
    items = entities,
    insertMany = ::insertOrIgnoreMovies,
    updateMany = ::updateMovies
  )

  /**
   * Deletes rows in the db matching the specified [ids]
   */
  @Query(
    value = """
            DELETE FROM movie
            WHERE id in (:ids)
        """
  )
  suspend fun deleteMovies(ids: List<Int>)
}