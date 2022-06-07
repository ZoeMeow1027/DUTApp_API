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
import io.zoemeow.dutapp.data.*
import io.zoemeow.dutapp.model.enums.ProcessResult
import io.zoemeow.dutapp.model.news.NewsGlobalItem
import io.zoemeow.dutapp.model.news.NewsGlobalListItem
import io.zoemeow.dutapp.model.news.NewsSubjectItem
import io.zoemeow.dutapp.model.news.NewsSubjectListItem
import io.zoemeow.dutapp.model.subject.SubjectScheduleItem
import io.zoemeow.dutapp.model.subject.SubjectSchoolYearSettings
import io.zoemeow.dutapp.repository.*
import io.zoemeow.dutapp.utils.getCurrentLesson
import io.zoemeow.dutapp.utils.getDayOfWeek
import io.zoemeow.dutapp.utils.getMD5FromString
import kotlinx.coroutines.launch
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
    internal val mainActivitySnackBarHostState: MutableState<SnackbarHostState?> = mutableStateOf(null)

    // Get context main activity
    internal val mainActivityContext: MutableState<Context?> = mutableStateOf(null)

    // News Details View when clicked a news.
    internal val newsDetailsClickedData: MutableState<NewsDetailsClickedData?> = mutableStateOf(null)

    // Initialize is processing data
    internal val isProcessingData: IsProcessingData = IsProcessingData()

    // News data with cache (for easier manage).
    internal val newsCacheData: MutableState<NewsCacheData> = mutableStateOf(NewsCacheData())

    // Get news global.
    // Check if is getting news global
    private val pageNewsGlobalCurrent = mutableStateOf(1)

    // Get news global from server
    fun getNewsGlobal(force: Boolean) {
        viewModelScope.launch {
            if (force) {
                pageNewsGlobalCurrent.value = 1
                isProcessingData["NewsGlobal"] = ProcessResult.Failed
            }

            if (
                if (isProcessingData["NewsGlobal"] != null)
                    isProcessingData["NewsGlobal"]!!.valueProcess.value == ProcessResult.Successful
                else false
            ) pageNewsGlobalCurrent.value += 1

            refreshNewsGlobalFromServer(
                pageNewsGlobalCurrent.value,
                !force
            )
        }
    }

    // Refresh news global
    private suspend fun refreshNewsGlobalFromServer(page: Int = 1, append: Boolean = false) {
        isProcessingData["NewsGlobal"] = ProcessResult.Running

        try {
            val dataGlobalFromInternet: NewsGlobalListItem = dutNewsRepo.getNewsGlobal(page)

            if ((dataGlobalFromInternet.news_list != null) && (dataGlobalFromInternet.news_list.size > 0)) {
                val list = ArrayList<NewsGlobalItem>()
                for (newsItem: NewsGlobalItem in dataGlobalFromInternet.news_list) {
                    list.add(
                        NewsGlobalItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            content = newsItem.content,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = getMD5FromString("${newsItem.date}-${newsItem.title}")
                        )
                    )
                }

                newsCacheFileRepo.setNewsGlobal(list, append = append)
                if (!append)
                    newsCacheData.value.newsGlobalData.value.clear()
                newsCacheData.value.newsGlobalData.value.addAll(list)
            } else throw Exception("News list empty.")

            // Return true
            isProcessingData["NewsGlobal"] = ProcessResult.Successful
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()

            // Return false
            isProcessingData["NewsGlobal"] = ProcessResult.Failed

            // Notify that can't load news here.
            mainActivitySnackBarHostState.value?.showSnackbar(
                mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
            )
        }
    }

    // Get news subjects
    // Check if is getting news subject
    private val pageNewSubjectCurrent = mutableStateOf(1)

    fun getNewsSubject(force: Boolean) {
        viewModelScope.launch {
            if (force) {
                pageNewSubjectCurrent.value = 1
                isProcessingData["NewsSubject"] = ProcessResult.Failed
            }

            if (
                if (isProcessingData["NewsSubject"] != null)
                    isProcessingData["NewsSubject"]!!.valueProcess.value == ProcessResult.Successful
                else false
            ) pageNewSubjectCurrent.value += 1

            refreshNewsSubjectsFromServer(
                pageNewSubjectCurrent.value,
                !force
            )
        }
    }

    // Refresh news subject
    private suspend fun refreshNewsSubjectsFromServer(page: Int = 1, append: Boolean = false) {
        isProcessingData["NewsSubject"] = ProcessResult.Running

        try {
            val dataSubjectsFromInternet: NewsSubjectListItem = dutNewsRepo.getNewsSubject(page)

            if ((dataSubjectsFromInternet.news_list != null) && (dataSubjectsFromInternet.news_list.size > 0)) {
                val list = ArrayList<NewsSubjectItem>()
                for (newsItem: NewsSubjectItem in dataSubjectsFromInternet.news_list) {
                    list.add(
                        NewsSubjectItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            content = newsItem.content,
                            links = ArrayList(newsItem.links ?: ArrayList()),
                            id = getMD5FromString("${newsItem.date}-${newsItem.title}")
                        )
                    )
                }

                newsCacheFileRepo.setNewsSubject(list, append = append)
                if (!append)
                    newsCacheData.value.newsSubjectData.value.clear()
                newsCacheData.value.newsSubjectData.value.addAll(list)
            } else throw Exception("News list empty.")

            // Return true
            isProcessingData["NewsSubject"] = ProcessResult.Successful
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()

            // Return false
            isProcessingData["NewsSubject"] = ProcessResult.Failed

            // Notify that can't load news here.
            mainActivitySnackBarHostState.value?.showSnackbar(
                mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
            )
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
            isProcessingData["LoggingIn"] = ProcessResult.Running

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
                    refreshSubjectSchedule()
                    refreshSubjectFee()
                    refreshAccountInfo()
                }
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
            }

            // All result will be returned to main page.
            accountPaneIndex.value = 0

            // If logged in (check session id is not empty)
            if (accCacheData.value.sessionID.value.isNotEmpty()) {
                isProcessingData["LoggingIn"] = ProcessResult.Successful
                // Navigate to page logged in
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_loginsuccessful)!!
                )
            }
            // If failed login at startup, will clear all auto login settings
            // and return back to login page
            else if (accLoginStartup.value) {
                isProcessingData["LoggingIn"] = ProcessResult.Failed
                accountPaneIndex.value = 0
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_autologinfailed)!!
                )
                accLoginStartup.value = false
            }
            // Any failed while logging in will be return to login/not logged in.
            else {
                isProcessingData["LoggingIn"] = ProcessResult.Failed
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

                // Logout
                viewModelScope.launch {
                    dutAccRepo.dutLogout(temp)
                }

                // Show snack bar logged out
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_loggedout)!!
                )
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

    // Get subject schedule and subject fee
    fun refreshSubjectSchedule() {
        viewModelScope.launch {
            try {
                isProcessingData["SubjectSchedule"] = ProcessResult.Running

                // Get subject schedule
                val dataSubjectScheduleFromInternet = dutAccRepo.dutGetSubjectSchedule(
                    accCacheData.value.sessionID.value,
                    appSettingsRepo.subjectSchoolYearSettings.subjectYear,
                    appSettingsRepo.subjectSchoolYearSettings.subjectSemester,
                    appSettingsRepo.subjectSchoolYearSettings.subjectInSummer
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

                isProcessingData["SubjectSchedule"] = ProcessResult.Successful
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
                isProcessingData["SubjectSchedule"] = ProcessResult.Failed
            }

            // TODO: Development for current day subjects here!
            getSubjectScheduleOnTodayAndTomorrow()
        }
    }

    fun refreshSubjectFee() {
        viewModelScope.launch {
            try {
                isProcessingData["SubjectFee"] = ProcessResult.Running

                // Get subject fee
                val dataSubjectFeeFromInternet = dutAccRepo.dutGetSubjectFee(
                    accCacheData.value.sessionID.value,
                    appSettingsRepo.subjectSchoolYearSettings.subjectYear,
                    appSettingsRepo.subjectSchoolYearSettings.subjectSemester,
                    appSettingsRepo.subjectSchoolYearSettings.subjectInSummer
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

                isProcessingData["SubjectFee"] = ProcessResult.Successful
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
                isProcessingData["SubjectFee"] = ProcessResult.Failed
            }
        }
    }

    internal val subjectScheduleToday: MutableState<ArrayList<SubjectScheduleItem>> = mutableStateOf(
        ArrayList()
    )

    internal val subjectScheduleTomorrow: MutableState<ArrayList<SubjectScheduleItem>> = mutableStateOf(
        ArrayList()
    )

    private fun getSubjectScheduleInDayOfWeek(
        plusDayOfWeek: Int = 0,
        plusLesson: Int = 0,
    ): ArrayList<SubjectScheduleItem> {
        // Set lesson first
        var lesson = getCurrentLesson()
        lesson += if (plusLesson > 0) plusLesson else 0

        // Set day of week
        // 0: Sunday to 6: Saturday
        // If plusDayOfWeek > 0, plusLesson will be 0
        var dayOfWeek = getDayOfWeek()
        if (plusDayOfWeek > 0) {
            dayOfWeek += plusDayOfWeek
            // If dayOfWeek > 6, will return to new week
            if (dayOfWeek > 6) dayOfWeek %= 7
            // Set lesson to 0
            lesson = 0
        }

        // Initialize a temporary array list for subject schedule item
        val result = ArrayList<SubjectScheduleItem>()

        try {
            result.addAll(
                accCacheData.value.subjectScheduleData
                        // Filter day of week
                    .filter { it.schedule_study!!.schedule!!.any { dayOfWeekGet -> dayOfWeekGet.day_of_week == dayOfWeek } }
                        // Filter lesson
                    .filter { it.schedule_study!!.schedule!!.any { lessonGet -> lessonGet.lesson!!.end!! >= lesson } }
                        // Sort subjects by lesson start
                    .sortedBy {
                        it.schedule_study?.schedule?.sortedBy {
                                it2 -> it2.lesson?.start
                        }?.get(0)?.lesson?.start
                    }
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        // Return result
        return result
    }

    fun getSubjectScheduleOnTodayAndTomorrow() {
        // Current subject schedule today
        try {
            subjectScheduleToday.value.clear()
            subjectScheduleToday.value.addAll(
                getSubjectScheduleInDayOfWeek(0, 0)
            )
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()
        }

        // Current subject schedule tomorrow
        try {
            subjectScheduleTomorrow.value.clear()
            subjectScheduleTomorrow.value.addAll(
                getSubjectScheduleInDayOfWeek(1, 0)
            )
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()
        }
    }

    fun reloadViewSubjectScheduleOnDay() {
        viewModelScope.launch {
            refreshNewsGlobalFromServer()
            refreshNewsSubjectsFromServer()
        }
        if (accCacheData.value.sessionID.value.isNotEmpty()) {
            refreshSubjectSchedule()
            refreshSubjectFee()
        }
    }

    // Get account information
    private fun refreshAccountInfo() {
        viewModelScope.launch {
            try {
                isProcessingData["AccInfo"] = ProcessResult.Running

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

                isProcessingData["AccInfo"] = ProcessResult.Successful
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
                isProcessingData["AccInfo"] = ProcessResult.Failed
            }
        }
    }

    fun openLinkInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        mainActivityContext.value?.startActivity(intent)
    }

    var subjectSchoolYearSettings: SubjectSchoolYearSettings
        get() = appSettingsRepo.subjectSchoolYearSettings
        set(value) {
            appSettingsRepo.subjectSchoolYearSettings = value
        }

    // Load news cache for backup if internet is not available.
    private fun loadAppCache() {
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

    // Load settings from appSettings.json, if needed.
    private fun loadSettings() {

    }

    init {
        // Load settings first before continue.
        loadSettings()

        // Load news cache for backup if internet is not available.
        loadAppCache()
        getSubjectScheduleOnTodayAndTomorrow()

        // Auto refresh news in server at startup.
        // refreshNewsGlobalFromServer()
        getNewsGlobal(true)
        // refreshNewsSubjectsFromServer()
        getNewsSubject(true)

        // Detect auto login (login if user checked auto login check box)
        executeAutoLogin()
    }
}
