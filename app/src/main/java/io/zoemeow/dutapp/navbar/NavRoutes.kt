package io.zoemeow.dutapp.navbar

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object News : NavRoutes("news")
    object Subject : NavRoutes("subject")
    object Settings : NavRoutes("settings")
}
