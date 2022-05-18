package io.zoemeow.dutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutapp.navbar.NavBarItemObject
import io.zoemeow.dutapp.navbar.NavRoutes
import io.zoemeow.dutapp.ui.theme.MyApplicationTheme
import io.zoemeow.dutapp.view.Account
import io.zoemeow.dutapp.view.Home
import io.zoemeow.dutapp.view.News
import io.zoemeow.dutapp.viewmodel.NewsViewModel

@ExperimentalPagerApi
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val newsViewModel = viewModel<NewsViewModel>()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background),
                title = {
                    Text(text = stringResource(id = R.string.topbar_name) )
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) {
        contentPadding ->
        NavigationHost(navController = navController, contentPadding, newsViewModel)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NavigationHost(navController: NavHostController, padding: PaddingValues, newsViewModel: NewsViewModel) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.News.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(NavRoutes.Home.route) {
            Home()
        }

        composable(NavRoutes.News.route) {
            News(newsViewModel)
        }

        composable(NavRoutes.Account.route) {
            Account()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar() {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItemObject.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
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
                    )},
                label = {
                    Text(
                        text = navItem.title
                    )},
            )
        }
    }
}