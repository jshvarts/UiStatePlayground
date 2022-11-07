package com.example.uistateplayground.core

import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen

private const val RETRY_TIME_IN_MILLIS = 15_000L

sealed interface Result<out T> {
  data class Success<T>(val data: T) : Result<T>
  data class Error(val exception: Throwable? = null) : Result<Nothing>
  object Loading : Result<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
  return this
    .map<T, Result<T>> {
      Result.Success(it)
    }
    .onStart { emit(Result.Loading) }
    .retryWhen { cause, _ ->
      if (cause is IOException) {
        emit(Result.Error(cause))

        delay(RETRY_TIME_IN_MILLIS)
        true
      } else {
        false
      }
    }
    .catch { emit(Result.Error(it)) }
}
