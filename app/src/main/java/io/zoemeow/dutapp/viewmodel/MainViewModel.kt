package io.zoemeow.dutapp.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.data.*
import io.zoemeow.dutapp.model.*
import io.zoemeow.dutapp.data.repository.DutAccountRepository
import io.zoemeow.dutapp.data.repository.DutNewsRepository
import io.zoemeow.dutapp.data.repository.NewsCacheRepository
import io.zoemeow.dutapp.utils.AppSettingsFun
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dutNewsRepo: DutNewsRepository,
    private val dutAccRepo: DutAccountRepository,
    private val dutNewsCacheDbRepo: NewsCacheRepository,
) : ViewModel() {
    // Exception will be saved here.
    private val exceptionWithCache: MutableState<ExceptionWithCache> = mutableStateOf(ExceptionWithCache())

    // News Details View when clicked a news.
    val newsDetailsClicked: MutableState<NewsDetailsClicked?> = mutableStateOf(null)

    // Account View.
    // 0: Not logged in, 1: Login, 2: Logged In
    val accountPaneIndex = mutableStateOf(0)

    // News data with cache (for easier manage).
    private val newsDataWithCache: MutableState<NewsDataWithCache> = mutableStateOf(NewsDataWithCache())
    val newsData: MutableState<NewsDataWithCache>
        get() = newsDataWithCache

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    // Get news global.
    private val procGlobal: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingNewsGlobal(): MutableState<Boolean> { return procGlobal }
    private val procGlobalInit: MutableState<Boolean> = mutableStateOf(true)
    fun refreshNewsGlobalFromServer(page: Int = 1, force: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!procGlobalInit.value && !force)
                    throw Exception("App not forced. Set force to true to load.")

                procGlobal.value = true
                val dataGlobalFromInternet: NewsGlobalListItem = dutNewsRepo.getNewsGlobal(page)

                if (dataGlobalFromInternet.newslist != null) {
                    newsDataWithCache.value.NewsGlobalData.value.clear()
                    dutNewsCacheDbRepo.deleteAllNewsGlobal()

                    val list = ArrayList<NewsGlobalItem>()
                    for (newsItem: NewsGlobalItem in dataGlobalFromInternet.newslist) {
                        val value = NewsGlobalItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            contentText = newsItem.contentText,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = md5("${newsItem.date}-${newsItem.title}")
                        )
                        list.add(value)
                    }

                    dutNewsCacheDbRepo.insertNewsGlobal(list)
                    newsDataWithCache.value.NewsGlobalData.value.addAll(list)
                }
            }
            catch (ex: Exception) {
                exceptionWithCache.value.addException(ex)
                Log.d("NewsGlobal", ex.message.toString())
            }
            procGlobal.value = false
            procGlobalInit.value = false
        }
    }

    // Get news subjects
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
                if (dataSubjectsFromInternet.newslist != null) {
                    newsDataWithCache.value.NewsSubjectData.value.clear()
                    dutNewsCacheDbRepo.deleteAllNewsSubject()

                    val list = ArrayList<NewsSubjectItem>()
                    for (newsItem: NewsSubjectItem in dataSubjectsFromInternet.newslist) {
                        list.add(NewsSubjectItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            contentText = newsItem.contentText,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = md5("${newsItem.date}-${newsItem.title}")
                        ))
                    }

                    dutNewsCacheDbRepo.insertNewsSubject(list)
                    newsDataWithCache.value.NewsSubjectData.value.addAll(list)
                }
            }
            catch (ex: Exception) {
                exceptionWithCache.value.addException(ex)
                Log.d("NewsSubject", ex.message.toString())
            }
            procSubjects.value = false
            procSubjectsInit.value = false
        }
    }

    // Get news cache from db.
    private fun getNewsCacheFromDb() {
        viewModelScope.launch {
            dutNewsCacheDbRepo.getAllNewsGlobal().collect {
                    list ->
                newsDataWithCache.value.NewsGlobalData.value.addAll(list)
            }
            dutNewsCacheDbRepo.getAllNewsSubject().collect {
                    list ->
                newsDataWithCache.value.NewsSubjectData.value.addAll(list)
            }
        }
    }

    // Account Information
    private val accDataWithCache: MutableState<AccountDataWithCache> = mutableStateOf(
        AccountDataWithCache()
    )
    val accountData: MutableState<AccountDataWithCache>
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
                if (result.loggedin) {
                    accDataWithCache.value.SessionID.value = result.sessionid!!
                    appSettings.autoLogin = rememberLogin
                    appSettings.username = user
                    appSettings.password = pass

                    // Navigate to page logged in
                    accountPaneIndex.value = 2

                    getSubjectScheduleAndFee(21, 2, false)
                    getAccountInformation()
                }
            }
            catch (ex: Exception) {
                exceptionWithCache.value.addException(ex)
                Log.d("Login", ex.message.toString())
            }
            procAccount.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                procAccount.value = true

                val temp = accDataWithCache.value.SessionID.value
                accDataWithCache.value.clearAllData()
                dutAccRepo.dutLogout(temp)

                appSettings.autoLogin = false
                appSettings.username = null
                appSettings.password = null

                // Navigate to page not logged in
                accountPaneIndex.value = 0
            }
            catch (ex: Exception) {
                exceptionWithCache.value.addException(ex)
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
                if (dataSubjectScheduleFromInternet.schedulelist != null &&
                        dataSubjectScheduleFromInternet.schedulelist.size > 0)
                    accDataWithCache.value.SubjectScheduleData.value = dataSubjectScheduleFromInternet.schedulelist

                val dataSubjectFeeFromInternet = dutAccRepo.dutGetSubjectFee(
                    accDataWithCache.value.SessionID.value, year, semester, inSummer)
                if (dataSubjectFeeFromInternet.feelist != null &&
                        dataSubjectFeeFromInternet.feelist.size > 0)
                    accDataWithCache.value.SubjectFeeData.value = dataSubjectFeeFromInternet.feelist
            }
            catch (ex: Exception) {
                exceptionWithCache.value.addException(ex)
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
                if (dataAccInfoFromInternet.accountinfo != null)
                    accDataWithCache.value.AccountInformationData.value = dataAccInfoFromInternet.accountinfo
            }
            catch (ex: Exception) {
                exceptionWithCache.value.addException(ex)
                Log.d("AccInfo", ex.message.toString())
            }
            procAccount.value = false
        }
    }

    private val appSettings = AppSettingsFun()

    private fun loadSettings() {
        viewModelScope.launch {
            appSettings.importSettings()
            if (appSettings.autoLogin) {
                Log.d("AutoLogin", "AutoLoginTriggered")
                if (appSettings.username != null && appSettings.password != null)
                    login(appSettings.username!!, appSettings.password!!)
            }
        }
    }

    init {
        loadSettings()
        getNewsCacheFromDb()

        refreshNewsGlobalFromServer()
        refreshNewsSubjectsFromServer()
    }
}
