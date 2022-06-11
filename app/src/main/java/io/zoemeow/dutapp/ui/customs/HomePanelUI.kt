package io.zoemeow.dutapp.ui.customs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun HomePanel_Subject_Box(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 5.dp, bottom = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        content = content
    )
}

@Composable
fun HomePanel_Subject_Header(
    title: String,
    description: String
) {
    Spacer(modifier = Modifier.size(10.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge,
    )
    Spacer(modifier = Modifier.size(5.dp))
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun HomePanel_Subject_Column(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
        content = content
    )
}

@Composable
fun HomePanel_Loading_State(
    title: String,
    description: String,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(5.dp))
            if (isLoading)
                CircularProgressIndicator()
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}