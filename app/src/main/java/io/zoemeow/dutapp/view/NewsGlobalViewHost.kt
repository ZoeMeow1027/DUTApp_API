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
import io.zoemeow.dutapp.model.news.NewsGlobalItem
import io.zoemeow.dutapp.ui.customs.NewsPanel_NewsDetailsItem
import io.zoemeow.dutapp.ui.customs.NewsPanel_NewsItem
import io.zoemeow.dutapp.utils.DateToString
import io.zoemeow.dutapp.utils.LazyList_EndOfListHandler

@Composable
fun NewsGlobalViewHost(
    newsDetailsClickedData: MutableState<NewsDetailsClickedData?>,
    isLoading: Boolean,
    data: NewsCacheData,
    getDataRequested: (force: Boolean) -> Unit,
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
            verticalArrangement = Arrangement.Top,
        ) {
            items(data.newsGlobalData) { item ->
                NewsPanel_NewsItem(
                    date = DateToString(item.date!!, "dd/MM/yyyy"),
                    title = item.title!!,
                    summary = item.content!!,
                    clickable = { newsDetailsClickedData.value?.setViewDetailsNewsGlobal(item) }
                )
            }
        }
    }
}

@Composable
fun NewsGlobalDetails(
    newsGlobalItem: NewsGlobalItem,
    linkClicked: (String) -> Unit
) {
    NewsPanel_NewsDetailsItem(
        title = newsGlobalItem.title,
        date = DateToString(newsGlobalItem.date ?: 0, "dd/MM/yyyy"),
        content = newsGlobalItem.content,
        links = newsGlobalItem.links,
        linkClicked = { linkClicked(it) }
    )
}