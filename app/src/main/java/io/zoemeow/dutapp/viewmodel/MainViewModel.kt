package io.zoemeow.dutapp.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.data.*
import io.zoemeow.dutapp.model.*
import io.zoemeow.dutapp.repository.DutAccountRepository
import io.zoemeow.dutapp.repository.DutNewsRepository
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dutNewsRepo: DutNewsRepository,
    private val dutAccRepo: DutAccountRepository,
    private val dutNewsGlobalCacheRepo: NewsGlobalCacheRepository
) : ViewModel() {
    // Exception will be saved here.
    private val exceptionWithCache: MutableState<ExceptionWithCache> = mutableStateOf(ExceptionWithCache())

    // News View.
    val newsDetailsClicked: MutableState<NewsDetailsClicked?> = mutableStateOf(null)

    // Account View.
    // 0: Not logged in, 1: Login, 2: Logged In
    val accountPaneIndex = mutableStateOf(0)

    // Get news.
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
    fun refreshNewsGlobalFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
                procGlobal.value = true
                val dataGlobalFromInternet: NewsGlobalListItem = dutNewsRepo.getNewsGlobal(page)
                if (dataGlobalFromInternet.newslist != null) {
                    newsDataWithCache.value.NewsGlobalData.value = dataGlobalFromInternet.newslist

                    for (newsItem: NewsGlobalItem in newsDataWithCache.value.NewsGlobalData.value) {
                        dutNewsGlobalCacheRepo.insertNews(NewsGlobalItem(
                            date = newsItem.date,
                            title = newsItem.title,
                            contentText = newsItem.contentText,
                            links = ArrayList<LinkItem>(newsItem.links),
                            id = md5("${newsItem.date}-${newsItem.title}")
                        ))
                    }
                }
            }
            catch (ex: Exception) {
                exceptionWithCache.value.addException(ex)
                Log.d("NewsGlobal", ex.message.toString())
            }
            procGlobal.value = false
        }
    }

    // Get news subjects
    private val procSubjects: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingNewsSubject(): MutableState<Boolean> { return procSubjects }
    fun refreshNewsSubjectsFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
                procSubjects.value = true
                val dataSubjectsFromInternet: NewsSubjectListItem = dutNewsRepo.getNewsSubject(page)
                if (dataSubjectsFromInternet.newslist != null)
                    newsDataWithCache.value.NewsSubjectData.value = dataSubjectsFromInternet.newslist
            }
            catch (ex: Exception) {
                exceptionWithCache.value.addException(ex)
                Log.d("NewsSubject", ex.message.toString())
            }
            procSubjects.value = false
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
    fun login(user: String, pass: String, rememberLogin: Boolean = false) {
        viewModelScope.launch {
            try {
                procAccount.value = true

                val result = dutAccRepo.dutLogin(user, pass)
                if (result.loggedin)
                    accDataWithCache.value.SessionID.value = result.sessionid!!
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

    init {
        refreshNewsGlobalFromServer()
        refreshNewsSubjectsFromServer()
    }
}
