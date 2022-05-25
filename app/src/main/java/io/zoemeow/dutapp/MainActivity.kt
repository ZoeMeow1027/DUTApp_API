package io.zoemeow.dutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutapp.model.NewsGlobalItem
import io.zoemeow.dutapp.model.NewsSubjectItem
import io.zoemeow.dutapp.navbar.NavBarItemObject
import io.zoemeow.dutapp.navbar.NavRoutes
import io.zoemeow.dutapp.ui.theme.MyApplicationTheme
import io.zoemeow.dutapp.view.*
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val mainViewModel = viewModel<MainViewModel>()

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val tempType = remember { mutableStateOf(-1) }
    val tempItemNewsGlobal: MutableState<NewsGlobalItem> = remember { mutableStateOf(NewsGlobalItem()) }
    val tempItemNewsSubject: MutableState<NewsSubjectItem> = remember { mutableStateOf(NewsSubjectItem()) }

    if (!sheetState.isVisible) {
        tempItemNewsGlobal.value = NewsGlobalItem()
        tempItemNewsSubject.value = NewsSubjectItem()
        tempType.value = -1
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        // https://stackoverflow.com/a/68608137
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.onSecondary,
        sheetContent = {
            when(tempType.value) {
                0 -> NewsGlobalDetails(newsGlobalItem = tempItemNewsGlobal.value)
                1 -> NewsSubjectDetails(newsSubjectItem = tempItemNewsSubject.value)
                -1 -> Box() { Text("") }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    title = { Text(text = stringResource(id = R.string.topbar_name)) }
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) },
            content = { contentPadding ->
                NavigationHost(
                    navController = navController,
                    padding = contentPadding,
                    mainViewModel = mainViewModel,
                    newsGlobalItemReceived = {
                        tempItemNewsGlobal.value = it
                        tempType.value = 0
                        scope.launch { sheetState.show() }
                    },
                    newsSubjectItemReceived = {
                        tempItemNewsSubject.value = it
                        tempType.value = 1
                        scope.launch { sheetState.show() }
                    }
                )
            }
        )
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    padding: PaddingValues,
    mainViewModel: MainViewModel,
    newsGlobalItemReceived: (NewsGlobalItem) -> Unit,
    newsSubjectItemReceived: (NewsSubjectItem) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Account.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(NavRoutes.Home.route) {
            Home()
        }

        composable(NavRoutes.News.route) {
            News(
                mainViewModel = mainViewModel,
                newsGlobalItemReceived = { newsGlobalItemReceived(it) },
                newsSubjectItemReceived = { newsSubjectItemReceived(it) }
            )
        }

        composable(NavRoutes.Subject.route) {
            Subjects(
                mainViewModel = mainViewModel,
            )
        }

        composable(NavRoutes.Account.route) {
            Account(
                mainViewModel = mainViewModel
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItemObject.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(
                        navItem.route
                    ) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = navItem.imageId),
                        contentDescription = navItem.title
                    )
                },
                label = {
                    Text(
                        text = navItem.title,
                        style = (
                                if (currentRoute == navItem.route)
                                    MaterialTheme.typography.titleMedium
                                else MaterialTheme.typography.titleSmall
                                )
                    )
                },
            )
        }
    }
}