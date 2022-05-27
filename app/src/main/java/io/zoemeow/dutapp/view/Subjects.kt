package io.zoemeow.dutapp.view

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.model.SubjectFeeItem
import io.zoemeow.dutapp.model.SubjectScheduleItem
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Subjects(mainViewModel: MainViewModel) {
    if (mainViewModel.isLoggedIn()) {
        val tabTitles = listOf(
            stringResource(id = R.string.navsubject_navtab_subjectschedule),
            stringResource(id = R.string.navsubject_navtab_subjectfee)
        )
        val pagerState = rememberPagerState(initialPage = 0)
        val scope = rememberCoroutineScope()

        Column {
            TabRow(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 5.dp),
                selectedTabIndex = pagerState.currentPage,
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
            HorizontalPager(count = tabTitles.size, state = pagerState) {
                    index ->
                when (index) {
                    0 -> {
                        if (mainViewModel.isProcessingAccount().value)
                            SubjectsLoadingScreen()
                        else SubjectsStudy(subjectListItem = mainViewModel.accountData.value.SubjectScheduleData.value)
                    }
                    1 -> {
                        if (mainViewModel.isProcessingAccount().value)
                            SubjectsLoadingScreen()
                        else SubjectsFee(subjectListItem = mainViewModel.accountData.value.SubjectFeeData.value)
                    }
                }
            }
        }
    } else SubjectsNotLoggedIn()
}

@Composable
fun SubjectsLoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(stringResource(id = R.string.text_loading))
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
fun SubjectsStudy(subjectListItem: ArrayList<SubjectScheduleItem>) {
    if (subjectListItem.size > 0) {
        LazyColumn {
            items(subjectListItem) {
                    item -> SubjectStudyItem(item = item)
            }
        }
    }
}

@Composable
fun SubjectsFee(subjectListItem: ArrayList<SubjectFeeItem>) {
    if (subjectListItem.size > 0) {
        LazyColumn {
            items(subjectListItem) {
                    item -> SubjectsFeeItem(item)
            }
        }
    }
}

@Composable
fun SubjectStudyItem(item: SubjectScheduleItem) {
    Column(
        modifier = Modifier.fillMaxWidth()
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
            Text(getDateString(
                item.DateExam,
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
        Text("${item.ID}")
        Text("${item.Name}")
        Text("${item.Credit}")
        Text("${item.IsHighQuality}")
        Text("${item.Price}")
        Text("${item.Debt}")
        Text("${item.IsReStudy}")
    }
}