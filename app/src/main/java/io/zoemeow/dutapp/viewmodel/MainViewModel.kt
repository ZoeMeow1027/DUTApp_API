package io.zoemeow.dutapp.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import io.zoemeow.dutapp.repository.*
import io.zoemeow.dutapp.utils.getCurrentLesson
import io.zoemeow.dutapp.utils.getDayOfWeek
import io.zoemeow.dutapp.utils.getMD5FromString
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dutApiRepo: DutApiRepository,
    private val appSettings2Repo: AppSettingsFileRepository,
    private val newsCacheFileRepo: NewsCacheFileRepository,
    private val accCacheFileRepo: SubjectCacheFileRepository,
    // Exception will be saved here.
    private val exceptionFileRepository: ExceptionFileRepository,
) : ViewModel() {
    // Get SnackBar host state from main activity
    internal val mainActivitySnackBarHostState: MutableState<SnackbarHostState?> = mutableStateOf(null)

    // Get context main activity
    internal val mainActivityContext: MutableState<Context?> = mutableStateOf(null)

    // News Details View when clicked a news.
    internal val newsDetailsClickedData: MutableState<NewsDetailsClickedData?> = mutableStateOf(null)

    // Initialize temporary data
    internal val tempVarData = TemporaryVariableData()

    // News data with cache (for easier manage).
    internal val newsCacheData: NewsCacheData = NewsCacheData()

    // Account information, subject schedule and subject fee cache
    internal val accCacheData: AccountCacheData = AccountCacheData()

    // Get news global from server
    fun getNewsGlobal(force: Boolean) {
        viewModelScope.launch {
            if (force) {
                tempVarData["NewsGlobalPage"] = "1"
                tempVarData["NewsGlobal"] = ProcessResult.Failed.result.toString()
            }

            if (
                if (tempVarData["NewsGlobal"].value != null)
                    tempVarData["NewsGlobal"].value!!.toInt() == ProcessResult.Successful.result
                else false
            ) tempVarData["NewsGlobalPage"] = tempVarData["NewsGlobalPage"].value!!.toInt().plus(1).toString()

            refreshNewsGlobalFromServer(
                tempVarData["NewsGlobalPage"].value!!.toInt(),
                !force
            )
        }
    }

    /**
     * Get news global from server.
     *
     * @param page: Page to get.
     * @param append: If false, will delete current cache and write again.
     */
    private suspend fun refreshNewsGlobalFromServer(page: Int = 1, append: Boolean = false) {
        tempVarData["NewsGlobal"] = ProcessResult.Running.result.toString()

        try {
            val dataGlobalFromInternet: NewsGlobalListItem = dutApiRepo.getNewsGlobal(page)

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
                    newsCacheData.newsGlobalData.clear()
                newsCacheData.newsGlobalData.addAll(list)
            } else throw Exception("News list empty.")

            // Return true
            tempVarData["NewsGlobal"] = ProcessResult.Successful.result.toString()
        }
        catch (ex: Exception) {
            exceptionFileRepository.writeToFile(ex)

            // Return false
            tempVarData["NewsGlobal"] = ProcessResult.Failed.result.toString()

            // Notify that can't load news here.
            mainActivitySnackBarHostState.value?.showSnackbar(
                mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
            )
        }
    }

    fun getNewsSubject(force: Boolean) {
        viewModelScope.launch {
            if (force) {
                tempVarData["NewsSubjectPage"] = 1.toString()
                tempVarData["NewsSubject"] = ProcessResult.Failed.result.toString()
            }

            if (
                if (tempVarData["NewsSubject"].value != null)
                    tempVarData["NewsSubject"].value == ProcessResult.Successful.result.toString()
                else false
            ) tempVarData["NewsSubjectPage"] = tempVarData["NewsSubjectPage"].value!!.toInt().plus(1).toString()

            refreshNewsSubjectsFromServer(
                tempVarData["NewsSubjectPage"].value!!.toInt(),
                !force
            )
        }
    }

    /**
     * Get news subject from server.
     *
     * @param page: Page to get.
     * @param append: If false, will delete current cache and write again.
     */
    private suspend fun refreshNewsSubjectsFromServer(page: Int = 1, append: Boolean = false) {
        tempVarData["NewsSubject"] = ProcessResult.Running.result.toString()

        try {
            val dataSubjectsFromInternet: NewsSubjectListItem = dutApiRepo.getNewsSubject(page)

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
                    newsCacheData.newsSubjectData.clear()
                newsCacheData.newsSubjectData.addAll(list)
            } else throw Exception("News list empty.")

            // Return true
            tempVarData["NewsSubject"] = ProcessResult.Successful.result.toString()
        }
        catch (ex: Exception) {
            exceptionFileRepository.writeToFile(ex)

            // Return false
            tempVarData["NewsSubject"] = ProcessResult.Failed.result.toString()

            // Notify that can't load news here.
            mainActivitySnackBarHostState.value?.showSnackbar(
                mainActivityContext.value?.getString(R.string.navnaws_notify_loadnewsfailed)!!
            )
        }
    }

    /**
     * Login to server using your account in sv.dut.udn.vn.
     *
     * @param user: Username
     * @param pass: Password
     * @param rememberLogin: Set true to save account to your device, which is used for auto login at app startup.
     */
    fun login(user: String, pass: String, rememberLogin: Boolean = true) {
        viewModelScope.launch {
            // Navigate to page logging in
            tempVarData["SettingsPanelIndex"] = "2"
            tempVarData["LoggingIn"] = ProcessResult.Running.result.toString()

            try {
                // Login
                val result = dutApiRepo.dutLogin(user, pass)

                // If login successfully
                if (result.logged_in) {
                    // Save session id to cache
                    accCacheData.sessionID.value = result.session_id!!
                    Log.d("CheckLogin", "Logged in")
                    // Only logged in will can remember login
                    if (rememberLogin) {
                        // Save to app settings
                        appSettings2Repo["AutoLogin"] = rememberLogin.toString()
                        appSettings2Repo["Username"] = user
                        appSettings2Repo["Password"] = pass
                    }
                }
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionFileRepository.writeToFile(ex)
            }

            // All result will be returned to main page.
            tempVarData["SettingsPanelIndex"] = "0"

            // If logged in (check session id is not empty)
            if (accCacheData.sessionID.value.isNotEmpty()) {
                tempVarData["LoggingIn"] = ProcessResult.Successful.result.toString()
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
            else if (tempVarData["AccLoginStartup"].value!!.toBoolean()) {
                tempVarData["LoggingIn"] = ProcessResult.Failed.result.toString()
                tempVarData["SettingsPanelIndex"] = "0"
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_autologinfailed)!!
                )
                tempVarData["AccLoginStartup"] = false.toString()
            }
            // Any failed while logging in will be return to login/not logged in.
            else {
                tempVarData["LoggingIn"] = ProcessResult.Failed.result.toString()
                tempVarData["SettingsPanelIndex"] = "1"
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_loginfailed)!!
                )
            }
        }
    }

    /**
     * Logout your account and delete all saved account from your device.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                // Clear auto login settings
                clearAutoLogin()

                // Get information before logout
                val temp = accCacheData.sessionID.value
                accCacheData.clearAllData()

                // Navigate to page not logged in
                tempVarData["SettingsPanelIndex"] = "0"

                // Logout
                viewModelScope.launch {
                    dutApiRepo.dutLogout(temp)
                }

                // Show snack bar logged out
                mainActivitySnackBarHostState.value?.showSnackbar(
                    mainActivityContext.value?.getString(R.string.navlogin_screenlogin_loggedout)!!
                )
            }
            catch (ex: Exception) {
                exceptionFileRepository.writeToFile(ex)
            }
        }
    }

    /**
     * Clear all auto login settings and subject cache.
     */
    private fun clearAutoLogin() {
        appSettings2Repo["AutoLogin"] = false.toString()
        appSettings2Repo["Username"] = ""
        appSettings2Repo["Password"] = ""
        accCacheFileRepo.deleteAllSubjectSchedule()
        accCacheFileRepo.deleteAllSubjectFee()
        accCacheFileRepo.deleteAccountInformation()
    }

    /**
     * Get subject schedule from server with subjectSchoolYearSettings.
     */
    fun refreshSubjectSchedule() {
        viewModelScope.launch {
            try {
                tempVarData["SubjectSchedule"] = ProcessResult.Running.result.toString()

                // Get subject schedule
                val dataSubjectScheduleFromInternet = dutApiRepo.dutGetSubjectSchedule(
                    sid = accCacheData.sessionID.value,
                    year = subjectSchoolYearSettings[0],
                    semester = subjectSchoolYearSettings[1],
                    inSummer = subjectSchoolYearSettings[2] == 1
                )

                if (dataSubjectScheduleFromInternet.schedule_list != null &&
                        dataSubjectScheduleFromInternet.schedule_list.size > 0) {
                    // Write to json
                    accCacheFileRepo.setSubjectSchedule(dataSubjectScheduleFromInternet.schedule_list)
                    accCacheFileRepo.subjectScheduleUpdateTime = dataSubjectScheduleFromInternet.date!!
                    accCacheFileRepo.setSubjectCreditTotal(dataSubjectScheduleFromInternet.total_credit!!)

                    // Add to cache
                    accCacheData.subjectScheduleData.clear()
                    accCacheData.subjectScheduleData.addAll(dataSubjectScheduleFromInternet.schedule_list)
                    accCacheData.subjectCredit = dataSubjectScheduleFromInternet.total_credit
                }

                tempVarData["SubjectSchedule"] = ProcessResult.Successful.result.toString()
                accCacheData.subjectGetTime = dataSubjectScheduleFromInternet.date!!
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionFileRepository.writeToFile(ex)
                tempVarData["SubjectSchedule"] = ProcessResult.Failed.result.toString()
            }

            // TODO: Development for current day subjects here!
            getSubjectScheduleOnTodayAndTomorrow()
            getSubjectExaminationOnDays()
        }
    }

    /**
     * Get subject fee from server with subjectSchoolYearSettings.
     */
    fun refreshSubjectFee() {
        viewModelScope.launch {
            try {
                tempVarData["SubjectFee"] = ProcessResult.Running.result.toString()

                // Get subject fee
                val dataSubjectFeeFromInternet = dutApiRepo.dutGetSubjectFee(
                    sid = accCacheData.sessionID.value,
                    year = subjectSchoolYearSettings[0],
                    semester = subjectSchoolYearSettings[1],
                    inSummer = subjectSchoolYearSettings[2] == 1
                )
                if (dataSubjectFeeFromInternet.fee_list != null &&
                    dataSubjectFeeFromInternet.fee_list.size > 0) {
                    // Write to json
                    accCacheFileRepo.setSubjectFee(dataSubjectFeeFromInternet.fee_list)
                    accCacheFileRepo.subjectFeeUpdateTime = dataSubjectFeeFromInternet.date!!
                    accCacheFileRepo.setSubjectCreditTotal(dataSubjectFeeFromInternet.total_credit!!)
                    accCacheFileRepo.setSubjectMoneyTotal(dataSubjectFeeFromInternet.total_money!!)

                    // Add to cache
                    accCacheData.subjectFeeData.clear()
                    accCacheData.subjectFeeData.addAll(dataSubjectFeeFromInternet.fee_list)
                    accCacheData.subjectCredit = dataSubjectFeeFromInternet.total_credit
                    accCacheData.subjectMoney = dataSubjectFeeFromInternet.total_money
                }
                accCacheData.subjectGetTime = dataSubjectFeeFromInternet.date!!
                tempVarData["SubjectFee"] = ProcessResult.Successful.result.toString()
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionFileRepository.writeToFile(ex)
                tempVarData["SubjectFee"] = ProcessResult.Failed.result.toString()
            }
        }
    }

    /**
     * Get account information from sv.dut.udn.vn.
     */
    private fun refreshAccountInfo() {
        viewModelScope.launch {
            try {
                tempVarData["AccInfo"] = ProcessResult.Running.result.toString()

                // Get account information
                val dataAccInfoFromInternet = dutApiRepo.dutGetAccInfo(
                    accCacheData.sessionID.value)
                if (dataAccInfoFromInternet.account_info != null) {
                    // Add to cache
                    accCacheData.accountInformationData.value = dataAccInfoFromInternet.account_info
                    // Write to json
                    accCacheFileRepo.setAccountInformation(dataAccInfoFromInternet.account_info)
                    accCacheFileRepo.accountInformationUpdateTime = dataAccInfoFromInternet.date!!
                }

                tempVarData["AccInfo"] = ProcessResult.Successful.result.toString()
            }
            // Any exception will be here!
            catch (ex: Exception) {
                exceptionFileRepository.writeToFile(ex)
                tempVarData["AccInfo"] = ProcessResult.Failed.result.toString()
            }
        }
    }

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
                accCacheData.subjectScheduleData
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
            exceptionFileRepository.writeToFile(ex)
        }

        // Return result
        return result
    }

    private fun getSubjectExaminationOnDays(dayNext: Int = 7, hideAfterMin: Int = 60) {
        try {
            val timeUnix = System.currentTimeMillis()
            subjectExaminationOn7Days.clear()
            subjectExaminationOn7Days.addAll(
                accCacheData.subjectScheduleData
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
            exceptionFileRepository.writeToFile(ex)
        }
    }

    private fun getSubjectScheduleOnTodayAndTomorrow() {
        // Current subject schedule
        try {
            subjectScheduleOnToday.clear()
            subjectScheduleOnToday.addAll(
                getSubjectScheduleInDayOfWeek(0, 0)
            )
        }
        catch (ex: Exception) {
            exceptionFileRepository.writeToFile(ex)
        }

        // Subject schedule on tomorrow
        try {
            subjectScheduleOnFuture.clear()
            subjectScheduleOnFuture.addAll(
                getSubjectScheduleInDayOfWeek(1, 0)
            )
        }
        catch (ex: Exception) {
            exceptionFileRepository.writeToFile(ex)
        }
    }

    fun reloadViewSubjectScheduleOnDay() {
        viewModelScope.launch {
            refreshNewsGlobalFromServer()
            refreshNewsSubjectsFromServer()
        }
        if (accCacheData.sessionID.value.isNotEmpty()) {
            refreshSubjectSchedule()
            refreshSubjectFee()
        }
    }

    fun openLinkInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        mainActivityContext.value?.startActivity(intent)
    }

    // ============================== View area =====================================
    // MainActivity
    internal val subjectGetTime: Long
        get() = accCacheData.subjectGetTime

    internal val subjectExaminationOn7Days: SnapshotStateList<SubjectScheduleItem>
        get() = accCacheData.subjectExaminationOn7Days

    internal val subjectScheduleOnToday: SnapshotStateList<SubjectScheduleItem>
        get() = accCacheData.subjectScheduleDataOnToday

    internal val subjectScheduleOnFuture: SnapshotStateList<SubjectScheduleItem>
        get() = accCacheData.subjectScheduleDataOnFuture

    internal fun isLoggedIn(): Boolean {
        return accCacheData.sessionID.value.isNotEmpty()
    }

    internal fun isAvailableOffline(): Boolean {
        return appSettings2Repo["AutoLogin"].value.toBoolean() &&
                accCacheFileRepo.getAccountInformation().studentId != null &&
                accCacheFileRepo.getSubjectSchedule().size != 0 &&
                accCacheFileRepo.getSubjectFee().size != 0
    }
    // ==============================================================================

    var subjectSchoolYearSettings: ArrayList<Int>
        get() = arrayListOf(
            appSettings2Repo["ScheduleYear"].value?.toInt() ?: 21,
            appSettings2Repo["ScheduleSemester"].value?.toInt() ?: 2,
            if (appSettings2Repo["ScheduleInSummer"].value?.toBoolean() == true)
                1 else 0
        )
        set(value) {
            try {
                if (value.size == 3) {
                    appSettings2Repo["ScheduleYear"] = value[0].toString()
                    appSettings2Repo["ScheduleSemester"] = value[1].toString()
                    appSettings2Repo["ScheduleInSummer"] =
                        (value[2] == 1).toString()
                }
                else throw Exception("Value length must equal 3!")
            }
            catch (ex: Exception) {
                exceptionFileRepository.writeToFile(ex)
            }
        }

    /**
     * Load news cache for backup if internet is not available.
     */
    private fun loadAppCache() {
        newsCacheData.newsGlobalData.addAll(newsCacheFileRepo.getNewsGlobal())
        newsCacheData.newsSubjectData.addAll(newsCacheFileRepo.getNewsSubject())

        accCacheData.accountInformationData.value = accCacheFileRepo.getAccountInformation()
        accCacheData.subjectScheduleData.clear()
        accCacheData.subjectScheduleData.addAll(accCacheFileRepo.getSubjectSchedule())
        accCacheData.subjectFeeData.clear()
        accCacheData.subjectFeeData.addAll(accCacheFileRepo.getSubjectFee())
    }

    /**
     * Detect auto login (login if user checked auto login check box)
     */
    fun executeAutoLogin() {
        if (appSettings2Repo["AutoLogin"].value.toBoolean()) {
            tempVarData["AccLoginStartup"] = true.toString()
            if (appSettings2Repo["Username"].value != null && appSettings2Repo["Password"].value != null)
                login(appSettings2Repo["Username"].value!!, appSettings2Repo["Password"].value!!)
        }
    }

    /**
     * Load settings from appSettings.json, if needed.
     */
    private fun loadSettings() {
        if (appSettings2Repo["JsonSettingsVersion"].value != null) {
            // TODO: Execute for json older than current here!
        }
        else appSettings2Repo["JsonSettingsVersion"] = "1"
    }

    /**
     * Varaiable at app startup.
     */
    private fun initializeVariables() {
        // Current news global page
        tempVarData["NewsGlobalPage"] = "1"

        // Current news subject page
        tempVarData["NewsSubjectPage"] = "1"

        // Check if have auto login
        tempVarData["AccLoginStartup"] = false.toString()

        // Settings View.
        // 0: Settings (and/or not logged in page)
        // 1: Login page
        // 2: Logging in page
        // 3: Account Information page
        tempVarData["SettingsPanelIndex"] = "0"
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
