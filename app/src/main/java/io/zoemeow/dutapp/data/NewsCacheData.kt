package io.zoemeow.dutapp.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.news.NewsGlobalItem
import io.zoemeow.dutapp.model.news.NewsSubjectItem

class NewsCacheData {
    private var dataGlobal: MutableState<ArrayList<NewsGlobalItem>> = mutableStateOf(ArrayList())
    private var dataSubject: MutableState<ArrayList<NewsSubjectItem>> = mutableStateOf(ArrayList())

    var newsGlobalData: MutableState<ArrayList<NewsGlobalItem>>
        get() = dataGlobal
        set(value) {
            dataGlobal.value.clear()
            dataGlobal.value.addAll(value.value)
        }

    var newsSubjectData: MutableState<ArrayList<NewsSubjectItem>>
        get() = dataSubject
        set(value) {
            dataSubject.value.clear()
            dataSubject.value.addAll(value.value)
        }
}
