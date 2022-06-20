package com.example.uistateplayground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uistateplayground.ActionMoviesScreen
import com.example.uistateplayground.AnimationMoviesScreen
import com.example.uistateplayground.HomeScreen

@Composable
fun UiStatePlaygroundNavHost(
  navController: NavHostController,
  modifier: Modifier
) {
  NavHost(
    navController = navController,
    startDestination = Screen.Home.route,
    modifier = modifier
  ) {
    composable(Screen.Home.route) {
      HomeScreen(navController, modifier)
    }
    composable(Screen.ActionMovies.route) {
      ActionMoviesScreen()
    }
    composable(Screen.AnimationMovies.route) {
      AnimationMoviesScreen()
    }
  }
}