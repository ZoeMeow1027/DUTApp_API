package io.zoemeow.dutapp.ui.customs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.model.news.LinkItem
import java.util.ArrayList

@Composable
fun NewsPanel_NewsItem(
    date: String,
    title: String,
    summary: String,
    clickable: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp)
            // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { clickable() }
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
        ) {
            // https://stackoverflow.com/questions/2891361/how-to-set-time-zone-of-a-java-util-date
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                // https://stackoverflow.com/a/65736376
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun NewsPanel_NewsDetailsItem(
    date: String?,
    title: String?,
    content: String?,
    links: ArrayList<LinkItem>?,
    linkClicked: (String) -> Unit
) {
    val optionsScrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .padding(20.dp)
            .background(MaterialTheme.colorScheme.onSecondary)
            .verticalScroll(optionsScrollState)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "$title",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = "Posted on $date",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(15.dp))
            val annotatedString = buildAnnotatedString {
                if (content != null) {
                    // Parse all string to annotated string.
                    append(content)
                    // Adjust color for annotated string to follow system mode.
                    addStyle(
                        style = SpanStyle(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
                        start = 0,
                        end = content.length
                    )
                    // Adjust for detected link.
                    links?.forEach {
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
                        links?.forEach {
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
