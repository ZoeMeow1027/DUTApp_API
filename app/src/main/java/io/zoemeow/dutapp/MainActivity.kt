package io.zoemeow.dutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import androidx.compose.ui.platform.LocalContext
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
import io.zoemeow.dutapp.data.NewsDetailsClicked
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val mainViewModel = viewModel<MainViewModel>()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    mainViewModel.setNewsDetailClicked(NewsDetailsClicked(
        showSheetRequested = {
            scope.launch { sheetState.show() }
        },
        hideSheetRequested = {
            scope.launch { sheetState.hide() }
        }
    ))
    mainViewModel.setSnackBarHostState(snackBarHostState)
    mainViewModel.setContext(LocalContext.current)

    // Best solution (at this time): https://stackoverflow.com/a/69052933
    // However, this isn't a good idea, because animation will be executed for 1s.
    // So, we won't able to manage this here!
    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }
            .collect {
                if (it == ModalBottomSheetValue.Hidden)
                    mainViewModel.newsDetailsClicked.value?.clearViewDetails()
            }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.onSecondary,
        sheetContent = {
            when(mainViewModel.newsDetailsClicked.value!!.newsType.value) {
                0 -> NewsGlobalDetails(
                    newsGlobalItem = mainViewModel.newsDetailsClicked.value!!.newsGlobal.value
                )
                1 -> NewsSubjectDetails(
                    newsSubjectItem = mainViewModel.newsDetailsClicked.value!!.newsSubject.value
                )
                -1 -> Box { Text("") }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
//            topBar = {
//                CenterAlignedTopAppBar(
//                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
//                    title = { Text(text = stringResource(id = R.string.topbar_name)) }
//                )
//            },
            bottomBar = { BottomNavigationBar(navController = navController) },
            content = { contentPadding ->
                NavigationHost(
                    navController = navController,
                    padding = contentPadding,
                    mainViewModel = mainViewModel
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
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.News.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(NavRoutes.Home.route) {
            Home()
        }

        composable(NavRoutes.News.route) {
            // If sheet still display, hide them, else will return to main screen.
            BackHandler(
                enabled = (mainViewModel.newsDetailsClicked.value!!.newsType.value != -1),
                onBack = { mainViewModel.newsDetailsClicked.value!!.hideViewDetails() },
            )
            News(mainViewModel = mainViewModel)
        }

        composable(NavRoutes.Subject.route) {
            Subjects(mainViewModel = mainViewModel)
        }

        composable(NavRoutes.Settings.route) {
            // If still in Login, roll back to Not Logged In, else will return to main screen.
            BackHandler(
                enabled = (mainViewModel.accountPaneIndex.value != 0),
                onBack = {
                    if (mainViewModel.accountPaneIndex.value != 2)
                        mainViewModel.accountPaneIndex.value = 0
                }
            )
            Settings(mainViewModel = mainViewModel)
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
                        style = MaterialTheme.typography.titleSmall
                    )
                },
            )
        }
    }
}