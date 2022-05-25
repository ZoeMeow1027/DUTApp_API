package io.zoemeow.dutapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.zoemeow.dutapp.R
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
                if (!mainViewModel.isLoggedIn())
                    NavLoginRoutes.AccountPageNotLoggedIn.route
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
            mainViewModel.getSubjectScheduleAndFee(21, 2, false)
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
            Column {
                Text(
                    text = stringResource(id = R.string.navlogin_screennotloggedin_text1),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(id = R.string.navlogin_screennotloggedin_text2),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.size(15.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = loginRequest,
            content = { Text(stringResource(id = R.string.navlogin_screennotloggedin_btnlogin)) },
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
    val context = LocalContext.current

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
                    // Progress bar for logging in...
                    if (clicked.value) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(stringResource(id = R.string.navlogin_screenlogin_title), style = MaterialTheme.typography.headlineMedium)
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        value = user.value,
                        label = { Text(stringResource(id = R.string.navlogin_screenlogin_username)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onGo = { passTextFieldFocusRequester.requestFocus() }
                        ),
                        onValueChange = { user.value = it },
                        enabled = enabledControls.value,
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .focusRequester(passTextFieldFocusRequester),
                        value = pass.value,
                        label = { Text(stringResource(id = R.string.navlogin_screenlogin_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(onGo = { loginCommand() }),
                        onValueChange = { pass.value = it },
                        enabled = enabledControls.value,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            enabled = enabledControls.value,
                            onClick = loginCommand,
                            content = { Text(stringResource(id = R.string.navlogin_screenlogin_btnlogin)) },
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Button(
                            enabled = enabledControls.value,
                            onClick = backRequest,
                            content = { Text(stringResource(id = R.string.navlogin_screenlogin_btncancel)) },
                        )
                    }
                    AccountCheckLogin(
                        mainViewModel = mainViewModel,
                        clicked = clicked.value,
                        loginFail = {
                            scope.launch {
                                clicked.value = false
                                snackBarHostState.showSnackbar(context.getString(R.string.navlogin_screenlogin_loginfailed))
                            }
                            enabledControls.value = true
                        },
                        loginSuccess = {
                            user.value = String()
                            pass.value = String()
                            clicked.value = false
                            enabledControls.value = true
                            loggedInRequest()
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
        if (!mainViewModel.isProcessingAccount().value) {
            if (!mainViewModel.isLoggedIn()) {
                loginFail()
            } else loginSuccess()
        }
    }
}

@Composable
fun AccountPageLoggedIn(logout: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = logout
        ) {
            Text(stringResource(id = R.string.navlogin_loggedin_btnlogout))
        }
    }
}

