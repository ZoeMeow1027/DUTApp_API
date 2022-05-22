package io.zoemeow.dutapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapp.model.NewsItem
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun News(mainViewModel: MainViewModel, callBack: (NewsItem) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val tabTitles = listOf("News Global", "News Subjects")
        val pagerState = rememberPagerState(initialPage = 0)
        val scope = rememberCoroutineScope()

        Column {
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
                    0 -> NewsGlobalView(mainViewModel) { item ->
                        callBack(item)
                    }
                    1 -> NewsSubjectView(mainViewModel) { item ->
                        callBack(item)
                    }
                }
            }
        }
    }
}

@Composable
fun NewsGlobalView(mainViewModel: MainViewModel, callBack: (NewsItem) -> Unit) {
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
            NewsLoadList(mainViewModel.dataGlobal.value.newslist!!) { item ->
                callBack(item)
            }
        }
    }
}

@Composable
fun NewsSubjectView(mainViewModel: MainViewModel, callBack: (NewsItem) -> Unit) {
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
            NewsLoadList(mainViewModel.dataSubjects.value.newslist!!) { item ->
                callBack(item)
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun getDateString(date: Long, dateFormat: String, gmt: String = "UTC"): String {
    // "dd/MM/yyyy"
    // "dd/MM/yyyy HH:mm"
    val simpleDateFormat = SimpleDateFormat(dateFormat)
    simpleDateFormat.timeZone = TimeZone.getTimeZone(gmt)
    return simpleDateFormat.format(Date(date))
}

// https://stackoverflow.com/questions/2891361/how-to-set-time-zone-of-a-java-util-date
@Composable
fun NewsLoadList(newsList: List<NewsItem>, callBack: (NewsItem) -> Unit) {
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
                    .padding(top = 10.dp, bottom = 10.dp)
                    .clickable {
                        callBack(item)
                    },
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                ) {
                    Text(
                        text = item.title!!,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = item.contenttext!!,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        // https://stackoverflow.com/questions/65736375/how-to-show-ellipsis-three-dots-at-the-end-of-a-text-line-in-android-jetpack-c
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = getDateString(item.date!!, "dd/MM/yyyy"),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun NewsDetails(newsItemChosen: NewsItem) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onSecondary)
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "${newsItemChosen.title}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = "Posted on ${
                    getDateString(
                        newsItemChosen.date ?: 0,
                        "dd/MM/yyyy"
                    )
                }",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(15.dp))
            Text(
                text = "${newsItemChosen.contenttext}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = "(Swipe bottom or click empty space above this to exit)",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
