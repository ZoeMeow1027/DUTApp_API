package io.zoemeow.dutapp.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapp.model.news.NewsGlobalItem
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.data.NewsCacheData
import io.zoemeow.dutapp.data.NewsDetailsClicked

@Composable
fun NewsGlobalViewHost(
    newsDetailsClicked: MutableState<NewsDetailsClicked?>,
    isLoading: MutableState<Boolean>,
    data: MutableState<NewsCacheData>,
    refreshRequired: () -> Unit,
) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    LaunchedEffect(Unit) {
        snapshotFlow { isLoading.value }
            .collect {
                if (!it) swipeRefreshState.isRefreshing = isLoading.value
            }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            refreshRequired()
        }
    ) {
        if (data.value.newsGlobalData.value.size == 0) {
            swipeRefreshState.isRefreshing = isLoading.value

            val loadingText = arrayOf("Loading data from server\nPlease wait...")
            val errorText = arrayOf("Nothing")
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
            ) {
                items(if (isLoading.value) loadingText else errorText) {
                        item -> Text(item)
                }
            }
        }
        else {
            swipeRefreshState.isRefreshing = false
            NewsGlobalLoadList(
                newsDetailsClicked = newsDetailsClicked,
                data.value.newsGlobalData.value,
            )
        }
    }
}

// https://stackoverflow.com/questions/2891361/how-to-set-time-zone-of-a-java-util-date
@Composable
fun NewsGlobalLoadList(
    newsDetailsClicked: MutableState<NewsDetailsClicked?>,
    newsList: ArrayList<NewsGlobalItem>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(newsList) { item ->
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 5.dp, bottom = 5.dp)
                    // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { newsDetailsClicked.value?.setViewDetailsNewsGlobal(item) }
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
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
fun NewsGlobalDetails(newsGlobalItem: NewsGlobalItem) {
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
            val context = LocalContext.current
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
                                ?.let { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.item.lowercase()))
                                    context.startActivity(intent)
                                }
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
