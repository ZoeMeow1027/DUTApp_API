package io.zoemeow.dutapp.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.NewsGlobalItem
import io.zoemeow.dutapp.model.NewsSubjectItem

class NewsDetailsClicked(
    private var showSheetRequested: () -> Unit,
    private var hideSheetRequested: () -> Unit
) {
    var newsGlobal: MutableState<NewsGlobalItem> = mutableStateOf(NewsGlobalItem())
    var newsSubject: MutableState<NewsSubjectItem> = mutableStateOf(NewsSubjectItem())
    private var newsType: MutableState<Int> = mutableStateOf(-1)

    var NewsType: MutableState<Int>
        get() = newsType
        private set(value) { newsType.value = value.value }

    fun setViewDetailsNewsGlobal(value: NewsGlobalItem) {
        newsGlobal.value = value
        NewsType.value = 0
        showSheetRequested()
    }

    fun setViewDetailsNewsSubject(value: NewsSubjectItem) {
        newsSubject.value = value
        NewsType.value = 1
        showSheetRequested()
    }

    fun clearViewDetails() {
        if (newsType.value != -1) {
            newsType.value = -1
            newsGlobal.value = NewsGlobalItem()
            newsSubject.value = NewsSubjectItem()
        }
    }

    fun hideViewDetails() {
        hideSheetRequested()
    }
}
