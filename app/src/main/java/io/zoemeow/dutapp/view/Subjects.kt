package io.zoemeow.dutapp.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import io.zoemeow.dutapp.ui.customs.LoadingFullScreen
import io.zoemeow.dutapp.utils.DateToString
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun Subjects(mainViewModel: MainViewModel) {
    val isLoadingLoggingIn = remember { mutableStateOf(false) }
    val isLoadingSubjectSchedule = remember { mutableStateOf(false) }

    LaunchedEffect(mainViewModel.tempVarData.changedCount.value) {
        isLoadingLoggingIn.value = (
                if (mainViewModel.tempVarData["LoggingIn"].value != null)
                    mainViewModel.tempVarData["LoggingIn"].value!!.toInt() == ProcessResult.Running.result
                else false
                )

        isLoadingSubjectSchedule.value = (
                if (mainViewModel.tempVarData["SubjectSchedule"].value != null)
                    mainViewModel.tempVarData["SubjectSchedule"].value!!.toInt() == ProcessResult.Running.result
                else false
                )
    }

    if (!mainViewModel.isLoggedIn() && !mainViewModel.isAvailableOffline()) {
        if (isLoadingLoggingIn.value) {
            LoadingFullScreen(
                title = "Please wait",
                contentList = arrayListOf(
                    "Logging you in..."
                )
            )
        }
        else SubjectsNotLoggedIn()
    }
    else {
        when (isLoadingSubjectSchedule.value) {
            true -> SubjectsLoadingSubject()
            false -> SubjectsLoggedIn(
                mainViewModel.accCacheData,
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
                    0 -> SubjectsStudy(subjectListItem = cacheData.subjectScheduleData)
                    1 -> SubjectsFee(SubjectFeeItem = cacheData.subjectFeeData, cacheData = cacheData)
                }
            }
        }
    }
}

@Composable
fun SubjectsStudy(subjectListItem: ArrayList<SubjectScheduleItem>) {
    if (subjectListItem.size > 0) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(subjectListItem) {
                    item -> Test(item = item)
            }
        }
    }
}

@Composable
fun SubjectsFee(SubjectFeeItem: List<SubjectFeeItem>, cacheData: AccountCacheData) {
    Demo_Table(SubjectFeeItem, cacheData)
}

//@Composable
//fun SubjectStudyItem(item: SubjectScheduleItem) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 5.dp, bottom = 5.dp)
//            // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
//            .clip(RoundedCornerShape(10.dp))
//            .background(MaterialTheme.colorScheme.primaryContainer)
//            .padding(top = 10.dp, bottom = 10.dp),
//    ) {
//        Text("${item.id}")
//        if (item.schedule_study != null) {
//            Column() {
//                for (i in item.schedule_study.schedule!!) {
//                    Text("DayOfWeek: ${i.day_of_week}")
//                    Text("Lesson: ${i.lesson?.start}-${i.lesson?.end}")
//                }
//            }
//        }
//        Text("${item.name}")
//        if (item.schedule_exam != null)
//            Text(getDateString(
//                item.schedule_exam.date,
//                stringResource(id = R.string.navsubject_subject_datetimeformat),
//                "GMT+7")
//            )
//    }
//}
//
//@Composable
//fun SubjectsFeeItem(item: SubjectFeeItem) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 5.dp, bottom = 5.dp)
//            // https://www.android--code.com/2021/09/jetpack-compose-box-rounded-corners_25.html
//            .clip(RoundedCornerShape(10.dp))
//            .background(MaterialTheme.colorScheme.primaryContainer)
//            .padding(top = 10.dp, bottom = 10.dp),
//    ) {
//        Text("${item.id}")
//        Text("${item.name}")
//        Text("${item.credit}")
//        Text("${item.is_high_quality}")
//        Text("${item.price}")
//        Text("${item.debt}")
//        Text("${item.is_restudy}")
//    }
//}

@Composable
fun <T> Table(
    columnCount: Int,
    cellWidth: (index: Int) -> Dp,
    data: List<T>,
    modifier: Modifier = Modifier,
    headerCellContent: @Composable (index: Int) -> Unit,
    cellContent: @Composable (index: Int, item: T) -> Unit,
) {
    androidx.compose.material.Surface(
        modifier = modifier
    ) {
        LazyRow(
            modifier = Modifier.padding(10.dp)
        ) {
            items((0 until columnCount).toList()) { columnIndex ->
                Column {
                    (0..data.size).forEach { index ->
                        androidx.compose.material.Surface(
                            border = BorderStroke(1.dp, Color.LightGray),
                            contentColor = Color.Transparent,
                            modifier = Modifier.width(cellWidth(columnIndex))
                        ) {
                            if (index == 0) {
                                headerCellContent(columnIndex)
                            } else {
                                cellContent(columnIndex, data[index - 1])
                            }
                        }
                    }
                }
            }
        }
    }
}

