package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.model.AccountInformationItem
import io.zoemeow.dutapp.viewmodel.MainViewModel

@Composable
fun Settings(mainViewModel: MainViewModel) {
    when (mainViewModel.accountPaneIndex.value) {
        0 -> SettingsPreview(mainViewModel)
        1 -> AccountPageLogin(
            mainViewModel = mainViewModel,
            backRequest = { mainViewModel.accountPaneIndex.value = 0 }
        )
        2 -> AccountPageLoggingIn()
        3 -> AccountPageInformation(
            accInfo = mainViewModel.accCacheData.value.accountInformationData.value,
            isLoading = mainViewModel.procAccInfo.value,
            logout = { mainViewModel.logout() }
        )
    }
}

@Composable
fun SettingsPreview(mainViewModel: MainViewModel) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp)) {
        // Login
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clip(RoundedCornerShape(10.dp))
                .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp))
                .clickable {
                    if (mainViewModel.accCacheData.value.sessionID.value.isNotEmpty())
                        mainViewModel.accountPaneIndex.value = 3
                    else mainViewModel.accountPaneIndex.value = 1
                }
        ) {
            if (!mainViewModel.accCacheData.value.sessionID.value.isNotEmpty()) AccountTagNotLoggedIn()
            else AccountTagLoggedIn(id = mainViewModel.accCacheData.value.accountInformationData.value.studentId ?: String())
        }
    }
}

@Composable
fun AccountTagNotLoggedIn() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.navlogin_screennotloggedin_text1),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = stringResource(id = R.string.navlogin_screennotloggedin_text2),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPageLogin(
    mainViewModel: MainViewModel,
    backRequest: () -> Unit,
) {
    val user = remember { mutableStateOf(String()) }
    val pass = remember { mutableStateOf(String()) }
    val autoLogin = remember { mutableStateOf(false) }
    val passTextFieldFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val loginCommand: () -> Unit = {
        focusManager.clearFocus()
        mainViewModel.login(user.value, pass.value, autoLogin.value)
        pass.value = String()
    }

    Scaffold(
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .navigationBarsPadding()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Top,
                ) {
                    Text(
                        text = stringResource(id = R.string.navlogin_screenlogin_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
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
                    )
                    // Check box: Auto login
                    Row(
                        modifier = Modifier.clickable { autoLogin.value = !autoLogin.value },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = autoLogin.value,
                            onCheckedChange = { autoLogin.value = it },
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = stringResource(id = R.string.navlogin_screenlogin_rememberlogin)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = loginCommand,
                            content = { Text(stringResource(id = R.string.navlogin_screenlogin_btnlogin)) },
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Button(
                            onClick = backRequest,
                            content = { Text(stringResource(id = R.string.navlogin_screenlogin_btncancel)) },
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AccountTagLoggedIn(id: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = id,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = "Tap here to view your info or logout your account.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AccountPageLoggingIn() {
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
                text = "Logging you in...",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(15.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 90.dp, end = 90.dp)
            )
        }
    }
}

@Composable
fun AccountPageInformation(accInfo: AccountInformationItem, isLoading: Boolean, logout: () -> Unit) {
    val openDialog = remember { mutableStateOf(false) }

    if (isLoading) AccountPageLoadingYourInfo()
    else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = accInfo.name ?: String(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = accInfo.studentId ?: String(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = accInfo.specialization ?: String(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            data class AccInfoItem(
                val key: String,
                val value: String,
            )
            val list: ArrayList<AccInfoItem> = arrayListOf(
                AccInfoItem("Class", accInfo.schoolClass ?: String()),
                AccInfoItem("School Email", accInfo.schoolEmail ?: String()),
                AccInfoItem("Email", accInfo.personalEmail ?: String()),
                AccInfoItem("Facebook URL", accInfo.facebookUrl ?: String()),
                AccInfoItem("Phone Number", accInfo.phoneNumber ?: String()),
            )

            Spacer(modifier = Modifier.size(20.dp))
            LazyColumn {
                items(list) { item ->
                    Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 10.dp, end = 10.dp)) {
                        Text(
                            text = item.key,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.size(3.dp))
                        Text(
                            text = item.value,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(20.dp))
            Button(onClick = { openDialog.value = true }) {
                Text(stringResource(id = R.string.navlogin_loggedin_btnlogout))
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(15.dp),
            onDismissRequest = { openDialog.value = false },
            title = { Text("Logout") },
            text = { Text(text = "Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        logout()
                    },
                    content = {
                        Text("Yes, log me out")
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog.value = false },
                    content = {
                        Text("No")
                    }
                )
            }
        )
    }
}

@Composable
fun AccountPageLoadingYourInfo() {
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
                text = "Loading your information...",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(15.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 90.dp, end = 90.dp)
            )
        }
    }
}
