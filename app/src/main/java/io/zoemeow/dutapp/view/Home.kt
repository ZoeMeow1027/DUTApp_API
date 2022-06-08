package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.zoemeow.dutapp.model.enums.ProcessResult
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem
import io.zoemeow.dutapp.utils.getCurrentLesson
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(mainViewModel: MainViewModel) {
    val isLoadingSubject = remember { mutableStateOf(false) }
    val optionsScrollState = rememberScrollState()

    LaunchedEffect(mainViewModel.variableData.changedCount.value) {
        isLoadingSubject.value = (
                try { mainViewModel.variableData.get<ProcessResult>("SubjectSchedule")!!.value.value == ProcessResult.Running }
                catch (_: Exception) { false }
                )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(optionsScrollState)
    ) {
        // Examination in next 7 days
        HomePanelExamination(
            isLoadingSubject,
            mainViewModel.subjectExam7Days
        )
        // Today lesson
        HomePanelToday(
            isLoadingSubject,
            getCurrentLesson(),
            mainViewModel.subjectScheduleToday
        )
        // Tomorrow lesson
        HomePanelTomorrow(
            isLoadingSubject,
            mainViewModel.subjectScheduleTomorrow
        )
    }
}

@Composable
fun HomePanelExamination(
    isLoadingSubject: MutableState<Boolean>,
    subjectExamInDays: SnapshotStateList<SubjectScheduleItem>
) {
    Spacer(modifier = Modifier.size(10.dp))
    Text(
        text = "Your examination",
        style = MaterialTheme.typography.headlineLarge,
    )
    Spacer(modifier = Modifier.size(5.dp))
    Text(
        text = "Your subject examination in next 7 days",
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.size(10.dp))
    if (!isLoadingSubject.value) {
        for (item in subjectExamInDays) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 5.dp, bottom = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = item.name.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = getDateString(item.schedule_exam!!.date, "dd/MM/yyyy HH:mm", "GMT+7"),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
    else {
        HomeNewScheduleLoading()
    }
}

@Composable
fun HomePanelToday(
    isLoadingSubject: MutableState<Boolean>,
    currentLesson: Int,
    subjectScheduleToday: SnapshotStateList<SubjectScheduleItem>
) {
    Spacer(modifier = Modifier.size(20.dp))
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
    if (!isLoadingSubject.value) {
        for (item in subjectScheduleToday) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 5.dp, bottom = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
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
    else {
        HomeNewScheduleLoading()
    }
}

@Composable
fun HomePanelTomorrow(
    isLoadingSubject: MutableState<Boolean>,
    subjectScheduleTomorrow: SnapshotStateList<SubjectScheduleItem>
) {
    Spacer(modifier = Modifier.size(20.dp))
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
    if (!isLoadingSubject.value) {
        for (item in subjectScheduleTomorrow) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 5.dp, bottom = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
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
    else {
        HomeNewScheduleLoading()
    }
}

@Composable
fun HomeNewScheduleLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
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