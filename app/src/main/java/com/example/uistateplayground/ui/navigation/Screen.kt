package com.example.uistateplayground.ui.navigation

sealed class Screen(val route: String) {
  object Home : Screen("home")
  object ActionMovies : Screen("action_movies_screen")
  object AnimationMovies : Screen("animation_movies_screen")
}