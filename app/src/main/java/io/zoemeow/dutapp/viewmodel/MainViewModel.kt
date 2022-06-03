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
import io.zoemeow.dutapp.model.account.AutoLoginSettings
import io.zoemeow.dutapp.model.news.NewsGlobalItem
import io.zoemeow.dutapp.model.news.NewsGlobalListItem
import io.zoemeow.dutapp.model.news.NewsSubjectItem
import io.zoemeow.dutapp.model.news.NewsSubjectListItem
import io.zoemeow.dutapp.model.subject.SubjectSchoolYearSettings
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
    private val accCacheFileRepo: SubjectCacheFileRepository,
) : ViewModel() {
    // Exception will be saved here.
    private val exceptionCacheData: MutableState<ExceptionCacheData> = mutableStateOf(ExceptionCacheData())

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

    // News data with cache (for easier manage).
    internal val newsCacheData: MutableState<NewsCacheData> = mutableStateOf(NewsCacheData())

    // Generate md5 from string.
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    // Get news global.
    // Check if is getting news global
    internal val isProcessingGlobal: MutableState<Boolean> = mutableStateOf(false)

    // Refresh news global
    fun refreshNewsGlobalFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
                isProcessingGlobal.value = true
                val dataGlobalFromInternet: NewsGlobalListItem = dutNewsRepo.getNewsGlobal(page)

                if (dataGlobalFromInternet.news_list != null) {
                    newsCacheData.value.newsGlobalData.value.clear()
                    newsCacheFileRepo.deleteAllNewsGlobal()

                    val list = ArrayList<NewsGlobalItem>()
                    for (newsItem: NewsGlobalItem in dataGlobalFromInternet.news_list) {
                        val value = NewsGlobalItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            content = newsItem.content,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = md5("${newsItem.date}-${newsItem.title}")
                        )
                        list.add(value)
                    }

                    newsCacheFileRepo.setNewsGlobal(list)
                    newsCacheData.value.newsGlobalData.value.addAll(list)
                } else throw Exception("News list empty.")
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)

                // Notify that can't load news here.
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
                )
            }
            isProcessingGlobal.value = false
        }
    }

    // Get news subjects
    // Check if is getting news subject
    internal val isProcessingSubject: MutableState<Boolean> = mutableStateOf(false)

    fun refreshNewsSubjectsFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
                isProcessingSubject.value = true
                val dataSubjectsFromInternet: NewsSubjectListItem = dutNewsRepo.getNewsSubject(page)
                if (dataSubjectsFromInternet.news_list != null) {
                    newsCacheData.value.newsSubjectData.value.clear()
                    newsCacheFileRepo.deleteAllNewsSubject()

                    val list = ArrayList<NewsSubjectItem>()
                    for (newsItem: NewsSubjectItem in dataSubjectsFromInternet.news_list) {
                        list.add(
                            NewsSubjectItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            content = newsItem.content,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = md5("${newsItem.date}-${newsItem.title}")
                        )
                        )
                    }

                    newsCacheFileRepo.setNewsSubject(list)
                    newsCacheData.value.newsSubjectData.value.addAll(list)
                } else throw Exception("News list empty.")
            }
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)

                // Notify that can't load news here.
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
                )
            }
            isProcessingSubject.value = false
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

    // Check if login is in progress
    internal val isProcessingLogin = mutableStateOf(false)

    // Log in using your account
    fun login(user: String, pass: String, rememberLogin: Boolean = true) {
        viewModelScope.launch {
            // Navigate to page logging in
            accountPaneIndex.value = 2
            isProcessingLogin.value = true

            try {
                // Login
                val result = dutAccRepo.dutLogin(user, pass)

                // If login successfully
                if (result.logged_in) {
                    // Save session id to cache
                    accCacheData.value.sessionID.value = result.session_id!!
                    Log.d("CheckLogin", "Logged in")
                    // Only logged in will can remember login
                    if (rememberLogin) {
                        // Save to app settings
                        appSettingsRepo.autoLogin = rememberLogin
                        appSettingsRepo.username = user
                        appSettingsRepo.password = pass
                    }

                    // Pre-load subject schedule, fee and account information
                    refreshSubjectScheduleAndFee()
                    refreshAccountInformation()
                }
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
            }

            // All result will be returned to main page.
            accountPaneIndex.value = 0
            isProcessingLogin.value = false

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
            }
        }
    }

    internal fun isAvailableOffline(): Boolean {
        return appSettingsRepo.autoLogin &&
                accCacheFileRepo.getAccountInformation().studentId != null &&
                accCacheFileRepo.getSubjectSchedule().size != 0 &&
                accCacheFileRepo.getSubjectFee().size != 0
    }

    // Clear auto login settings
    private fun clearAutoLogin() {
        appSettingsRepo.autoLogin = false
        appSettingsRepo.username = null
        appSettingsRepo.password = null
        accCacheFileRepo.deleteAllSubjectSchedule()
        accCacheFileRepo.deleteAllSubjectFee()
        accCacheFileRepo.deleteAccountInformation()
    }

    internal var isProcessingSubjectScheduleFee = mutableStateOf(false)

    // Get subject schedule and subject fee
    fun refreshSubjectScheduleAndFee() {
        viewModelScope.launch {
            try {
                isProcessingSubjectScheduleFee.value = true

                // Get subject schedule
                val dataSubjectScheduleFromInternet = dutAccRepo.dutGetSubjectSchedule(
                    accCacheData.value.sessionID.value,
                    appSettingsRepo.subjectYear,
                    appSettingsRepo.subjectSemester,
                    appSettingsRepo.subjectInSummer
                )

                if (dataSubjectScheduleFromInternet.schedule_list != null &&
                        dataSubjectScheduleFromInternet.schedule_list.size > 0) {
                    // Add to cache
                    accCacheData.value.subjectScheduleData.value = dataSubjectScheduleFromInternet.schedule_list
                    // Write to json
                    accCacheFileRepo.setSubjectSchedule(dataSubjectScheduleFromInternet.schedule_list)
                    accCacheFileRepo.subjectScheduleUpdateTime = dataSubjectScheduleFromInternet.date!!
                }

                // Get subject fee
                val dataSubjectFeeFromInternet = dutAccRepo.dutGetSubjectFee(
                    accCacheData.value.sessionID.value,
                    appSettingsRepo.subjectYear,
                    appSettingsRepo.subjectSemester,
                    appSettingsRepo.subjectInSummer
                )
                if (dataSubjectFeeFromInternet.fee_list != null &&
                        dataSubjectFeeFromInternet.fee_list.size > 0) {
                    // Add to cache
                    accCacheData.value.subjectFeeData.value = dataSubjectFeeFromInternet.fee_list
                    // Write to json
                    accCacheFileRepo.setSubjectFee(dataSubjectFeeFromInternet.fee_list)
                    accCacheFileRepo.subjectFeeUpdateTime = dataSubjectFeeFromInternet.date!!
                }
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
            }

            isProcessingSubjectScheduleFee.value = false
        }
    }

    internal val isProcessingAccountInfo = mutableStateOf(false)

    // Get account information
    private fun refreshAccountInformation() {
        viewModelScope.launch {
            try {
                isProcessingAccountInfo.value = true

                // Get account information
                val dataAccInfoFromInternet = dutAccRepo.dutGetAccInfo(
                    accCacheData.value.sessionID.value)
                if (dataAccInfoFromInternet.account_info != null) {
                    // Add to cache
                    accCacheData.value.accountInformationData.value = dataAccInfoFromInternet.account_info
                    // Write to json
                    accCacheFileRepo.setAccountInformation(dataAccInfoFromInternet.account_info)
                    accCacheFileRepo.accountInformationUpdateTime = dataAccInfoFromInternet.date!!
                }
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
            }

            isProcessingAccountInfo.value = false
        }
    }

    fun getSubjectSchoolYearSettings(): SubjectSchoolYearSettings {
        return SubjectSchoolYearSettings(
            appSettingsRepo.subjectDefault,
            appSettingsRepo.subjectYear,
            appSettingsRepo.subjectSemester,
            appSettingsRepo.subjectInSummer
        )
    }

    fun setSubjectSchoolYearSettings(item: SubjectSchoolYearSettings) {
        appSettingsRepo.subjectDefault = item.subjectDefault
        appSettingsRepo.subjectYear = item.subjectYear
        appSettingsRepo.subjectSemester = item.subjectSemester
        appSettingsRepo.subjectInSummer = item.subjectInSummer
    }

    // Load news cache for backup if internet is not available.
    private fun loadCache() {
        newsCacheData.value.newsGlobalData.value.addAll(newsCacheFileRepo.getNewsGlobal())
        newsCacheData.value.newsSubjectData.value.addAll(newsCacheFileRepo.getNewsSubject())
        accCacheData.value.accountInformationData.value = accCacheFileRepo.getAccountInformation()
        accCacheData.value.subjectScheduleData.value.clear()
        accCacheData.value.subjectScheduleData.value.addAll(accCacheFileRepo.getSubjectSchedule())
        accCacheData.value.subjectFeeData.value.clear()
        accCacheData.value.subjectFeeData.value.addAll(accCacheFileRepo.getSubjectFee())
    }

    // Detect auto login (login if user checked auto login check box)
    fun executeAutoLogin() {
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
        // Load settings first before continue.
        loadSettings()

        // Load news cache for backup if internet is not available.
        loadCache()

        // Auto refresh news in server at startup.
        refreshNewsGlobalFromServer()
        refreshNewsSubjectsFromServer()

        // Detect auto login (login if user checked auto login check box)
        executeAutoLogin()
    }
}
