package io.zoemeow.dutapp.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.R
import io.zoemeow.dutapp.data.AccountCacheData
import io.zoemeow.dutapp.data.ExceptionCacheData
import io.zoemeow.dutapp.data.NewsCacheData
import io.zoemeow.dutapp.data.NewsDetailsClickedData
import io.zoemeow.dutapp.model.news.NewsGlobalItem
import io.zoemeow.dutapp.model.news.NewsGlobalListItem
import io.zoemeow.dutapp.model.news.NewsSubjectItem
import io.zoemeow.dutapp.model.news.NewsSubjectListItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem
import io.zoemeow.dutapp.model.subject.SubjectSchoolYearSettings
import io.zoemeow.dutapp.repository.*
import io.zoemeow.dutapp.utils.getCurrentLesson
import io.zoemeow.dutapp.utils.getDayOfWeek
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

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
    internal val newsDetailsClickedData: MutableState<NewsDetailsClickedData?> = mutableStateOf(null)
    fun setNewsDetailClicked(item: NewsDetailsClickedData) {
        newsDetailsClickedData.value = item
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
    private val pageNewsGlobalCurrent = mutableStateOf(1)
    private val pageNewsGlobalPreviousResult = mutableStateOf(false)

    // Get news global from server
    fun getNewsGlobal(force: Boolean) {
        viewModelScope.launch {
            if (force) {
                pageNewsGlobalCurrent.value = 1
                pageNewsGlobalPreviousResult.value = false
                newsCacheFileRepo.deleteAllNewsGlobal()
            }

            if (pageNewsGlobalPreviousResult.value)
                pageNewsGlobalCurrent.value += 1

            pageNewsGlobalPreviousResult.value = refreshNewsGlobalFromServer(
                pageNewsGlobalCurrent.value,
                !force
            )
        }
    }

    // Refresh news global
    private suspend fun refreshNewsGlobalFromServer(page: Int = 1, append: Boolean = false): Boolean {
        var result = false
        isProcessingGlobal.value = true

        try {
            val dataGlobalFromInternet: NewsGlobalListItem = dutNewsRepo.getNewsGlobal(page)

            if (dataGlobalFromInternet.news_list != null) {
                val list = ArrayList<NewsGlobalItem>()
                for (newsItem: NewsGlobalItem in dataGlobalFromInternet.news_list) {
                    list.add(
                        NewsGlobalItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            content = newsItem.content,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = md5("${newsItem.date}-${newsItem.title}")
                        )
                    )
                }

                newsCacheFileRepo.setNewsGlobal(list, append = append)
                if (!append)
                    newsCacheData.value.newsGlobalData.value.clear()
                newsCacheData.value.newsGlobalData.value.addAll(list)
            } else throw Exception("News list empty.")

            // Return true
            result = true
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)

            // Notify that can't load news here.
            mainActivitySnackBarHostState.value?.showSnackbar(
                mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
            )
        }
        isProcessingGlobal.value = false
        return result
    }

    // Get news subjects
    // Check if is getting news subject
    internal val isProcessingSubject: MutableState<Boolean> = mutableStateOf(false)
    private val pageNewSubjectCurrent = mutableStateOf(1)
    private val pageNewsSubjectPreviousResult = mutableStateOf(false)

    fun getNewsSubject(force: Boolean) {
        viewModelScope.launch {
            if (force) {
                pageNewSubjectCurrent.value = 1
                pageNewsSubjectPreviousResult.value = false
                newsCacheFileRepo.deleteAllNewsSubject()
            }

            if (pageNewsSubjectPreviousResult.value)
                pageNewSubjectCurrent.value += 1

            pageNewsSubjectPreviousResult.value = refreshNewsSubjectsFromServer(
                pageNewSubjectCurrent.value,
                !force
            )
        }
    }

    // Refresh news subject
    private suspend fun refreshNewsSubjectsFromServer(page: Int = 1, append: Boolean = false): Boolean {
        var result = false
        isProcessingSubject.value = true

        try {
            val dataSubjectsFromInternet: NewsSubjectListItem = dutNewsRepo.getNewsSubject(page)

            if (dataSubjectsFromInternet.news_list != null) {
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

                newsCacheFileRepo.setNewsSubject(list, append = append)
                if (!append)
                    newsCacheData.value.newsSubjectData.value.clear()
                newsCacheData.value.newsSubjectData.value.addAll(list)
            } else throw Exception("News list empty.")

            // Return true
            result = true
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)

            // Notify that can't load news here.
            mainActivitySnackBarHostState.value?.showSnackbar(
                mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
            )
        }

        isProcessingSubject.value = false
        return result
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
                    refreshAccountInfo()
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
                    accCacheData.value.subjectScheduleData = dataSubjectScheduleFromInternet.schedule_list
                    accCacheData.value.subjectCredit = dataSubjectScheduleFromInternet.total_credit!!

                    // Write to json
                    accCacheFileRepo.setSubjectSchedule(dataSubjectScheduleFromInternet.schedule_list)
                    accCacheFileRepo.subjectScheduleUpdateTime = dataSubjectScheduleFromInternet.date!!
                    accCacheFileRepo.setSubjectCreditTotal(dataSubjectScheduleFromInternet.total_credit)
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
                    accCacheData.value.subjectFeeData = dataSubjectFeeFromInternet.fee_list
                    accCacheData.value.subjectCredit = dataSubjectFeeFromInternet.total_credit!!
                    accCacheData.value.subjectMoney = dataSubjectFeeFromInternet.total_money!!

                    // Write to json
                    accCacheFileRepo.setSubjectFee(dataSubjectFeeFromInternet.fee_list)
                    accCacheFileRepo.subjectFeeUpdateTime = dataSubjectFeeFromInternet.date!!
                    accCacheFileRepo.setSubjectCreditTotal(dataSubjectFeeFromInternet.total_credit)
                    accCacheFileRepo.setSubjectMoneyTotal(dataSubjectFeeFromInternet.total_money)
                }
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
            }

            // TODO: Development for current day subjects here!
            getCurrentSubjectScheduleOnDay()

            isProcessingSubjectScheduleFee.value = false
        }
    }

    internal val subjectScheduleDayOfWeek: MutableState<ArrayList<SubjectScheduleItem>> = mutableStateOf(
        ArrayList()
    )

    fun getCurrentSubjectScheduleOnDay() {
        // val day = getDayOfWeek()
        val day = getDayOfWeek()
        val lesson = getCurrentLesson()
        Log.d("io.zoemeow.dutapp", "DayOfWeek: $day")

        try {
            subjectScheduleDayOfWeek.value.clear()
            subjectScheduleDayOfWeek.value.addAll(
                accCacheData.value.subjectScheduleData
                    .filter {
                        it.schedule_study!!.schedule!!.any { dayOfWeekGet -> dayOfWeekGet.day_of_week == day }
                    }
                    .filter {
                        it.schedule_study!!.schedule!!.any { lessonGet -> lessonGet.lesson!!.end!! >= lesson }
                    }
            )
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    internal val isProcessingAccountInfo = mutableStateOf(false)

    // Get account information
    private fun refreshAccountInfo() {
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

    fun openLinkInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        mainActivityContext.value?.startActivity(intent)
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
        accCacheData.value.subjectScheduleData.clear()
        accCacheData.value.subjectScheduleData.addAll(accCacheFileRepo.getSubjectSchedule())
        accCacheData.value.subjectFeeData.clear()
        accCacheData.value.subjectFeeData.addAll(accCacheFileRepo.getSubjectFee())
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
        getCurrentSubjectScheduleOnDay()

        // Auto refresh news in server at startup.
        // refreshNewsGlobalFromServer()
        getNewsGlobal(true)
        // refreshNewsSubjectsFromServer()
        getNewsSubject(true)

        // Detect auto login (login if user checked auto login check box)
        executeAutoLogin()
    }
}
