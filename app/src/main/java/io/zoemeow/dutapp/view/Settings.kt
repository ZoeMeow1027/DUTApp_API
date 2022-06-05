package io.zoemeow.dutapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.zoemeow.dutapp.BuildConfig
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsMain(mainViewModel: MainViewModel) {
    val dialogLogoutEnabled = remember { mutableStateOf(false) }

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
        SettingsOptionSubject(
            mainViewModel.getSubjectSchoolYearSettings()
        )
        Divider(thickness = 0.5.dp)
        SettingsOptionAppSettings()
        Divider(thickness = 0.5.dp)
        SettingsOptionAppInfo(
            linkClicked = { mainViewModel.openLinkInBrowser(it) }
        )
    }
}

@ExperimentalComposeUiApi
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
    Column {
        SettingsOptionTitle(text = "Account")
        // If not logged in
        if (mainViewModel.accCacheData.value.sessionID.value.isEmpty() &&
            !mainViewModel.isAvailableOffline()) {
            // Login
            SettingsOptionLayout(
                textAbove = "Login",
                textBelow = "To use more app features, you need to sign in.",
                clickable = { mainViewModel.accountPaneIndex.value = 1 }
            )
        }
        // If logged in/offline mode and logged in previously.
        else {
            Column {
                // Account Information
                SettingsOptionLayout(
                    textAbove = "View Account Information",
                    clickable = { mainViewModel.accountPaneIndex.value = 3 }
                )
                // Re-login
                if (mainViewModel.accCacheData.value.sessionID.value.isEmpty()) {
                    SettingsOptionLayout(
                        textAbove = "Re-login",
                        textBelow = "You have saved your login, but you haven't been logged in to system. Click here to re-login.",
                        clickable = { mainViewModel.executeAutoLogin() }
                    )
                }
                // Logout
                SettingsOptionLayout(
                    textAbove = "Logout",
                    textBelow = "Logged in as ${mainViewModel.accCacheData.value.accountInformationData.value.studentId}",
                    clickable = { toggleLogout() }
                )
            }
        }
    }
}

@Composable
fun SettingsOptionSubject(
    schoolYearSettings: SubjectSchoolYearSettings,
) {
    Column {
        SettingsOptionTitle(text = "Subject")
        // Setting for School year.
        SettingsOptionLayout(
            textAbove = "Set school year (Currently disabled)",
            textBelow = "Currently: School Year ${schoolYearSettings.subjectYear}, Semester ${schoolYearSettings.subjectSemester}${if (schoolYearSettings.subjectInSummer) " (in summer)" else ""}",
            clickable = { }
        )
    }
}

@Composable
fun SettingsOptionAppSettings() {
    Column {
        SettingsOptionTitle(text = "Settings")
        SettingsOptionLayout(
            textAbove = "App theme",
            textBelow = "Following system theme (This feature are under development. Stay tuned!)",
            clickable = {  }
        )
        SettingsOptionLayout(
            textAbove = "Black backgrounds in dark theme (for AMOLED)",
            textBelow = "No (This feature are under development. Stay tuned!)",
            clickable = {  }
        )
    }
}

@Composable
fun SettingsOptionAppInfo(
    linkClicked: (String) -> Unit
) {
    Column {
        SettingsOptionTitle(text = "App information")
        SettingsOptionLayout(
            textAbove = "Version",
            textBelow = BuildConfig.VERSION_NAME,
            clickable = { }
        )
        SettingsOptionLayout(
            textAbove = "Changelog",
            textBelow = "(This feature are under development. Stay tuned!)",
            clickable = { }
        )
        SettingsOptionLayout(
            textAbove = "GitHub (click to open in browser)",
            textBelow = "https://github.com/ZoeMeow5466/DUTApp_API",
            clickable = { linkClicked("https://github.com/ZoeMeow5466/DUTApp_API") }
        )
    }
}

@Composable
fun SettingsOptionTitle(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Column {
            Text(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun SettingsOptionLayout(
    textAbove: String,
    textBelow: String? = null,
    clickable: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { clickable() },
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
            Text(
                text = textAbove,
                style = MaterialTheme.typography.titleMedium,
            )
            if (textBelow != null) {
                Text(
                    text = textBelow,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
