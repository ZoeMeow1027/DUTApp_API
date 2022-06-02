package io.zoemeow.dutapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.model.subject.SubjectSchoolYearSettings
import io.zoemeow.dutapp.viewmodel.MainViewModel

@Composable
fun Settings(mainViewModel: MainViewModel) {
    when (mainViewModel.accountPaneIndex.value) {
        0 -> SettingsMain(mainViewModel)
        1 -> AccountPageLogin(
            mainViewModel = mainViewModel,
            backRequest = { mainViewModel.accountPaneIndex.value = 0 }
        )
        2 -> AccountPageLoggingIn()
        3 -> AccountPageInformation(
            accInfo = mainViewModel.accCacheData.value.accountInformationData.value,
            isLoading = mainViewModel.isProcessingAccountInfo.value,
        )
    }
}

@Composable
fun SettingsMain(mainViewModel: MainViewModel) {
    val dialogLogoutEnabled = remember { mutableStateOf(false) }
    val dialogSchoolYearEnabled = remember { mutableStateOf(false) }

    DialogLogout(
        enabled = dialogLogoutEnabled,
        logoutRequest = { mainViewModel.logout() }
    )
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsOptionAccount(
            mainViewModel,
            toggleLogout = { dialogLogoutEnabled.value = true }
        )
        Divider(thickness = 0.5.dp)
        SettingsOpenSchoolYear(
            mainViewModel.getSubjectSchoolYearSettings(),
            toggleSchoolYear = { }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DialogLogout(
    enabled: MutableState<Boolean>,
    logoutRequest: () -> Unit
) {
    // Alert dialog for logout
    if (enabled.value) {
        AlertDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            onDismissRequest = { enabled.value = false },
            title = { Text("Logout") },
            text = {
                Text(
                    text = "Logout will clear subjects cache. You will need to login again to continue receiving subjects.\nAre you sure you want to continue?"
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { enabled.value = false },
                    content = {
                        Text("No")
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        enabled.value = false
                        logoutRequest()
                    },
                    content = {
                        Text("Yes, log me out")
                    }
                )
            },
        )
    }
}

@Composable
fun SettingsOptionAccount(
    mainViewModel: MainViewModel,
    toggleLogout: () -> Unit
) {
    Box {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    text = "Account",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            // If not logged in
            if (mainViewModel.accCacheData.value.sessionID.value.isEmpty() &&
                !mainViewModel.isAvailableOffline()) {
                // Login
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clickable { mainViewModel.accountPaneIndex.value = 1 },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = "To use more app features, you need to sign in.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            // If logged in/offline mode and logged in previously.
            else {
                Column {
                    // Account Information
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clickable { mainViewModel.accountPaneIndex.value = 3 },
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                            text = "View Account Information",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    // Re-login
                    if (mainViewModel.accCacheData.value.sessionID.value.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .clickable { mainViewModel.executeAutoLogin() },
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                            ) {
                                Text(
                                    text = "Re-login",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                Text(
                                    text = "You have saved your login, but you haven't been logged in to system. Click here to re-login.",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                    // Logout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clickable { toggleLogout() },
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                            Text(
                                text = "Logout",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = "Logged in as ${mainViewModel.accCacheData.value.accountInformationData.value.studentId}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsOpenSchoolYear(
    schoolYearSettings: SubjectSchoolYearSettings,
    toggleSchoolYear: () -> Unit
) {
    Box() {
        Column() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                Column() {
                    Text(
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        text = "Subject",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            // Setting for School year.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                    //.clickable { toggleSchoolYear() },
                contentAlignment = Alignment.CenterStart,
            ) {
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                    Text(
                        text = "Set school year (Currently disabled)",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Currently: School Year ${schoolYearSettings.subjectYear}, Semester ${schoolYearSettings.subjectSemester}${if (schoolYearSettings.subjectInSummer) " (in summer)" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
