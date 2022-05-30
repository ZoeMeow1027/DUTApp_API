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
    private var newsTypePri: MutableState<Int> = mutableStateOf(-1)

    var newsType: MutableState<Int>
        get() = newsTypePri
        private set(value) { newsTypePri.value = value.value }

    fun setViewDetailsNewsGlobal(value: NewsGlobalItem) {
        newsGlobal.value = value
        newsType.value = 0
        showSheetRequested()
    }

    fun setViewDetailsNewsSubject(value: NewsSubjectItem) {
        newsSubject.value = value
        newsType.value = 1
        showSheetRequested()
    }

    fun clearViewDetails() {
        if (newsType.value != -1) {
            newsType.value = -1
        }
    }

    fun hideViewDetails() {
        hideSheetRequested()
    }
}
