package com.example.uistateplayground.data

/**
 * Performs an upsert by first attempting to insert [items] using [insertMany] with the the result
 * of the inserts returned.
 *
 * Items that were not inserted due to conflicts are then updated using [updateMany]
 */
suspend fun <T> upsert(
  items: List<T>,
  insertMany: suspend (List<T>) -> List<Long>,
  updateMany: suspend (List<T>) -> Unit,
) {
  val insertResults = insertMany(items)

  val updateList = items.zip(insertResults)
    .mapNotNull { (item, insertResult) ->
      if (insertResult == -1L) item else null
    }
  if (updateList.isNotEmpty()) updateMany(updateList)
}
