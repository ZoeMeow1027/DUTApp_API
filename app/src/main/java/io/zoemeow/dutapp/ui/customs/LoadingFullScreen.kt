package io.zoemeow.dutapp.ui.customs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingFullScreen(
    title: String,
    contentList: ArrayList<String>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.size(10.dp))
        for (item in contentList) {
            Text(
                text = item,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(5.dp))
        }
        Spacer(modifier = Modifier.size(10.dp))
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 90.dp, end = 90.dp)
        )
    }
}