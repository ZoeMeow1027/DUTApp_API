package io.zoemeow.dutapp.navbar

sealed class NavLoginRoutes(val route: String) {
    object AccountPageNotLoggedIn : NavLoginRoutes("notloggedin")
    object AccountPageLogin : NavLoginRoutes("login")
    object AccountPageLoggedIn : NavLoginRoutes("loggedin")
}