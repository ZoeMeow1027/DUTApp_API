package io.zoemeow.dutapp.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.model.*
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
    val dataGlobal: MutableState<NewsGlobalListItem> = mutableStateOf(NewsGlobalListItem())
    fun refreshNewsGlobalFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
//                if (procGlobal.value)
//                    throw Exception("Another process is running!")
                procGlobal.value = true

                dataGlobal.value = dutNewsRepo.getNewsGlobal(page)
            }
            catch (ex: Exception) {
                Log.d("NewsGlobal", ex.message.toString())
            }
            procGlobal.value = false
        }
    }

    // Get news subjects
    private val procSubjects: MutableState<Boolean> = mutableStateOf(false)
    fun isProcessingNewsSubject(): MutableState<Boolean> {
        return procSubjects
    }
    val dataSubjects: MutableState<NewsSubjectListItem> = mutableStateOf(NewsSubjectListItem())
    fun refreshNewsSubjectsFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
//                if (procSubjects.value)
//                    throw Exception("Another process is running!")
                procSubjects.value = true

                dataSubjects.value = dutNewsRepo.getNewsSubject(page)
            }
            catch (ex: Exception) {
                Log.d("NewsSubject", ex.message.toString())
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
            try {
//                if (procAccount.value)
//                    throw Exception("Another process is running!")
                procAccount.value = true

                val result = dutAccRepo.dutLogin(user, pass)
                if (result.loggedin)
                    sessionId.value = result.sessionid!!
            }
            catch (ex: Exception) {
                Log.d("Login", ex.message.toString())
            }
            procAccount.value = false
        }
    }
    fun logout() {
        viewModelScope.launch {
            try {
//                procAccount.value = false
//                if (procAccount.value)
//                    throw Exception("Another process is running!")
                procAccount.value = true

                val temp = sessionId.value
                dutAccRepo.dutLogout(temp)
                resetAccountVariable()
            }
            catch (ex: Exception) {
                Log.d("Logout", ex.message.toString())
            }
            procAccount.value = false
        }
    }
    fun isLoggedIn(): Boolean {
        return (if (sessionId.value == null) false else sessionId.value.isNotEmpty())
    }
    private fun resetAccountVariable() {
        sessionId.value = String()
        dataSubjectSchedule.value = SubjectScheduleListItem()
        dataSubjectFee.value = SubjectFeeListItem()
        dataAccInfo.value = AccountInformationMainItem()
    }

    // Get subject schedule and subject fee
    val dataSubjectSchedule: MutableState<SubjectScheduleListItem> = mutableStateOf(SubjectScheduleListItem())
    val dataSubjectFee: MutableState<SubjectFeeListItem> = mutableStateOf(SubjectFeeListItem())
    fun getSubjectScheduleAndFee(year: Int, semester: Int, inSummer: Boolean) {
        viewModelScope.launch {
            try {
//                if (procAccount.value)
//                    throw Exception("Another process is running!")
                procAccount.value = true

                dataSubjectSchedule.value = dutAccRepo.dutGetSubjectSchedule(sessionId.value, year, semester, inSummer)
                dataSubjectFee.value = dutAccRepo.dutGetSubjectFee(sessionId.value, year, semester, inSummer)
            }
            catch (ex: Exception) {
                Log.d("SubjectScheduleFee", ex.message.toString())
            }
            procAccount.value = false
        }
    }

    // Get account information
    val dataAccInfo: MutableState<AccountInformationMainItem> = mutableStateOf(AccountInformationMainItem())
    fun getAccountInformation() {
        viewModelScope.launch {
            try {
//                if (procAccount.value)
//                    throw Exception("Another process is running!")
                procAccount.value = true

                dataAccInfo.value = dutAccRepo.dutGetAccInfo(sessionId.value)
            }
            catch (ex: Exception) {
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
