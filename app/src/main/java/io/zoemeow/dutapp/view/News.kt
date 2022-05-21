package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapp.model.NewsListItem
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun News(mainViewModel: MainViewModel) {
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
                            }
                        },
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
                    0 -> NewsGlobalView(mainViewModel)
                    1 -> NewsSubjectView(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun NewsGlobalView(mainViewModel: MainViewModel) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            mainViewModel.getAllNewsGlobalFromServer()
        }
    ) {
        if (mainViewModel.dataGlobal.value.newslist == null) {
            val loadingText = arrayOf("Loading data from server", "Please wait...")
            val errorText = arrayOf("Nothing")
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    if (mainViewModel.loadingGlobal.value) loadingText
                    else errorText
                ) { item ->
                    Text(item)
                }
            }

            swipeRefreshState.isRefreshing = mainViewModel.loadingGlobal.value
        } else {
            swipeRefreshState.isRefreshing = false
            NewsLoadList(mainViewModel.dataGlobal.value.newslist!!)
        }
    }
}

@Composable
fun NewsSubjectView(mainViewModel: MainViewModel) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            mainViewModel.getAllNewsSubjectsFromServer()
        }
    ) {
        if (mainViewModel.dataSubjects.value.newslist == null) {
            val loadingText = arrayOf("Loading data from server", "Please wait...")
            val errorText = arrayOf("Nothing")
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    if (mainViewModel.loadingSubjects.value) loadingText
                    else errorText
                ) { item ->
                    Text(item)
                }
            }

            swipeRefreshState.isRefreshing = mainViewModel.loadingSubjects.value
        } else {
            swipeRefreshState.isRefreshing = false
            NewsLoadList(mainViewModel.dataSubjects.value.newslist!!)
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
        items(newsList) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp)
                    // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
//                    .background(Color(0xFF00D724))
                    .padding(top = 10.dp, bottom = 10.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                ) {
                    Text(
                        text = item.title!!,
                        style = MaterialTheme.typography.titleMedium
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