//Bang hoc phi
@Composable
fun Demo_Table(item: List<SubjectFeeItem>, cacheData: AccountCacheData) {
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> 150.dp
            1 -> 70.dp
            2 -> 70.dp
            3 -> 90.dp
            else -> 150.dp
        }
    }
    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "Tên lớp học phần"
            1 -> "Số TC"
            2 -> "CLC"
            3 -> "Học phí"
            else -> ""
        }

        Text(
            text = value,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Black,
            textDecoration = TextDecoration.Underline
        )
    }

    val cellText: @Composable (Int, SubjectFeeItem) -> Unit = { index, item ->
        val value = when (index) {
            0 -> item.name.toString()
            1 -> item.credit.toString()
            2 -> if (item.is_high_quality==true) "YES" else if(item.is_high_quality==false) "NO" else ""
            3 -> item.price.toString()
            else -> ""
        }
        if (value == "Tổng cộng:"){
            Text(
                text = value,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = Color.Red,
                modifier = Modifier.padding(16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        else{
            Text(
                text = value,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    Table(
        columnCount = 4,
        data = item.plus(SubjectFeeItem(null,"Tổng cộng:",cacheData.subjectCredit,null,cacheData.subjectMoney.toInt(),false,false )),
        cellWidth = cellWidth,
        modifier = Modifier.verticalScroll(rememberScrollState()),
        headerCellContent = headerCellTitle,
        cellContent = cellText
    )
}

//Xem lich hoc va lich thi
@Composable
fun Test(item: SubjectScheduleItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        var visible by remember { mutableStateOf(false) }
        //header
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { visible = !visible }
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color(255,205,178))
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.name}"
                )
                Icon(
                    modifier = Modifier.rotate(animateFloatAsState(if (visible) 180f else 0f).value),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black.copy(ContentAlpha.disabled)
            )
        }

        Column {
            AnimatedVisibility(
                visible = visible,
                enter = expandVertically(
                    spring(
                        stiffness = Spring.StiffnessLow,
                        visibilityThreshold = IntSize.VisibilityThreshold
                    ),
                ),
                exit = shrinkVertically(),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Color(255,239,183))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mã lớp học phần: ${item.id} \nCLC: ${item.is_high_quality} \nGiảng viên: ${item.lecturer}",
                        )
                    }
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black
                    )
                }
            }

            var visible0 by remember { mutableStateOf(false) }
            AnimatedVisibility(
                visible = visible,
                enter = expandVertically(
                    spring(
                        stiffness = Spring.StiffnessLow,
                        visibilityThreshold = IntSize.VisibilityThreshold
                    ),
                ),
                exit = shrinkVertically(),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { visible0 = !visible0 }
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Color(212,252,216))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lịch học:",
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            modifier = Modifier.rotate(animateFloatAsState(if (visible0) 90f else 0f).value),
                            contentDescription = null
                        )
                    }
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black.copy(ContentAlpha.disabled)
                    )
                }
            }
            AnimatedVisibility(
                visible = visible0,
                enter = expandVertically(
                    spring(
                        stiffness = Spring.StiffnessLow,
                        visibilityThreshold = IntSize.VisibilityThreshold
                    ),
                ),
                exit = shrinkVertically(),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Color(191,252,253))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (item.schedule_study != null)
                            Text(
                                text = "Thứ: ${((item.schedule_study.schedule?.component1()?.day_of_week)?.toInt() ?:2 ) + 1} \nTiết: ${item.schedule_study.schedule?.component1()?.lesson?.start}-${item.schedule_study.schedule?.component1()?.lesson?.end} \nPhòng: ${item.schedule_study.schedule?.component1()?.room} \nTuần học: ${item.schedule_study.weeks?.component1()?.start}-${item.schedule_study.weeks?.component1()?.end}",
                            )
                    }
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black
                    )
                }
            }

            var visible1 by remember { mutableStateOf(false) }
            AnimatedVisibility(
                visible = visible,
                enter = expandVertically(
                    spring(
                        stiffness = Spring.StiffnessLow,
                        visibilityThreshold = IntSize.VisibilityThreshold
                    ),
                ),
                exit = shrinkVertically(),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { visible1 = !visible1 }
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Color(212,252,216))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lịch thi:",
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            modifier = Modifier.rotate(animateFloatAsState(if (visible1) 90f else 0f).value),
                            contentDescription = null
                        )
                    }
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black.copy(ContentAlpha.disabled)
                    )
                }
            }
            AnimatedVisibility(
                visible = visible1,
                enter = expandVertically(
                    spring(
                        stiffness = Spring.StiffnessLow,
                        visibilityThreshold = IntSize.VisibilityThreshold
                    ),
                ),
                exit = shrinkVertically(),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(color = Color(191,252,253))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (item.schedule_exam?.room != null)
                            Text(
                                text = "Nhóm thi: ${item.schedule_exam.group} \nThi chung: ${item.schedule_exam.is_global} \nNgày: " + DateToString(item.schedule_exam.date, stringResource(id = R.string.navsubject_subject_datetimeformat), "GMT+7").split(" ")[0] + "\nGiờ: " + DateToString(item.schedule_exam.date, stringResource(id = R.string.navsubject_subject_datetimeformat), "GMT+7").replace(":", "h").split(" ")[1] + "\nPhòng: ${item.schedule_exam.room}"
                            )
                        else
                            Text(
                                text = "Hiện chưa có lịch thi của môn này !"
                            )
                    }
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black
                    )
                }
            }
        }
    }
}