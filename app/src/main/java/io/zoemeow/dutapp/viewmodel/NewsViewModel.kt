package io.zoemeow.dutapp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.zoemeow.dutapp.model.NewsItem
import io.zoemeow.dutapp.model.NewsType
import io.zoemeow.dutapp.repository.DutNewsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: DutNewsRepository)
    : ViewModel() {
    val dataGlobal: MutableState<NewsItem> = mutableStateOf(NewsItem())
    val dataSubjects: MutableState<NewsItem> = mutableStateOf(NewsItem())

    var loadingGlobal: MutableState<Boolean> = mutableStateOf(false)
    var loadingSubjects: MutableState<Boolean> = mutableStateOf(false)

    init {
        getAllNewsGlobalFromServer()
        getAllNewsSubjectsFromServer()
    }

    fun getAllNewsGlobalFromServer(page: Int = 1) {
        viewModelScope.launch {
            loadingGlobal.value = true
            dataGlobal.value = repository.getAllNews(NewsType.Global, page)
            loadingGlobal.value = false
        }
    }

    fun getAllNewsSubjectsFromServer(page: Int = 1) {
        viewModelScope.launch {
            loadingSubjects.value = true
            dataSubjects.value = repository.getAllNews(NewsType.Subjects, page)
            loadingSubjects.value = false
        }
    }
}