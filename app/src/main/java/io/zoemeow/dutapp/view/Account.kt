package io.zoemeow.dutapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.zoemeow.dutapp.navbar.NavLoginRoutes
import io.zoemeow.dutapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun Account(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    AccountNavigationHost(mainViewModel, navController)
}

@Composable
fun AccountNavigationHost(mainViewModel: MainViewModel, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = (
                if (!mainViewModel.loggedIn()) NavLoginRoutes.AccountPageNotLoggedIn.route
                else NavLoginRoutes.AccountPageLoggedIn.route
                )
    ) {
        composable(NavLoginRoutes.AccountPageNotLoggedIn.route) {
            AccountPageNotLoggedIn(
                loginRequest = { navController.navigate(NavLoginRoutes.AccountPageLogin.route) }
            )
        }

        composable(NavLoginRoutes.AccountPageLogin.route) {
            AccountPageLogin(
                mainViewModel,
                backRequest = { navController.popBackStack() },
                loggedInRequest = {
                    navController.navigate(NavLoginRoutes.AccountPageLoggedIn.route) {
                        popUpTo(NavLoginRoutes.AccountPageNotLoggedIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavLoginRoutes.AccountPageLoggedIn.route) {
            AccountPageLoggedIn(
                logout = {
                    mainViewModel.logout()
                    navController.navigate(NavLoginRoutes.AccountPageNotLoggedIn.route) {
                        popUpTo(NavLoginRoutes.AccountPageLogin.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun AccountPageNotLoggedIn(loginRequest: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)) {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column() {
                Text(
                    text = "You are not logged in.",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Login to access more features, just for you :)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.size(15.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = loginRequest,
            content = { Text("Login") },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPageLogin(
    mainViewModel: MainViewModel,
    backRequest: () -> Unit,
    loggedInRequest: () -> Unit
) {
    val user = remember { mutableStateOf(String()) }
    val pass = remember { mutableStateOf(String()) }
    val enabledControls = remember { mutableStateOf(true) }

    val snackBarHostState = remember { SnackbarHostState() }
    val clicked = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val passTextFieldFocusRequester = remember { FocusRequester() }

    val loginCommand: () -> Unit = {
        clicked.value = true
        mainViewModel.login(user.value, pass.value)
        enabledControls.value = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .wrapContentHeight()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Top,
                ) {
                    if (clicked.value) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Text("Login", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.size(10.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = user.value,
                        label = { Text("Username") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onGo = { passTextFieldFocusRequester.requestFocus() }
                        ),
                        onValueChange = { user.value = it },
                        enabled = enabledControls.value,
                    )
                    Spacer(Modifier.size(10.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passTextFieldFocusRequester),
                        value = pass.value,
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(onGo = { loginCommand() }),
                        onValueChange = { pass.value = it },
                        enabled = enabledControls.value,
                    )
                    Spacer(Modifier.size(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            enabled = enabledControls.value,
                            onClick = loginCommand,
                            content = { Text("Login") },
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Button(
                            enabled = enabledControls.value,
                            onClick = backRequest,
                            content = { Text("Cancel") },
                        )
                    }
                    AccountCheckLogin(
                        mainViewModel,
                        clicked.value,
                        loginFail = {
                            scope.launch {
                                clicked.value = false
                                snackBarHostState.showSnackbar(
                                    "Logging in failed! Check your account and try again."
                                )
                            }
                            enabledControls.value = true
                        },
                        loginSuccess = {
                            user.value = String()
                            pass.value = String()
                            clicked.value = false
                            enabledControls.value = true
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun AccountCheckLogin(
    mainViewModel: MainViewModel,
    clicked: Boolean,
    loginFail: () -> Unit,
    loginSuccess: () -> Unit
) {
    if (clicked) {
        if (!mainViewModel.isProcessing().value) {
            if (!mainViewModel.loggedIn()) {
                loginFail()
            } else loginSuccess()
        }
    }
}

@Composable
fun AccountPageLoggedIn(logout: () -> Unit) {
    Column() {
        Button(
            onClick = logout
        ) {
            Text("Logout")
        }
    }
}
