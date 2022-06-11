package io.zoemeow.dutapp.ui.customs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsPanel_LayoutOptionItem(
    textAbove: String,
    textBelow: String? = null,
    clickable: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { clickable() },
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
            Text(
                text = textAbove,
                style = MaterialTheme.typography.titleMedium,
            )
            if (textBelow != null) {
                Text(
                    text = textBelow,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun SettingsPanel_Divider() {
    Divider(
        thickness = 1.dp,
        color = Color.LightGray
    )
}