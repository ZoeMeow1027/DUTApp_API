package io.zoemeow.dutapp.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapp.model.NewsListItem
import io.zoemeow.dutapp.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun News(newsViewModel: NewsViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val tabTitles = listOf("News Global", "News Subjects")
        val pagerState = rememberPagerState(initialPage = 0)
        val scope = rememberCoroutineScope()

        Column() {
            TabRow(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 5.dp),
                selectedTabIndex = pagerState.currentPage,

            ) {
                tabTitles.forEachIndexed { index, text ->
                    val selected = pagerState.currentPage == index
                    Tab(
                        selected = selected,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }},
                        text = {
                            Text(
                                text = text,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    )
                }
            }
            HorizontalPager(
                count = tabTitles.size,
                state = pagerState,
            ) { index ->
                when (index) {
                    0 -> NewsGlobalView(newsViewModel)
                    1 -> NewsSubjectView(newsViewModel)
                }
            }
        }
    }
}

@Composable
fun NewsLoadingScreen() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Loading data from server.")
        Text("Please wait...")
    }
}

@Composable
fun NewsErrorScreen() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Loading data from server.")
        Text("Please wait...")
    }
}

@Composable
fun NewsGlobalView(newsViewModel: NewsViewModel) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            newsViewModel.getAllNewsGlobalFromServer()
        }
    ) {
        if (newsViewModel.loadingGlobal.value) {
            swipeRefreshState.isRefreshing = true
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                NewsLoadingScreen()
            }
        }
        else if (newsViewModel.dataGlobal.value.newslist == null) {
            swipeRefreshState.isRefreshing = false
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                NewsErrorScreen()
            }
        }
        else {
            swipeRefreshState.isRefreshing = false
            NewsLoadList(newsViewModel.dataGlobal.value.newslist!!)
        }
    }
}

@Composable
fun NewsSubjectView(newsViewModel: NewsViewModel) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            newsViewModel.getAllNewsSubjectsFromServer()
        }
    ) {
        if (newsViewModel.loadingSubjects.value) {
            swipeRefreshState.isRefreshing = true
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                NewsLoadingScreen()
            }
        }
        else if (newsViewModel.dataSubjects.value.newslist == null) {
            swipeRefreshState.isRefreshing = false
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                NewsErrorScreen()
            }
        }
        else {
            swipeRefreshState.isRefreshing = false
            NewsLoadList(newsViewModel.dataSubjects.value.newslist!!)
        }
    }
}

@Composable
fun NewsLoadList(newsList: List<NewsListItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(newsList) {
                item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp)
                    // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onSecondary)
//                    .background(Color(0xFF00D724))
                    .padding(top = 10.dp, bottom = 10.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.
                    padding(start = 15.dp, end = 15.dp)
                ) {
                    Text(
                        text = item.title!!,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = item.contenttext!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = item.date!!,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
