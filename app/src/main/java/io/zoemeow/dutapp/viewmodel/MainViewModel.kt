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
import io.zoemeow.dutapp.repository.DutMainRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: DutMainRepository) : ViewModel() {
    // Get news
    val dataGlobal: MutableState<NewsListItem> = mutableStateOf(NewsListItem())
    val dataSubjects: MutableState<NewsListItem> = mutableStateOf(NewsListItem())

    val loadingGlobal: MutableState<Boolean> = mutableStateOf(false)
    val loadingSubjects: MutableState<Boolean> = mutableStateOf(false)

    fun getAllNewsGlobalFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
                loadingGlobal.value = true
                dataGlobal.value = repository.getAllNews(NewsType.Global, page)
            } catch (_: Exception) {

            }

            loadingGlobal.value = false
        }
    }

    fun getAllNewsSubjectsFromServer(page: Int = 1) {
        viewModelScope.launch {
            try {
                loadingSubjects.value = true
                dataSubjects.value = repository.getAllNews(NewsType.Subjects, page)
            } catch (_: Exception) {

            }

            loadingSubjects.value = false
        }
    }

    // Login/logout
    private val processing: MutableState<Boolean> = mutableStateOf(false)
    private val sessionId: MutableState<String> = mutableStateOf(String())

    fun login(user: String, pass: String) {
        viewModelScope.launch {
            processing.value = true
            val result = repository.dutLogin(user, pass)
            if (result.loggedin) {
                sessionId.value = result.sessionid!!
            }
            processing.value = false
        }
    }

    fun isProcessing(): MutableState<Boolean> {
        return processing
    }

    fun logout() {
        viewModelScope.launch {
            processing.value = true
            val temp = sessionId.value
            sessionId.value = String()
            repository.dutLogout(temp)
            processing.value = false
        }
    }

    fun loggedIn(): Boolean {
        return (
                if (sessionId.value == null)
                    false
                else sessionId.value.isNotEmpty()
                )
    }

    // Get subject schedule and subject fee
    val dataSubjectSchedule: MutableState<SubjectScheduleListItem> = mutableStateOf(SubjectScheduleListItem())
    val dataSubjectFee: MutableState<SubjectFeeListItem> = mutableStateOf(SubjectFeeListItem())
    fun getSubjectScheduleAndFee(year: Int, semester: Int, inSummer: Boolean) {
        viewModelScope.launch {
            try {
                processing.value = true
                dataSubjectSchedule.value = repository.dutGetSubjectSchedule(sessionId.value, year, semester, inSummer)
                dataSubjectFee.value = repository.dutGetSubjectFee(sessionId.value, year, semester, inSummer)
            }
            catch (_: Exception) {

            }
            processing.value = false
        }
    }

    init {
        getAllNewsGlobalFromServer()
        getAllNewsSubjectsFromServer()
    }
}
