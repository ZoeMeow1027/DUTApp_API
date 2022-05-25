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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapp.model.NewsGlobalListItem
import io.zoemeow.dutapp.model.NewsSubjectItem
import io.zoemeow.dutapp.model.NewsSubjectListItem

@Composable
fun NewsSubjectViewHost(
    isLoading: MutableState<Boolean>,
    data: MutableState<NewsSubjectListItem>,
    refreshRequired: () -> Unit,
    newsSubjectItemReceived: (NewsSubjectItem) -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            refreshRequired()
        },
        content = {
            if (data.value.newslist == null) {
                swipeRefreshState.isRefreshing = isLoading.value

                val loadingText = arrayOf("Loading data from server", "Please wait...")
                val errorText = arrayOf("Nothing")
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(if (isLoading.value) loadingText else errorText) {
                            item -> Text(item)
                    }
                }
            }
            else {
                swipeRefreshState.isRefreshing = false
                NewsSubjectLoadList(
                    data.value.newslist!!,
                    newsItemReceived = { item -> newsSubjectItemReceived(item) }
                )
            }
        }
    )
}

// https://stackoverflow.com/questions/2891361/how-to-set-time-zone-of-a-java-util-date
@Composable
fun NewsSubjectLoadList(
    newsList: List<NewsSubjectItem>,
    newsItemReceived: (NewsSubjectItem) -> Unit
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
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(top = 10.dp, bottom = 10.dp)
                    .clickable { newsItemReceived(item) },
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
fun NewsSubjectDetails(newsSubjectItem: NewsSubjectItem) {
    Box(
        modifier = Modifier.padding(20.dp).background(MaterialTheme.colorScheme.onSecondary)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "${newsSubjectItem.title}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = "Posted on ${ getDateString(newsSubjectItem.date ?: 0, "dd/MM/yyyy") }",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(15.dp))
            val annotatedString = buildAnnotatedString {
                if (newsSubjectItem.contenttext != null) {
                    // Parse all string to annotated string.
                    append(newsSubjectItem.contenttext)
                    // Adjust color for annotated string to follow system mode.
                    addStyle(
                        style = SpanStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
                        start = 0,
                        end = newsSubjectItem.contenttext.length
                    )
                    // Adjust for detected link.
                    newsSubjectItem.links?.forEach {
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
                        newsSubjectItem.links?.forEach {
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
                text = "(Swipe bottom or click empty space above this to exit)",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
