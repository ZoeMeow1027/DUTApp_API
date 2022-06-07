package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.zoemeow.dutapp.model.enums.ProcessResult
import io.zoemeow.dutapp.utils.getCurrentLesson
import io.zoemeow.dutapp.viewmodel.MainViewModel

@Composable
fun Home(mainViewModel: MainViewModel) {
    val isLoadingSubject = (
            if (mainViewModel.variableData.get<ProcessResult>("SubjectSchedule") != null)
                mainViewModel.variableData.get<ProcessResult>("SubjectSchedule")!!.value.value == ProcessResult.Running
            else false
            )

    Column() {
        // What's going on...
        // Today lesson
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                val currentLesson = getCurrentLesson()
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Your today lessons",
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = (
                            if (currentLesson <= 0)
                                "Good morning! Your lessons are ready..."
                            else if (currentLesson > 14)
                                "Your today lessons are over! Happy tonight!"
                            else
                                "Current lesson: ${getCurrentLesson()}"
                            ),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.size(10.dp))

                // Current lesson, if done loading
                if (!isLoadingSubject) {
                    LazyColumn(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(mainViewModel.subjectScheduleToday.value) { item ->
                            Box(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                    .padding(top = 5.dp, bottom = 5.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = item.name.toString(),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    val temp = item.schedule_study?.schedule?.filter {
                                        it.lesson?.end!! >= currentLesson
                                    }
                                    var str = ""
                                    for (item1 in temp!!) {
                                        if (str.isNotEmpty())
                                            str += "; "
                                        str += "Lesson ${item1.lesson?.start} -> ${item1.lesson?.end} "
                                    }
                                    Text(
                                        text = str,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
                else {
                    Box(
                        modifier = Modifier.fillMaxWidth().wrapContentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().wrapContentSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.size(10.dp))
                            Text("Loading your new schedule")
                            Text("Please wait...")
                            Spacer(modifier = Modifier.size(5.dp))
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }
        // Tomorrow lesson
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Tomorrow lesson",
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = "Below is lesson what is beginning learned by you tomorrow.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.size(10.dp))

                // Tomorrow lesson
                if (!isLoadingSubject) {
                    LazyColumn(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(mainViewModel.subjectScheduleTomorrow.value) { item ->
                            Box(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                    .padding(top = 5.dp, bottom = 5.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = item.name.toString(),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    val temp = item.schedule_study?.schedule
                                    var str = ""
                                    for (item1 in temp!!) {
                                        if (str.isNotEmpty())
                                            str += "; "
                                        str += "Lesson ${item1.lesson?.start} -> ${item1.lesson?.end} "
                                    }
                                    Text(
                                        text = str,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
                else {
                    Box(
                        modifier = Modifier.fillMaxWidth().wrapContentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().wrapContentSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.size(10.dp))
                            Text("Loading your new schedule")
                            Text("Please wait...")
                            Spacer(modifier = Modifier.size(5.dp))
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }
    }
}
