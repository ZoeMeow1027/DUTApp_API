package io.zoemeow.dutapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapp.data.NewsCacheData
import io.zoemeow.dutapp.data.NewsDetailsClickedData
import io.zoemeow.dutapp.model.news.NewsSubjectItem
import io.zoemeow.dutapp.ui.customs.NewsPanel_NewsDetailsItem
import io.zoemeow.dutapp.ui.customs.NewsPanel_NewsItem
import io.zoemeow.dutapp.utils.DateToString
import io.zoemeow.dutapp.utils.LazyList_EndOfListHandler

@Composable
fun NewsSubjectViewHost(
    newsDetailsClickedData: MutableState<NewsDetailsClickedData?>,
    isLoading: Boolean,
    data: MutableState<NewsCacheData>,
    getDataRequested: (Boolean) -> Unit,
) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    val lazyColumnState = rememberLazyListState()
    swipeRefreshState.isRefreshing = isLoading

    LazyList_EndOfListHandler(
        listState = lazyColumnState,
        onLoadMore = {
            getDataRequested(false)
        }
    )

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            getDataRequested(true)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp),
            state = lazyColumnState,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            items(data.value.newsSubjectData) { item ->
                NewsPanel_NewsItem(
                    date = DateToString(item.date!!, "dd/MM/yyyy"),
                    title = item.title!!,
                    summary = item.content!!,
                    clickable = { newsDetailsClickedData.value?.setViewDetailsNewsSubject(item) }
                )
            }
        }
    }
}

@Composable
fun NewsSubjectDetails(
    newsSubjectItem: NewsSubjectItem,
    linkClicked: (String) -> Unit
) {
    NewsPanel_NewsDetailsItem(
        title = newsSubjectItem.title,
        date = DateToString(newsSubjectItem.date ?: 0, "dd/MM/yyyy"),
        content = newsSubjectItem.content,
        links = newsSubjectItem.links,
        linkClicked = { linkClicked(it) }
    )
}