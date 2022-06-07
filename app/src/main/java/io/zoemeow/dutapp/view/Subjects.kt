package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.data.AccountCacheData
import io.zoemeow.dutapp.model.enums.ProcessResult
import io.zoemeow.dutapp.model.subject.SubjectFeeItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem
import io.zoemeow.dutapp.pagerTabIndicatorOffset
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun Subjects(mainViewModel: MainViewModel) {
    val isLoadingLoggingIn = (
            if (mainViewModel.variableData.get<ProcessResult>("LoggingIn") != null)
                mainViewModel.variableData.get<ProcessResult>("LoggingIn")!!.value.value == ProcessResult.Running
            else false
            )

    if (mainViewModel.accCacheData.value.sessionID.value.isEmpty() && !mainViewModel.isAvailableOffline()) {
        if (isLoadingLoggingIn)
            AccountPageLoggingIn()
        else SubjectsNotLoggedIn()
    }
    else {
        val isLoadingSubjectSchedule = (
                if (mainViewModel.variableData.get<ProcessResult>("SubjectSchedule") != null)
                    mainViewModel.variableData.get<ProcessResult>("SubjectSchedule")!!.value.value == ProcessResult.Running
                else false
                )

        when (isLoadingSubjectSchedule) {
            true -> SubjectsLoadingSubject()
            false -> SubjectsLoggedIn(
                mainViewModel.accCacheData.value,
                refreshRequest = {
                    mainViewModel.refreshSubjectSchedule()
                    mainViewModel.refreshSubjectFee()
                }
            )
        }
    }
}

@Composable
fun SubjectsNotLoggedIn() {
    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.navsubject_notloggedin_text1),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(id = R.string.navsubject_notloggedin_text2),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SubjectsLoadingSubject() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Please wait",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = "Loading your subjects...",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(15.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 90.dp, end = 90.dp)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SubjectsLoggedIn(cacheData: AccountCacheData, refreshRequest: () -> Unit) {
    val tabTitles = listOf(
        stringResource(id = R.string.navsubject_navtab_subjectschedule),
        stringResource(id = R.string.navsubject_navtab_subjectfee)
    )
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()
    val swipeRefreshState = rememberSwipeRefreshState(false)

    Column {
        TabRow(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 5.dp),
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                    tabPositions ->
                // This is a temporary fix for require material2 instead of material3.
                // https://github.com/google/accompanist/issues/1076
                // Waiting for a release fix for this library.
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            tabTitles.forEachIndexed { index, text ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = text,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
        }

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = refreshRequest
        ) {
            HorizontalPager(count = tabTitles.size, state = pagerState) { index ->
                when (index) {
                    0 -> SubjectsStudy(cacheData = cacheData)
                    1 -> SubjectsFee(cacheData = cacheData)
                }
            }
        }
    }
}

@Composable
fun SubjectsStudy(cacheData: AccountCacheData) {
    if (cacheData.subjectScheduleData.size > 0) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(cacheData.subjectScheduleData) {
                    item -> SubjectStudyItem(item = item)
            }
        }
    }
}

@Composable
fun SubjectsFee(cacheData: AccountCacheData) {
    if (cacheData.subjectFeeData.size > 0) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(cacheData.subjectFeeData) {
                    item -> SubjectsFeeItem(item)
            }
            item {
                Column {
                    Text(
                        "Total credit: ${cacheData.subjectCredit}"
                    )
                    Text(
                        "Total money: ${cacheData.subjectMoney}"
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectStudyItem(item: SubjectScheduleItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp)
            // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 10.dp, bottom = 10.dp),
    ) {
        Text("${item.id}")
        if (item.schedule_study != null) {
            Column() {
                for (i in item.schedule_study.schedule!!) {
                    Text("DayOfWeek: ${i.day_of_week}")
                    Text("Lesson: ${i.lesson?.start}-${i.lesson?.end}")
                }
            }
        }
        Text("${item.name}")
        if (item.schedule_exam != null)
            Text(getDateString(
                item.schedule_exam.date,
                stringResource(id = R.string.navsubject_subject_datetimeformat),
                "GMT+7")
            )
    }
}

@Composable
fun SubjectsFeeItem(item: SubjectFeeItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp)
            // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 10.dp, bottom = 10.dp),
    ) {
        Text("${item.id}")
        Text("${item.name}")
        Text("${item.credit}")
        Text("${item.is_high_quality}")
        Text("${item.price}")
        Text("${item.debt}")
        Text("${item.is_restudy}")
    }
}