package com.example.uistateplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.uistateplayground.data.Movie
import com.example.uistateplayground.data.MovieGenre
import com.example.uistateplayground.ui.GenreViewModel
import com.example.uistateplayground.ui.HomeViewModel
import com.example.uistateplayground.ui.navigation.Screen
import com.example.uistateplayground.ui.navigation.UiStatePlaygroundNavHost
import com.example.uistateplayground.ui.theme.UiStatePlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()
      UiStatePlaygroundTheme() {
        Surface(color = Color.Blue) {
        }
        Scaffold { paddingValues ->
          UiStatePlaygroundNavHost(
            navController = navController,
            modifier = Modifier
              .padding(paddingValues)
          )
        }
      }
    }
  }
}

@Composable
fun HomeScreen(
  navController: NavController,
  modifier: Modifier = Modifier,
  homeViewModel: HomeViewModel = hiltViewModel()
) {
  val state by homeViewModel.uiState.collectAsState()

  if (state.isLoading) {
    LoadingIndicator()
  } else {
    Column(
      modifier
        .verticalScroll(
          rememberScrollState()
        )
    ) {
      Spacer(Modifier.height(16.dp))

      ScreenTitle(R.string.screen_title_home)

      HomeSection(title = R.string.section_title_top_rated) {
        TopRatedMovieList(state.topRatedMovies)
      }

      HomeSection(
        title = R.string.section_title_action,
        filter = SectionFilter {
          navController.navigate(Screen.ActionMovies.route)
        }
      ) {
        ActionMovieList(state.actionMovies)
      }

      HomeSection(
        title = R.string.section_title_animation,
        filter = SectionFilter {
          navController.navigate(Screen.AnimationMovies.route)
        }
      ) {
        AnimationMovieList(state.animationMovies)
      }

      Spacer(Modifier.height(16.dp))
    }
  }
}

@Composable
fun HomeSection(
  @StringRes title: Int,
  filter: SectionFilter? = null,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Column {
    if (filter == null) {
      SectionTitle(title = title)
    } else {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
          .fillMaxWidth()
      ) {
        SectionTitle(
          title = title,
          modifier = modifier
            .alignByBaseline()
        )
        SectionFilterButton(
          filter = filter,
          modifier = Modifier
            .alignByBaseline()
        )
      }
    }
    content()
  }
}

@Composable
fun TopRatedMovieList(movies: List<Movie>) {

  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(
      start = 16.dp,
      end = 16.dp
    )
  ) {
    items(movies) { movie ->
      HomePosterImage(movie)
    }
  }
}

@Composable
fun ActionMovieList(movies: List<Movie>) {
  LazyRow(
    modifier = Modifier
      .height(160.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(
      start = 16.dp,
      end = 16.dp
    )
  ) {
    items(movies) { movie ->
      HomePosterImage(movie)
    }
  }
}

@Composable
fun AnimationMovieList(movies: List<Movie>) {
  LazyHorizontalGrid(
    rows = GridCells.Fixed(2),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(
      start = 16.dp,
      end = 16.dp
    ),
    modifier = Modifier
      .height(340.dp)
  ) {
    items(movies) { movie ->
      HomePosterImage(movie)
    }
  }
}

@Composable
fun ScreenTitle(@StringRes title: Int) {
  Text(
    text = stringResource(id = title),
    style = MaterialTheme.typography.h4,
    modifier = Modifier.padding(horizontal = 16.dp)
  )
}

@Composable
fun SectionTitle(
  @StringRes title: Int,
  modifier: Modifier = Modifier
) {
  Text(
    text = stringResource(id = title).uppercase(Locale.getDefault()),
    style = MaterialTheme.typography.h6,
    modifier = modifier
      .paddingFromBaseline(top = 46.dp, bottom = 8.dp)
      .padding(start = 16.dp)
  )
}

@Composable
fun SectionFilterButton(
  filter: SectionFilter,
  modifier: Modifier = Modifier
) {
  Text(
    text = stringResource(id = filter.text),
    style = MaterialTheme.typography.h6,
    modifier = modifier
      .padding(end = 16.dp)
      .clickable { filter.onClick() }
  )
}

@Composable
fun HomePosterImage(movie: Movie) {
  AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
      .data(movie.posterUrl)
      .crossfade(true)
      .build(),
    contentDescription = movie.title,
    contentScale = ContentScale.Crop,
    placeholder = painterResource(id = R.drawable.poster_placeholder),
    modifier = Modifier
      .clip(shape = RoundedCornerShape(16.dp))
  )
}

@Composable
fun GenrePosterImage(movie: Movie) {
  AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
      .data(movie.posterUrl)
      .crossfade(true)
      .build(),
    contentDescription = movie.title,
    contentScale = ContentScale.Crop,
    placeholder = painterResource(id = R.drawable.poster_placeholder)
  )
}

@Composable
fun ActionMoviesScreen(viewModel: GenreViewModel = hiltViewModel()) {
  LaunchedEffect(Unit) {
    viewModel.fetchMovies(MovieGenre.ACTION)
  }

  val state by viewModel.uiState.collectAsState()
  if (state.isLoading) {
    LoadingIndicator()
  } else {
    LazyVerticalGrid(
      columns = GridCells.Adaptive(100.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(
        top = 16.dp,
        bottom = 16.dp
      )
    ) {

      item(span = { GridItemSpan(maxLineSpan) }) {
        ScreenTitle(R.string.screen_title_action_movies)
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(Modifier.height(8.dp))
      }

      items(state.movies) { movie ->
        GenrePosterImage(movie)
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(Modifier.height(16.dp))
      }
    }
  }
}

@Composable
fun AnimationMoviesScreen(viewModel: GenreViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsState()

  LaunchedEffect(Unit) {
    viewModel.fetchMovies(MovieGenre.ANIMATION)
  }

  if (state.isLoading) {
    LoadingIndicator()
  } else {
    LazyVerticalGrid(
      columns = GridCells.Adaptive(100.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(
        top = 16.dp,
        bottom = 16.dp
      )
    ) {

      item(span = { GridItemSpan(maxLineSpan) }) {
        ScreenTitle(R.string.screen_title_animation_movies)
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(Modifier.height(8.dp))
      }

      items(state.movies) { movie ->
        GenrePosterImage(movie)
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(Modifier.height(16.dp))
      }
    }
  }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .fillMaxSize()
  ) {
    CircularProgressIndicator()
  }
}

@Preview(showBackground = true)
@Composable
fun TopRatedMovieListPreview() {
  TopRatedMovieList(getFakeMovieList())
}

@Preview(showBackground = true)
@Composable
fun ActionMovieListPreview() {
  ActionMovieList(getFakeMovieList())
}

@Preview(showBackground = true)
@Composable
fun AnimationMovieListPreview() {
  AnimationMovieList(getFakeMovieList())
}

data class SectionFilter(
  @StringRes val text: Int = R.string.section_filter_text_default,
  val onClick: () -> Unit
)

private fun getFakeMovieList() = listOf(
  Movie("", ""),
  Movie("", ""),
  Movie("", ""),
  Movie("", ""),
  Movie("", ""),
  Movie("", ""),
  Movie("", "")
)