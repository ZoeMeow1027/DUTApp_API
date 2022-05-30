package io.zoemeow.dutapp.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.data.*
import io.zoemeow.dutapp.model.*
import io.zoemeow.dutapp.repository.*
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dutNewsRepo: DutNewsRepository,
    private val dutAccRepo: DutAccountRepository,
    private val appSettingsRepo: AppSettingsRepository,
    private val newsCacheFileRepo: NewsCacheFileRepository,
) : ViewModel() {
    // Exception will be saved here.
    private val exceptionCacheData: MutableState<ExceptionCacheData> = mutableStateOf(ExceptionCacheData())

    // News Details View when clicked a news.
    val newsDetailsClicked: MutableState<NewsDetailsClicked?> = mutableStateOf(null)

    // Account View.
    // 0: Not logged in, 1: Login, 2: Logged In
    val accountPaneIndex = mutableStateOf(0)

    // News data with cache (for easier manage).
    private val newsCacheData: MutableState<NewsCacheData> = mutableStateOf(NewsCacheData())
    val newsData: MutableState<NewsCacheData>
        get() = newsCacheData

    // Generate md5 from string.
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    // Get news global.
    // Check if is getting news global
    private val procGlobal: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingNewsGlobal(): MutableState<Boolean> { return procGlobal }

    private val procGlobalInit: MutableState<Boolean> = mutableStateOf(true)

    // Refresh news global
    fun refreshNewsGlobalFromServer(page: Int = 1, force: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!procGlobalInit.value && !force)
                    throw Exception("App not forced. Set force to true to load.")

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
                }
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                Log.d("NewsGlobal", ex.message.toString())
            }
            procGlobal.value = false
            procGlobalInit.value = false
        }
    }

    // Get news subjects
    // Check if is getting news subject
    private val procSubjects: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingNewsSubject(): MutableState<Boolean> { return procSubjects }

    private val procSubjectsInit: MutableState<Boolean> = mutableStateOf(true)
    fun refreshNewsSubjectsFromServer(page: Int = 1, force: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!procSubjectsInit.value && !force)
                    throw Exception("App not forced. Set force to true to load.")

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
                }
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                Log.d("NewsSubject", ex.message.toString())
            }
            procSubjects.value = false
            procSubjectsInit.value = false
        }
    }

    // Account Information
    private val accDataWithCache: MutableState<AccountCacheData> = mutableStateOf(
        AccountCacheData()
    )
    val accountData: MutableState<AccountCacheData>
        get() = accDataWithCache

    // Login/logout
    private val procAccount: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingAccount(): MutableState<Boolean> {
        return procAccount
    }
    fun login(user: String, pass: String, rememberLogin: Boolean = true) {
        viewModelScope.launch {
            try {
                procAccount.value = true

                val result = dutAccRepo.dutLogin(user, pass)
                if (result.loggedIn) {
                    accDataWithCache.value.SessionID.value = result.sessionId!!
                    appSettingsRepo.autoLogin = rememberLogin
                    appSettingsRepo.username = user
                    appSettingsRepo.password = pass

                    // Navigate to page logged in
                    accountPaneIndex.value = 2

                    getSubjectScheduleAndFee(21, 2, false)
                    getAccountInformation()
                }
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                Log.d("Login", ex.message.toString())
            }
            procAccount.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                procAccount.value = true

                appSettingsRepo.autoLogin = false
                appSettingsRepo.username = null
                appSettingsRepo.password = null

                // Navigate to page not logged in
                accountPaneIndex.value = 0

                // Logout
                val temp = accDataWithCache.value.SessionID.value
                accDataWithCache.value.clearAllData()
                dutAccRepo.dutLogout(temp)
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                Log.d("Logout", ex.message.toString())
            }
            procAccount.value = false
        }
    }

    fun isLoggedIn(): Boolean {
        return (accDataWithCache.value.isStoringSessionID())
    }

    // Get subject schedule and subject fee
    fun getSubjectScheduleAndFee(year: Int, semester: Int, inSummer: Boolean) {
        viewModelScope.launch {
            try {
                procAccount.value = true

                val dataSubjectScheduleFromInternet = dutAccRepo.dutGetSubjectSchedule(
                    accDataWithCache.value.SessionID.value, year, semester, inSummer)
                if (dataSubjectScheduleFromInternet.scheduleList != null &&
                        dataSubjectScheduleFromInternet.scheduleList.size > 0)
                    accDataWithCache.value.SubjectScheduleData.value = dataSubjectScheduleFromInternet.scheduleList

                val dataSubjectFeeFromInternet = dutAccRepo.dutGetSubjectFee(
                    accDataWithCache.value.SessionID.value, year, semester, inSummer)
                if (dataSubjectFeeFromInternet.feeList != null &&
                        dataSubjectFeeFromInternet.feeList.size > 0)
                    accDataWithCache.value.SubjectFeeData.value = dataSubjectFeeFromInternet.feeList
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                Log.d("SubjectScheduleFee", ex.message.toString())
            }
            procAccount.value = false
        }
    }

    // Get account information
    fun getAccountInformation() {
        viewModelScope.launch {
            try {
                procAccount.value = true

                val dataAccInfoFromInternet = dutAccRepo.dutGetAccInfo(
                    accDataWithCache.value.SessionID.value)
                if (dataAccInfoFromInternet.accountInfo != null)
                    accDataWithCache.value.AccountInformationData.value = dataAccInfoFromInternet.accountInfo
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                Log.d("AccInfo", ex.message.toString())
            }
            procAccount.value = false
        }
    }

    // Load settings from appSettings.json.
    private fun loadSettings() {
        viewModelScope.launch {
            // Load all old news
            newsCacheData.value.NewsGlobalData.value.addAll(newsCacheFileRepo.getNewsGlobal())
            newsCacheData.value.NewsSubjectData.value.addAll(newsCacheFileRepo.getNewsSubject())

            // Detect auto login
            if (appSettingsRepo.autoLogin) {
                Log.d("AutoLogin", "AutoLoginTriggered")
                if (appSettingsRepo.username != null && appSettingsRepo.password != null)
                    login(appSettingsRepo.username!!, appSettingsRepo.password!!)
            }
        }
    }

    init {
        // Load settings first before continue.
        loadSettings()
        // Load news cache for backup if internet is not available.
//        getNewsCacheFromDb()
        // Auto refresh news in server at startup.
        refreshNewsGlobalFromServer()
        refreshNewsSubjectsFromServer()
    }
}
