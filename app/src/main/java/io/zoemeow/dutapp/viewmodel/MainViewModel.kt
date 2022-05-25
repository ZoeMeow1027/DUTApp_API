package io.zoemeow.dutapp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.model.NewsListItem
import io.zoemeow.dutapp.model.NewsType
import io.zoemeow.dutapp.model.SubjectFeeListItem
import io.zoemeow.dutapp.model.SubjectScheduleListItem
import io.zoemeow.dutapp.repository.DutAccountRepository
import io.zoemeow.dutapp.repository.DutNewsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dutNewsRepo: DutNewsRepository,
    private val dutAccRepo: DutAccountRepository
) : ViewModel() {
    // Get news global
    private val procGlobal: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingNewsGlobal(): MutableState<Boolean> {
        return procGlobal
    }
    val dataGlobal: MutableState<NewsListItem> = mutableStateOf(NewsListItem())
    fun refreshNewsGlobalFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
                procGlobal.value = true
                dataGlobal.value = dutNewsRepo.getAllNews(NewsType.Global, page)
            } catch (_: Exception) {

            }

            procGlobal.value = false
        }
    }

    // Get news subjects
    private val procSubjects: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingNewsSubject(): MutableState<Boolean> {
        return procSubjects
    }
    val dataSubjects: MutableState<NewsListItem> = mutableStateOf(NewsListItem())
    fun refreshAllNewsSubjectsFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
                procSubjects.value = true
                dataSubjects.value = dutNewsRepo.getAllNews(NewsType.Subjects, page)
            } catch (_: Exception) {

            }

            procSubjects.value = false
        }
    }

    // Login/logout
    private val sessionId: MutableState<String> = mutableStateOf(String())
    private val procAccount: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingAccount(): MutableState<Boolean> {
        return procAccount
    }
    fun login(user: String, pass: String, rememberLogin: Boolean = false) {
        viewModelScope.launch {
            procAccount.value = true
            val result = dutAccRepo.dutLogin(user, pass)
            if (result.loggedin) {
                sessionId.value = result.sessionid!!
            }
            procAccount.value = false
        }
    }
    fun logout() {
        viewModelScope.launch {
            procAccount.value = true
            val temp = sessionId.value
            sessionId.value = String()
            dutAccRepo.dutLogout(temp)
            procAccount.value = false
        }
    }
    fun isLoggedIn(): Boolean {
        return (if (sessionId.value == null) false else sessionId.value.isNotEmpty())
    }

    // Get subject schedule and subject fee
    val dataSubjectSchedule: MutableState<SubjectScheduleListItem> = mutableStateOf(SubjectScheduleListItem())
    val dataSubjectFee: MutableState<SubjectFeeListItem> = mutableStateOf(SubjectFeeListItem())
    fun getSubjectScheduleAndFee(year: Int, semester: Int, inSummer: Boolean) {
        viewModelScope.launch {
            try {
                procAccount.value = true
                dataSubjectSchedule.value = dutAccRepo.dutGetSubjectSchedule(sessionId.value, year, semester, inSummer)
                dataSubjectFee.value = dutAccRepo.dutGetSubjectFee(sessionId.value, year, semester, inSummer)
            } catch (_: Exception) {

            }
            procAccount.value = false
        }
    }

    init {
        refreshNewsGlobalFromServer()
        refreshAllNewsSubjectsFromServer()
    }
}
