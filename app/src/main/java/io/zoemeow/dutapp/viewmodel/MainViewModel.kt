package io.zoemeow.dutapp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.model.NewsItem
import io.zoemeow.dutapp.model.NewsType
import io.zoemeow.dutapp.repository.DutMainRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: DutMainRepository) : ViewModel() {
    val dataGlobal: MutableState<NewsItem> = mutableStateOf(NewsItem())
    val dataSubjects: MutableState<NewsItem> = mutableStateOf(NewsItem())

    val loadingGlobal: MutableState<Boolean> = mutableStateOf(false)
    val loadingSubjects: MutableState<Boolean> = mutableStateOf(false)

    init {
        getAllNewsGlobalFromServer()
        getAllNewsSubjectsFromServer()
    }

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
}