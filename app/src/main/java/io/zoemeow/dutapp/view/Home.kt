package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutapp.R

@Composable
fun Home() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_home_24),
            contentDescription = "home",
            tint = Color.Blue,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
        )
    }
}
