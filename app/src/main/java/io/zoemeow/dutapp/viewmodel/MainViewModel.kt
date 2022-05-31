package io.zoemeow.dutapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.data.*
import io.zoemeow.dutapp.model.*
import io.zoemeow.dutapp.repository.*
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dutNewsRepo: DutNewsApiRepository,
    private val dutAccRepo: DutAccountApiRepository,
    private val appSettingsRepo: AppSettingsFileRepository,
    private val newsCacheFileRepo: NewsCacheFileRepository,
) : ViewModel() {
    // Get SnackBar host state from main activity
    private val mainActivitySnackBarHostState: MutableState<SnackbarHostState?> = mutableStateOf(null)
    fun setSnackBarHostState(item: SnackbarHostState) {
        mainActivitySnackBarHostState.value = item
    }

    // Get context main activity
    private val mainActivityContext: MutableState<Context?> = mutableStateOf(null)
    fun setContext(item: Context) {
        mainActivityContext.value = item
    }

    // News Details View when clicked a news.
    internal val newsDetailsClicked: MutableState<NewsDetailsClicked?> = mutableStateOf(null)
    fun setNewsDetailClicked(item: NewsDetailsClicked) {
        newsDetailsClicked.value = item
    }

    // Exception will be saved here.
    private val exceptionCacheData: MutableState<ExceptionCacheData> = mutableStateOf(ExceptionCacheData())

    // News data with cache (for easier manage).
    internal val newsCacheData: MutableState<NewsCacheData> = mutableStateOf(NewsCacheData())

    // Generate md5 from string.
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    // Get news global.
    // Check if is getting news global
    internal val procGlobal: MutableState<Boolean> = mutableStateOf(false)

    // Check if in startup in process global.
    // Useful for prevent  auto refresh due to switch navigation.
    private val procGlobalInit: MutableState<Boolean> = mutableStateOf(true)

    // Refresh news global
    fun refreshNewsGlobalFromServer(page: Int = 1, force: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!procGlobalInit.value && !force) {
                    Log.d("ProcGlobal", "App not forced. Set force to true to load.")
                    return@launch
                }

                procGlobal.value = true
                val dataGlobalFromInternet: NewsGlobalListItem = dutNewsRepo.getNewsGlobal(page)

                if (dataGlobalFromInternet.newsList != null) {
                    newsCacheData.value.NewsGlobalData.value.clear()
                    newsCacheFileRepo.deleteAllNewsGlobal()

                    val list = ArrayList<NewsGlobalItem>()
                    for (newsItem: NewsGlobalItem in dataGlobalFromInternet.newsList) {
                        val value = NewsGlobalItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            contentText = newsItem.contentText,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = md5("${newsItem.date}-${newsItem.title}")
                        )
                        list.add(value)
                    }

                    newsCacheFileRepo.setNewsGlobal(list)
                    newsCacheData.value.NewsGlobalData.value.addAll(list)
                } else throw Exception("Empty news list")

                procGlobal.value = false
                procGlobalInit.value = false
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
                procGlobal.value = false
                procGlobalInit.value = false

                // Notify that can't load news here.
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
                )
            }
        }
    }

    // Get news subjects
    // Check if is getting news subject
    internal val procSubjects: MutableState<Boolean> = mutableStateOf(false)

    // Check if in startup in process subject.
    // Useful for prevent  auto refresh due to switch navigation.

    private val procSubjectsInit: MutableState<Boolean> = mutableStateOf(true)
    fun refreshNewsSubjectsFromServer(page: Int = 1, force: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!procGlobalInit.value && !force) {
                    Log.d("ProcGlobal", "App not forced. Set force to true to load.")
                    return@launch
                }

                procSubjects.value = true
                val dataSubjectsFromInternet: NewsSubjectListItem = dutNewsRepo.getNewsSubject(page)
                if (dataSubjectsFromInternet.newsList != null) {
                    newsCacheData.value.NewsSubjectData.value.clear()
                    newsCacheFileRepo.deleteAllNewsSubject()

                    val list = ArrayList<NewsSubjectItem>()
                    for (newsItem: NewsSubjectItem in dataSubjectsFromInternet.newsList) {
                        list.add(NewsSubjectItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            contentText = newsItem.contentText,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = md5("${newsItem.date}-${newsItem.title}")
                        ))
                    }

                    newsCacheFileRepo.setNewsSubject(list)
                    newsCacheData.value.NewsSubjectData.value.addAll(list)
                } else throw Exception("Empty news list")

                procSubjects.value = false
                procSubjectsInit.value = false
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
                procSubjects.value = false
                procSubjectsInit.value = false

                // Notify that can't load news here.
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
                )
            }
        }
    }


    // Settings View.
    // 0: Settings (and/or not logged in page)
    // 1: Login page
    // 2: Logging in page
    // 3: Account Information page
    internal val accountPaneIndex = mutableStateOf(0)

    // Settings Information
    internal val accCacheData: MutableState<AccountCacheData> = mutableStateOf(
        AccountCacheData()
    )

    // Check if have auto login
    private val accLoginStartup = mutableStateOf(false)

    // Log in using your account
    fun login(user: String, pass: String, rememberLogin: Boolean = true) {
        viewModelScope.launch {
            // Navigate to page logging in
            accountPaneIndex.value = 2

            try {
                // Login
                val result = dutAccRepo.dutLogin(user, pass)

                // If login successfully
                if (result.loggedIn) {
                    // Save session id to cache
                    accCacheData.value.sessionID.value = result.sessionId!!

                    // Only logged in will can remember login
                    if (rememberLogin) {
                        // Save to app settings
                        appSettingsRepo.autoLogin = rememberLogin
                        appSettingsRepo.username = user
                        appSettingsRepo.password = pass
                    }

                    // Pre-load subject schedule, fee and account information
                    getSubjectScheduleAndFee(21, 2, false)
                    getAccountInformation()
                }
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
            }

            // All result will be returned to main page.
            accountPaneIndex.value = 0

            // If logged in (check session id is not empty)
            if (accCacheData.value.sessionID.value.isNotEmpty()) {
                // Navigate to page logged in
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_loginsuccessful)!!
                )
            }
            // If failed login at startup, will clear all auto login settings
            // and return back to login page
            else if (accLoginStartup.value) {
                accountPaneIndex.value = 0
                clearAutoLogin()
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_autologinfailed)!!
                )
                accLoginStartup.value = false
            }
            // Any failed while logging in will be return to login/not logged in.
            else {
                accountPaneIndex.value = 1
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_loginfailed)!!
                )
            }
        }
    }

    // Clear auto login settings
    private fun clearAutoLogin() {
        appSettingsRepo.autoLogin = false
        appSettingsRepo.username = null
        appSettingsRepo.password = null
    }

    fun logout() {
        viewModelScope.launch {
            try {
                // Clear auto login settings
                clearAutoLogin()

                // Get information before logout
                val temp = accCacheData.value.sessionID.value
                accCacheData.value.clearAllData()

                // Navigate to page not logged in
                accountPaneIndex.value = 0

                // Show snack bar logged out
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_loggedout)!!
                )

                // Logout
                dutAccRepo.dutLogout(temp)
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
            }
        }
    }

    internal var procSubjectSchedule = mutableStateOf(false)

    // Get subject schedule and subject fee
    fun getSubjectScheduleAndFee(year: Int, semester: Int, inSummer: Boolean) {
        viewModelScope.launch {
            try {
                procSubjectSchedule.value = true

                // Get subject schedule
                val dataSubjectScheduleFromInternet = dutAccRepo.dutGetSubjectSchedule(
                    accCacheData.value.sessionID.value, year, semester, inSummer)
                // Add to cache
                if (dataSubjectScheduleFromInternet.scheduleList != null &&
                        dataSubjectScheduleFromInternet.scheduleList.size > 0)
                    accCacheData.value.subjectScheduleData.value = dataSubjectScheduleFromInternet.scheduleList

                // Get subject fee
                val dataSubjectFeeFromInternet = dutAccRepo.dutGetSubjectFee(
                    accCacheData.value.sessionID.value, year, semester, inSummer)
                // Add to cache
                if (dataSubjectFeeFromInternet.feeList != null &&
                        dataSubjectFeeFromInternet.feeList.size > 0)
                    accCacheData.value.subjectFeeData.value = dataSubjectFeeFromInternet.feeList
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
            }

            procSubjectSchedule.value = false
        }
    }

    internal val procAccInfo = mutableStateOf(false)

    // Get account information
    fun getAccountInformation() {
        viewModelScope.launch {
            try {
                procAccInfo.value = true

                // Get account information
                val dataAccInfoFromInternet = dutAccRepo.dutGetAccInfo(
                    accCacheData.value.sessionID.value)
                // Add to cache
                if (dataAccInfoFromInternet.accountInfo != null)
                    accCacheData.value.accountInformationData.value = dataAccInfoFromInternet.accountInfo
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
            }

            procAccInfo.value = false
        }
    }

    // Load news cache for backup if internet is not available.
    private fun loadCache() {
        newsCacheData.value.NewsGlobalData.value.addAll(newsCacheFileRepo.getNewsGlobal())
        newsCacheData.value.NewsSubjectData.value.addAll(newsCacheFileRepo.getNewsSubject())
    }

    // Detect auto login (login if user checked auto login check box)
    private fun executeAutoLogin() {
        if (appSettingsRepo.autoLogin) {
            accLoginStartup.value = true
            if (appSettingsRepo.username != null && appSettingsRepo.password != null)
                login(appSettingsRepo.username!!, appSettingsRepo.password!!)
        }
    }

    // Load settings from appSettings.json.
    private fun loadSettings() {

    }

    init {
        // Load news cache for backup if internet is not available.
        loadCache()

        // Load settings first before continue.
        loadSettings()

        // Auto refresh news in server at startup.
        refreshNewsGlobalFromServer()
        refreshNewsSubjectsFromServer()

        // Detect auto login (login if user checked auto login check box)
        executeAutoLogin()
    }
}
