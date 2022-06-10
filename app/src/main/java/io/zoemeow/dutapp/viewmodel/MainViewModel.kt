package io.zoemeow.dutapp.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
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
    internal val variableData: VariableData = VariableData()

    // News data with cache (for easier manage).
    internal val newsCacheData: MutableState<NewsCacheData> = mutableStateOf(NewsCacheData())

    // Get news global from server
    fun getNewsGlobal(force: Boolean) {
        viewModelScope.launch {
            if (force) {
                variableData["NewsGlobalPage"] = 1
                variableData.set("NewsGlobal", ProcessResult.Failed)
            }

            if (
                if (variableData.get<ProcessResult>("NewsGlobal") != null)
                    variableData.get<ProcessResult>("NewsGlobal")!!.value.value == ProcessResult.Successful
                else false
            ) variableData["NewsGlobalPage"] = variableData.get<Int>("NewsGlobalPage")!!.value.value!!.plus(1)

            refreshNewsGlobalFromServer(
                variableData.get<Int>("NewsGlobalPage")!!.value.value!!,
                !force
            )
        }
    }

    // Refresh news global
    private suspend fun refreshNewsGlobalFromServer(page: Int = 1, append: Boolean = false) {
        variableData.set("NewsGlobal", ProcessResult.Running)

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
            variableData.set("NewsGlobal", ProcessResult.Successful)
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()

            // Return false
            variableData.set("NewsGlobal", ProcessResult.Failed)

            // Notify that can't load news here.
            mainActivitySnackBarHostState.value?.showSnackbar(
                mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
            )
        }
    }

    fun getNewsSubject(force: Boolean) {
        viewModelScope.launch {
            if (force) {
                variableData["NewsSubjectPage"] = 1
                variableData.set("NewsSubject", ProcessResult.Failed)
            }

            if (
                if (variableData.get<ProcessResult>("NewsSubject") != null)
                    variableData.get<ProcessResult>("NewsSubject")!!.value.value == ProcessResult.Successful
                else false
            ) variableData["NewsSubjectPage"] = variableData.get<Int>("NewsSubjectPage")!!.value.value!!.plus(1)

            refreshNewsSubjectsFromServer(
                variableData.get<Int>("NewsSubjectPage")!!.value.value!!,
                !force
            )
        }
    }

    // Refresh news subject
    private suspend fun refreshNewsSubjectsFromServer(page: Int = 1, append: Boolean = false) {
        variableData.set("NewsSubject", ProcessResult.Running)

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
            variableData.set("NewsSubject", ProcessResult.Successful)
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()

            // Return false
            variableData.set("NewsSubject", ProcessResult.Failed)

            // Notify that can't load news here.
            mainActivitySnackBarHostState.value?.showSnackbar(
                mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
            )
        }
    }

    // Settings Information
    internal val accCacheData: MutableState<AccountCacheData> = mutableStateOf(
        AccountCacheData()
    )

    // Log in using your account
    fun login(user: String, pass: String, rememberLogin: Boolean = true) {
        viewModelScope.launch {
            // Navigate to page logging in
            variableData["SettingsPanelIndex"] = 2
            variableData.set("LoggingIn", ProcessResult.Running)

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
                }
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
            }

            // All result will be returned to main page.
            variableData["SettingsPanelIndex"] = 0

            // If logged in (check session id is not empty)
            if (accCacheData.value.sessionID.value.isNotEmpty()) {
                variableData.set("LoggingIn", ProcessResult.Successful)
                // Pre-load subject schedule, fee and account information
                refreshSubjectSchedule()
                refreshSubjectFee()
                refreshAccountInfo()
                // Navigate to page logged in
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_loginsuccessful)!!
                )
            }
            // If failed login at startup, will clear all auto login settings
            // and return back to login page
            else if (variableData.get<Boolean>("AccLoginStartup")!!.value.value!!) {
                variableData.set("LoggingIn", ProcessResult.Failed)
                variableData["SettingsPanelIndex"] = 0
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_autologinfailed)!!
                )
                variableData["AccLoginStartup"] = false
            }
            // Any failed while logging in will be return to login/not logged in.
            else {
                variableData.set("LoggingIn", ProcessResult.Failed)
                variableData["SettingsPanelIndex"] = 1
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
                variableData["SettingsPanelIndex"] = 0

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
                variableData["SubjectSchedule"] = ProcessResult.Running

                // Get subject schedule
                val dataSubjectScheduleFromInternet = dutAccRepo.dutGetSubjectSchedule(
                    accCacheData.value.sessionID.value,
                    appSettingsRepo.subjectSchoolYearSettings.subjectYear,
                    appSettingsRepo.subjectSchoolYearSettings.subjectSemester,
                    appSettingsRepo.subjectSchoolYearSettings.subjectInSummer
                )

                if (dataSubjectScheduleFromInternet.schedule_list != null &&
                        dataSubjectScheduleFromInternet.schedule_list.size > 0) {
                    // Write to json
                    accCacheFileRepo.setSubjectSchedule(dataSubjectScheduleFromInternet.schedule_list)
                    accCacheFileRepo.subjectScheduleUpdateTime = dataSubjectScheduleFromInternet.date!!
                    accCacheFileRepo.setSubjectCreditTotal(dataSubjectScheduleFromInternet.total_credit!!)

                    // Add to cache
                    accCacheData.value.subjectScheduleData.clear()
                    accCacheData.value.subjectScheduleData.addAll(dataSubjectScheduleFromInternet.schedule_list)
                    accCacheData.value.subjectCredit = dataSubjectScheduleFromInternet.total_credit
                }

                variableData["SubjectSchedule"] = ProcessResult.Successful
                accCacheData.value.subjectGetTime = dataSubjectScheduleFromInternet.date!!
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
                variableData["SubjectSchedule"] = ProcessResult.Failed
            }

            // TODO: Development for current day subjects here!
            getSubjectScheduleOnTodayAndTomorrow()
            getSubjectExaminationOnDays()
        }
    }

    fun refreshSubjectFee() {
        viewModelScope.launch {
            try {
                variableData.set("SubjectFee", ProcessResult.Running)

                // Get subject fee
                val dataSubjectFeeFromInternet = dutAccRepo.dutGetSubjectFee(
                    accCacheData.value.sessionID.value,
                    appSettingsRepo.subjectSchoolYearSettings.subjectYear,
                    appSettingsRepo.subjectSchoolYearSettings.subjectSemester,
                    appSettingsRepo.subjectSchoolYearSettings.subjectInSummer
                )
                if (dataSubjectFeeFromInternet.fee_list != null &&
                    dataSubjectFeeFromInternet.fee_list.size > 0) {
                    // Write to json
                    accCacheFileRepo.setSubjectFee(dataSubjectFeeFromInternet.fee_list)
                    accCacheFileRepo.subjectFeeUpdateTime = dataSubjectFeeFromInternet.date!!
                    accCacheFileRepo.setSubjectCreditTotal(dataSubjectFeeFromInternet.total_credit!!)
                    accCacheFileRepo.setSubjectMoneyTotal(dataSubjectFeeFromInternet.total_money!!)

                    // Add to cache
                    accCacheData.value.subjectFeeData.clear()
                    accCacheData.value.subjectFeeData.addAll(dataSubjectFeeFromInternet.fee_list)
                    accCacheData.value.subjectCredit = dataSubjectFeeFromInternet.total_credit
                    accCacheData.value.subjectMoney = dataSubjectFeeFromInternet.total_money
                }
                accCacheData.value.subjectGetTime = dataSubjectFeeFromInternet.date!!
                variableData["SubjectFee"] = ProcessResult.Successful
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
                variableData.set("SubjectFee", ProcessResult.Failed)
            }
        }
    }

    internal val subjectScheduleToday = mutableStateListOf<SubjectScheduleItem>()

    internal val subjectScheduleTomorrow = mutableStateListOf<SubjectScheduleItem>()

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
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()
        }

        // Return result
        return result
    }

    internal val subjectExam7Days = mutableStateListOf<SubjectScheduleItem>()

    fun getSubjectExaminationOnDays(dayNext: Int = 7, hideAfterMin: Int = 60) {
        try {
            val timeUnix = System.currentTimeMillis()
            subjectExam7Days.clear()
            subjectExam7Days.addAll(
                accCacheData.value.subjectScheduleData
                    // Filter time between 7 days
                    .filter {
                        (it.schedule_exam!!.date - timeUnix > 0) &&
                                (it.schedule_exam.date - timeUnix < (1000*60*60*24*dayNext + 1000*60*hideAfterMin))
                    }
                    // Sort subjects by lesson start
                    .sortedBy {
                        it.schedule_exam?.date
                    }
            )
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()
        }
    }

    fun getSubjectScheduleOnTodayAndTomorrow() {
        // Current subject schedule today
        try {
            subjectScheduleToday.clear()
            subjectScheduleToday.addAll(
                getSubjectScheduleInDayOfWeek(0, 0)
            )
        }
        catch (ex: Exception) {
            exceptionCacheData.value.addException(ex)
            ex.printStackTrace()
        }

        // Current subject schedule tomorrow
        try {
            subjectScheduleTomorrow.clear()
            subjectScheduleTomorrow.addAll(
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
                variableData.set("AccInfo", ProcessResult.Running)

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

                variableData.set("AccInfo", ProcessResult.Successful)
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionCacheData.value.addException(ex)
                ex.printStackTrace()
                variableData.set("AccInfo", ProcessResult.Failed)
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
            variableData["AccLoginStartup"] = true
            if (appSettingsRepo.username != null && appSettingsRepo.password != null)
                login(appSettingsRepo.username!!, appSettingsRepo.password!!)
        }
    }

    // Load settings from appSettings.json, if needed.
    private fun loadSettings() {

    }

    private fun initializeVariables() {
        // Current news global page
        variableData["NewsGlobalPage"] = 1

        // Current news subject page
        variableData["NewsSubjectPage"] = 1

        // Check if have auto login
        variableData["AccLoginStartup"] = false

        // Settings View.
        // 0: Settings (and/or not logged in page)
        // 1: Login page
        // 2: Logging in page
        // 3: Account Information page
        variableData["SettingsPanelIndex"] = 0
    }

    init {
        // Initialize first variables
        initializeVariables()

        // Load settings first before continue.
        loadSettings()

        // Load news cache for backup if internet is not available.
        loadAppCache()
        getSubjectScheduleOnTodayAndTomorrow()
        getSubjectExaminationOnDays()

        // Auto refresh news in server at startup.
        // refreshNewsGlobalFromServer()
        getNewsGlobal(true)
        // refreshNewsSubjectsFromServer()
        getNewsSubject(true)

        // Detect auto login (login if user checked auto login check box)
        executeAutoLogin()
    }
}
