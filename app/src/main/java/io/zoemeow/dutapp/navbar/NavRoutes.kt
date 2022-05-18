package io.zoemeow.dutapp.navbar

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object News : NavRoutes("news")
    object Account : NavRoutes("account")
}
