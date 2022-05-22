package io.zoemeow.dutapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.zoemeow.dutapp.model.SubjectFeeListItem
import io.zoemeow.dutapp.model.SubjectScheduleListItem
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Subjects(mainViewModel: MainViewModel) {
    if (!mainViewModel.loggedIn()) {
        SubjectsNotLoggedIn()
    }
    else {
        val tabTitles = listOf("Subjects Schedule", "Subjects Fee")
        val pagerState = rememberPagerState(initialPage = 0)
        val scope = rememberCoroutineScope()

        Column {
            TabRow(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 5.dp),
                selectedTabIndex = pagerState.currentPage,
            ) {
                tabTitles.forEachIndexed { index, text ->
                    val selected = pagerState.currentPage == index
                    Tab(
                        selected = selected,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = text,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    )
                }
            }
            HorizontalPager(
                count = tabTitles.size,
                state = pagerState,
            ) { index ->
                when (index) {
                    0 -> {
                        if (mainViewModel.isProcessing().value)
                            SubjectsLoadingScreen()
                        else SubjectsStudy(subjectListItem = mainViewModel.dataSubjectSchedule.value)
                    }
                    1 -> {
                        if (mainViewModel.isProcessing().value)
                            SubjectsLoadingScreen()
                        else SubjectsFee(subjectFeeListItem = mainViewModel.dataSubjectFee.value)
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectsLoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text("Loading data from server.")
        Text("Please wait...")
    }
}

@Composable
fun SubjectsNotLoggedIn() {
    Column(
        modifier = Modifier
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "You are not logged in",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "To see your subjects schedule, sign in at \"Account\" tab.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SubjectsStudy(subjectListItem: SubjectScheduleListItem) {
    @SuppressLint("SimpleDateFormat")
    fun getDateString(date: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC+7")
        return simpleDateFormat.format(Date(date))
    }

    if (subjectListItem.schedulelist != null) {
        LazyColumn() {
            items(subjectListItem.schedulelist) {
                    item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)
                        // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(top = 10.dp, bottom = 10.dp),
                ) {
                    Text("${item.ID}")
                    Text("${item.Name}")
                    Text("${item.ScheduleStudy}")
                    Text("${item.Weeks}")
                    if (item.DateExam != null)
                        Text(getDateString(item.DateExam))
                }
            }
        }
    }
}

@Composable
fun SubjectsFee(subjectFeeListItem: SubjectFeeListItem) {
    if (subjectFeeListItem.feelist != null) {
        LazyColumn() {
            items(subjectFeeListItem.feelist) {
                    item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)
                        // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(top = 10.dp, bottom = 10.dp),
                ) {
                    Text("${item.ID}")
                    Text("${item.Name}")
                    Text("${item.Credit}")
                    Text("${item.IsHighQuality}")
                    Text("${item.Price}")
                    Text("${item.Debt}")
                    Text("${item.IsReStudy}")
                }
            }
        }
    }
}