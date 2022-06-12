package io.zoemeow.dutapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.zoemeow.dutapp.BuildConfig
import io.zoemeow.dutapp.model.enums.ProcessResult
import io.zoemeow.dutapp.ui.customs.LoadingFullScreen
import io.zoemeow.dutapp.ui.customs.SettingsPanel_Divider
import io.zoemeow.dutapp.ui.customs.SettingsPanel_LayoutOptionItem
import io.zoemeow.dutapp.viewmodel.MainViewModel

@Composable
fun Settings(
    mainViewModel: MainViewModel,
    currentPage: MutableState<Int>
) {
    val isLoadingInfo = (
            if (mainViewModel.tempVarData["AccInfo"].value != null)
                mainViewModel.tempVarData["AccInfo"].value!!.toInt() == ProcessResult.Running.result
            else false
            )

    when (currentPage.value) {
        0 -> SettingsMain(mainViewModel)
        1 -> AccountPageLogin(
            mainViewModel = mainViewModel,
            backRequest = { mainViewModel.tempVarData["SettingsPanelIndex"] = "0" }
        )
        2 -> LoadingFullScreen(
            title = "Please wait",
            contentList = arrayListOf(
                "Logging you in..."
            )
        )
        3 -> AccountPageInformation(
            accInfo = mainViewModel.accCacheData.accountInformationData.value,
            isLoading = isLoadingInfo,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsMain(mainViewModel: MainViewModel) {
    val dialogLogoutEnabled = remember { mutableStateOf(false) }
    val optionsScrollState = rememberScrollState()

    DialogLogout(
        enabled = dialogLogoutEnabled,
        logoutRequest = { mainViewModel.logout() }
    )
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(optionsScrollState)
    ) {
        SettingsOptionAccount(
            mainViewModel,
            toggleLogout = { dialogLogoutEnabled.value = true }
        )
        SettingsPanel_Divider()
        SettingsOptionSubject(
            mainViewModel.subjectSchoolYearSettings
        )
        SettingsPanel_Divider()
        SettingsOptionAppSettings()
        SettingsPanel_Divider()
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
        if (!mainViewModel.isLoggedIn() && !mainViewModel.isAvailableOffline()) {
            // Login
            SettingsPanel_LayoutOptionItem(
                textAbove = "Login",
                textBelow = "To use more app features, you need to sign in.",
                clickable = { mainViewModel.tempVarData["SettingsPanelIndex"] = "1" }
            )
        }
        // If logged in/offline mode and logged in previously.
        else {
            Column {
                // Account Information
                SettingsPanel_LayoutOptionItem(
                    textAbove = "View Account Information",
                    textBelow = "View your information which is saved in sv.dut.udn.vn",
                    clickable = { mainViewModel.tempVarData["SettingsPanelIndex"] = "3" }
                )
                // Re-login
                if (!mainViewModel.isLoggedIn()) {
                    SettingsPanel_LayoutOptionItem(
                        textAbove = "Re-login",
                        textBelow = "You have saved your login, but you haven't been logged in to system. Click here to re-login.",
                        clickable = { mainViewModel.executeAutoLogin() }
                    )
                }
                // Logout
                SettingsPanel_LayoutOptionItem(
                    textAbove = "Logout",
                    textBelow = "Logged in as ${mainViewModel.accCacheData.accountInformationData.value.studentId}",
                    clickable = { toggleLogout() }
                )
            }
        }
    }
}

@Composable
fun SettingsOptionSubject(
    schoolYearSettings: ArrayList<Int>,
) {
    Column {
        SettingsOptionTitle(text = "Subject")
        // Setting for School year.
        SettingsPanel_LayoutOptionItem(
            textAbove = "Set school year (Currently disabled)",
            textBelow = "Currently: School Year ${schoolYearSettings[0]}, Semester ${schoolYearSettings[1]}${if (schoolYearSettings[2] == 1) " (in summer)" else ""}",
            clickable = { }
        )
    }
}

@Composable
fun SettingsOptionAppSettings() {
    Column {
        SettingsOptionTitle(text = "Settings")
        SettingsPanel_LayoutOptionItem(
            textAbove = "App theme",
            textBelow = "Following system theme (This feature are under development. Stay tuned!)",
            clickable = {  }
        )
        SettingsPanel_LayoutOptionItem(
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
        SettingsPanel_LayoutOptionItem(
            textAbove = "Version",
            textBelow = BuildConfig.VERSION_NAME,
            clickable = { }
        )
        SettingsPanel_LayoutOptionItem(
            textAbove = "Changelog",
            textBelow = "(This feature are under development. Stay tuned!)",
            clickable = { }
        )
        SettingsPanel_LayoutOptionItem(
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
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}