package com.piashcse.hilt_mvvm_compose_movie.ui.screens.moviedetail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.piashcse.hilt_mvvm_compose_movie.R
import com.piashcse.hilt_mvvm_compose_movie.data.model.MovieItem
import com.piashcse.hilt_mvvm_compose_movie.ui.component.CircularIndeterminateProgressBar
import com.piashcse.hilt_mvvm_compose_movie.data.datasource.remote.ApiURL
import com.piashcse.hilt_mvvm_compose_movie.data.model.artist.Cast
import com.piashcse.hilt_mvvm_compose_movie.navigation.Screen
import com.piashcse.hilt_mvvm_compose_movie.ui.component.ExpandingText
import com.piashcse.hilt_mvvm_compose_movie.ui.component.text.SubtitlePrimary
import com.piashcse.hilt_mvvm_compose_movie.ui.component.text.SubtitleSecondary
import com.piashcse.hilt_mvvm_compose_movie.ui.theme.*
import com.piashcse.hilt_mvvm_compose_movie.utils.hourMinutes
import com.piashcse.hilt_mvvm_compose_movie.utils.roundTo
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@Composable
fun MovieDetail(navController: NavController, movieId: Int) {
    val viewModel = hiltViewModel<MovieDetailViewModel>()
    val isLoading by viewModel.isLoading.collectAsState()
    val movieDetail by viewModel.movieDetail.collectAsState()
    val recommendMovie by viewModel.recommendedMovie.collectAsState()
    val movieCredit by viewModel.movieCredit.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.movieDetail(movieId)
        viewModel.recommendedMovie(movieId)
        viewModel.movieCredit(movieId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                DefaultBackgroundColor
            )
    ) {
        CircularIndeterminateProgressBar(isDisplayed = isLoading, 0.4f)
        movieDetail?.let { it ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                CoilImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    imageModel = { ApiURL.IMAGE_URL.plus(it.poster_path) },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        contentDescription = "Movie detail",
                        colorFilter = null,
                    ),
                    component = rememberImageComponent {
                        +CircularRevealPlugin(
                            duration = 800
                        )
                        +ShimmerPlugin(
                            shimmer = Shimmer.Flash(
                                baseColor = SecondaryFontColor,
                                highlightColor = DefaultBackgroundColor
                            )
                        )
                    },
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Text(
                        text = it.title,
                        modifier = Modifier.padding(top = 10.dp),
                        color = FontColor,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.W700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp, top = 10.dp)
                    ) {

                        Column(Modifier.weight(1f)) {
                            SubtitlePrimary(
                                text = it.original_language,
                            )
                            SubtitleSecondary(
                                text = stringResource(R.string.language)
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            SubtitlePrimary(
                                text = it.vote_average.roundTo(1).toString(),
                            )
                            SubtitleSecondary(
                                text = stringResource(R.string.rating)
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            SubtitlePrimary(
                                text = it.runtime.hourMinutes()
                            )
                            SubtitleSecondary(
                                text = stringResource(R.string.duration)
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            SubtitlePrimary(
                                text = it.release_date
                            )
                            SubtitleSecondary(
                                text = stringResource(R.string.release_date)
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.description),
                        color = FontColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    ExpandingText(text = it.overview)
                    recommendMovie?.let {
                        RecommendedMovie(navController, it)
                    }
                    movieCredit?.let {
                        ArtistAndCrew(navController, it.cast)
                    }
                }
            }
        }
    }
}

@Preview(name = "MovieDetail", showBackground = true)
@Composable
fun Preview() {
    // MovieDetail(null, MovieItem())
}

@Composable
fun RecommendedMovie(navController: NavController?, recommendedMovie: List<MovieItem>) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = stringResource(R.string.similar),
            color = FontColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(modifier = Modifier.fillMaxHeight()) {
            items(recommendedMovie, itemContent = { item ->
                Column(
                    modifier = Modifier.padding(
                        start = 0.dp, end = 8.dp, top = 5.dp, bottom = 5.dp
                    )
                ) {
                    CoilImage(
                        modifier = Modifier
                            .height(190.dp)
                            .width(140.dp)
                            .cornerRadius(10)
                            .clickable {
                                navController?.navigate(
                                    Screen.MovieDetail.route.plus(
                                        "/${item.id}"
                                    )
                                )
                            },
                        imageModel = { ApiURL.IMAGE_URL.plus(item.posterPath) },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            contentDescription = "similar movie",
                            colorFilter = null,
                        ),
                        component = rememberImageComponent {
                            // shows a shimmering effect when loading an image.
                            +CircularRevealPlugin(
                                duration = 800
                            )
                        },
                    )
                }
            })
        }
    }
}

@Composable
fun ArtistAndCrew(navController: NavController?, cast: List<Cast>) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = stringResource(R.string.cast),
            color = FontColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(modifier = Modifier.fillMaxHeight()) {
            items(cast, itemContent = { item ->
                Column(
                    modifier = Modifier.padding(
                        start = 0.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                    ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CoilImage(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .height(80.dp)
                            .width(80.dp)
                            .cornerRadius(40)
                            .clickable {
                                navController?.navigate(
                                    Screen.ArtistDetail.route.plus(
                                        "/${item.id}"
                                    )
                                )
                            },
                        imageModel = { ApiURL.IMAGE_URL.plus(item.profilePath) },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            contentDescription = "artist and crew",
                            colorFilter = null,
                        ),
                        component = rememberImageComponent {
                            +CircularRevealPlugin(
                                duration = 800
                            )
                            +ShimmerPlugin(
                                shimmer = Shimmer.Flash(
                                    baseColor = SecondaryFontColor,
                                    highlightColor = DefaultBackgroundColor
                                )
                            )
                        },
                    )
                    SubtitleSecondary(text = item.name)
                }
            })
        }
    }
}
