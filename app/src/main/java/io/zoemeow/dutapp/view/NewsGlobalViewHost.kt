package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.data.NewsCacheData
import io.zoemeow.dutapp.data.NewsDetailsClickedData
import io.zoemeow.dutapp.model.news.NewsGlobalItem
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun NewsGlobalViewHost(
    newsDetailsClickedData: MutableState<NewsDetailsClickedData?>,
    isLoading: Boolean,
    data: MutableState<NewsCacheData>,
    getDataRequested: (force: Boolean) -> Unit,
) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    val lazyColumnState = rememberLazyListState()
    swipeRefreshState.isRefreshing = isLoading

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
            items(data.value.newsGlobalData.value) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)
                        // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { newsDetailsClickedData.value?.setViewDetailsNewsGlobal(item) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(
                            start = 15.dp,
                            end = 15.dp,
                            top = 10.dp,
                            bottom = 10.dp
                        )
                    ) {
                        Text(
                            text = item.title!!,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text(
                            text = item.content!!,
                            style = MaterialTheme.typography.bodyMedium,
                            // https://stackoverflow.com/a/65736376
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        // https://stackoverflow.com/questions/2891361/how-to-set-time-zone-of-a-java-util-date
                        Text(
                            text = getDateString(item.date!!, "dd/MM/yyyy"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        Spacer(modifier = Modifier.size(15.dp))
                        if (isLoading) { }
                    }
                )
            }
        }

        InfiniteListHandler(
            listState = lazyColumnState,
            onLoadMore = {
                getDataRequested(false)
            }
        )
    }
}

@Composable
fun NewsGlobalDetails(
    newsGlobalItem: NewsGlobalItem,
    linkClicked: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .background(MaterialTheme.colorScheme.onSecondary)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "${newsGlobalItem.title}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = "Posted on ${ getDateString(newsGlobalItem.date ?: 0, "dd/MM/yyyy") }",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(15.dp))
            val annotatedString = buildAnnotatedString {
                if (newsGlobalItem.content != null) {
                    // Parse all string to annotated string.
                    append(newsGlobalItem.content!!)
                    // Adjust color for annotated string to follow system mode.
                    addStyle(
                        style = SpanStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
                        start = 0,
                        end = newsGlobalItem.content!!.length
                    )
                    // Adjust for detected link.
                    newsGlobalItem.links?.forEach {
                        addStringAnnotation(
                            tag = it.position!!.toString(),
                            annotation = it.url!!,
                            start = it.position,
                            end = it.position + it.text!!.length
                        )
                        addStyle(
                            style = SpanStyle(color = Color(0xff64B5F6)),
                            start = it.position,
                            end = it.position + it.text.length
                        )
                    }
                }
            }
            ClickableText(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                onClick = {
                    try {
                        newsGlobalItem.links?.forEach {
                                item ->
                            annotatedString
                                .getStringAnnotations(item.position!!.toString(), it, it)
                                .firstOrNull()
                                ?.let { url -> linkClicked(url.item.lowercase()) }
                        }
                    } catch (_: Exception) {
                        // TODO: Exception for can't open link here!
                    }
                }
            )
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = stringResource(id = R.string.newsdetails_swipeorclickabovetoexit),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfiniteListHandler(
    listState: LazyListState,
    buffer: Int = 2,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect {
                if (loadMore.value)
                    onLoadMore()
            }
    }
}
