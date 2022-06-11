package io.zoemeow.dutapp.view

import androidx.compose.foundation.background
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
import io.zoemeow.dutapp.model.account.AccountInformationItem
import io.zoemeow.dutapp.ui.customs.LoadingFullScreen
import io.zoemeow.dutapp.viewmodel.MainViewModel

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
fun AccountPageInformation(accInfo: AccountInformationItem, isLoading: Boolean) {
    if (isLoading) {
        LoadingFullScreen(
            title = "Please wait",
            contentList = arrayListOf(
                "Loading your information..."
            )
        )
    }
    else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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
        }
    }
}