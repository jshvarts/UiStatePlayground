package com.example.uistateplayground.data.source

import android.content.Context
import androidx.room.Room
import com.example.uistateplayground.data.model.UiStatePlaygroundDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun providesDatabase(
    @ApplicationContext context: Context,
  ): UiStatePlaygroundDatabase = Room.databaseBuilder(
    context,
    UiStatePlaygroundDatabase::class.java,
    "ui-state-playground-database"
  ).build()
}
