package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.zoemeow.dutapp.model.enums.ProcessResult
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem
import io.zoemeow.dutapp.ui.customs.HomePanel_Loading_State
import io.zoemeow.dutapp.ui.customs.HomePanel_Subject_Box
import io.zoemeow.dutapp.ui.customs.HomePanel_Subject_Column
import io.zoemeow.dutapp.ui.customs.HomePanel_Subject_Header
import io.zoemeow.dutapp.utils.DateToString
import io.zoemeow.dutapp.utils.dateTimeToString
import io.zoemeow.dutapp.utils.getCurrentLesson
import io.zoemeow.dutapp.utils.getCurrentUnixTime
import io.zoemeow.dutapp.viewmodel.MainViewModel

@Composable
fun Home(mainViewModel: MainViewModel) {
    val isLoadingSubject = remember { mutableStateOf(false) }
    val isLoggingIn = remember { mutableStateOf(false) }

    LaunchedEffect(mainViewModel.tempVarData.changedCount.value) {
        isLoadingSubject.value = try {
            mainViewModel.tempVarData["SubjectSchedule"].value!!.toInt() ==
                    ProcessResult.Running.result
        }
        catch (_: Exception) { false }

        isLoggingIn.value = (
                try { mainViewModel.tempVarData["LoggingIn"].value!!.toInt() == ProcessResult.Running.result }
                catch (_: Exception) { false }
                )
    }

    val optionsScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(optionsScrollState)
    ) {
        if (mainViewModel.isLoggedIn() || mainViewModel.isAvailableOffline()) {
            HomePanelLoggedIn(mainViewModel, isLoadingSubject)
        }
        else {
            HomePageNotLoggedIn(isLoggingIn.value)
        }
    }
}

@Composable
fun HomePanelLoggedIn(
    mainViewModel: MainViewModel,
    isLoadingSubject: MutableState<Boolean>
) {
    // Examination in next 7 days
    HomePanelExamination(
        isLoadingSubject,
        mainViewModel.subjectExaminationOn7Days
    )
    // Today lesson
    HomePanelToday(
        isLoadingSubject,
        getCurrentLesson(),
        mainViewModel.subjectScheduleOnToday
    )
    // Tomorrow lesson
    HomePanelTomorrow(
        isLoadingSubject,
        mainViewModel.subjectScheduleOnFuture
    )
}

@Composable
fun HomePanelExamination(
    isLoadingSubject: MutableState<Boolean>,
    subjectExamInDays: SnapshotStateList<SubjectScheduleItem>
) {
    HomePanel_Subject_Header(
        title = "Your examination",
        description = "Your subject examination in next 7 days"
    )
    if (!isLoadingSubject.value) {
        for (item in subjectExamInDays) {
            HomePanel_Subject_Box {
                HomePanel_Subject_Column {
                    Text(
                        text = item.name.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${dateTimeToString(item.schedule_exam!!.date - getCurrentUnixTime())} (${DateToString(item.schedule_exam.date, "dd/MM/yyyy HH:mm", "GMT+7")})",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (item.schedule_exam.room != null) {
                        Text(
                            text = "In room: ${item.schedule_exam.room}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
    else HomePanelNewScheduleLoading()
    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun HomePanelToday(
    isLoadingSubject: MutableState<Boolean>,
    currentLesson: Int,
    subjectScheduleToday: SnapshotStateList<SubjectScheduleItem>
) {
    HomePanel_Subject_Header(
        title = "Your today lessons",
        description = (
                if (currentLesson <= 0)
                    "Good morning! Your lessons are ready..."
                else if (currentLesson > 14)
                    "Your today lessons are over! Happy tonight!"
                else
                    "Current lesson: %s%s".format(
                        getCurrentLesson(),
                        if (subjectScheduleToday.size == 0)
                            ", but you're reached all lessons today! Enjoy your day!"
                        else ""
                    )
                )
    )
    // Current lesson, if done loading
    if (!isLoadingSubject.value) {
        for (item in subjectScheduleToday) {
            HomePanel_Subject_Box {
                HomePanel_Subject_Column {
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
                        text = item.name.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = str,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
    else HomePanelNewScheduleLoading()
    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun HomePanelTomorrow(
    isLoadingSubject: MutableState<Boolean>,
    subjectScheduleTomorrow: SnapshotStateList<SubjectScheduleItem>
) {
    HomePanel_Subject_Header(
        title = "Tomorrow lesson",
        description = (
                if (subjectScheduleTomorrow.size == 0)
                    "No lessons! It can be no lessons in tomorrow, or your lessons week has ended."
                else "Below is lesson what is beginning learned by you tomorrow."
                )
    )
    // Tomorrow lesson
    if (!isLoadingSubject.value) {
        for (item in subjectScheduleTomorrow) {
            HomePanel_Subject_Box {
                HomePanel_Subject_Column {
                    // Find and join all lessons
                    val temp = item.schedule_study?.schedule
                    var str = ""
                    for (item1 in temp!!) {
                        if (str.isNotEmpty())
                            str += "; "
                        str += "Lesson ${item1.lesson?.start} -> ${item1.lesson?.end} "
                    }
                    // Lesson name
                    Text(
                        text = item.name.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    // Lesson in str
                    Text(
                        text = str,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
    else HomePanelNewScheduleLoading()
    Spacer(modifier = Modifier.size(10.dp))
}

@Composable
fun HomePageNotLoggedIn(loggingIn: Boolean) {
    HomePanel_Loading_State(
        isLoading = loggingIn,
        title = if (!loggingIn) "You are not logged in!" else "Logging you in...",
        description = "Your subject schedule will show here after you logged in."
    )
}

@Composable
fun HomePanelNewScheduleLoading() {
    HomePanel_Loading_State(
        isLoading = true,
        title = "Loading your new schedule",
        description = "Please wait..."
    )
}