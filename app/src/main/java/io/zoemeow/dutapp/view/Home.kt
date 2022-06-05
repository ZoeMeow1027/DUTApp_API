package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.zoemeow.dutapp.utils.getCurrentLesson
import io.zoemeow.dutapp.viewmodel.MainViewModel

@Composable
fun Home(mainViewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Current Lesson
        if (mainViewModel.isProcessingSubjectScheduleFee.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else {
            if (mainViewModel.subjectScheduleDayOfWeek.value.size > 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(mainViewModel.subjectScheduleDayOfWeek.value) { item ->
                        Box() {
                            Column() {
                                Text(
                                    text = item.id.toString()
                                )
                                Text(
                                    text = item.name.toString()
                                )
                            }
                        }
                    }
                    item {
                        Text("Current lesson: ${getCurrentLesson()}")
                    }
                }
            }
            else if (getCurrentLesson() > 15) {
                // TODO: Tomorrow here!
            }
            else {
                // TODO: Current here!
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No current lesson today!")
                    Text("Enjoy your day!")
                }
            }
        }
    }
}
