package io.zoemeow.dutapp.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.zoemeow.dutapp.model.news.NewsGlobalItem
import io.zoemeow.dutapp.model.news.NewsSubjectItem

class NewsCacheData {
    private var dataGlobal: SnapshotStateList<NewsGlobalItem> = mutableStateListOf()
    private var dataSubject: SnapshotStateList<NewsSubjectItem> = mutableStateListOf()

    var newsGlobalData: SnapshotStateList<NewsGlobalItem>
        get() = dataGlobal
        set(value) {
            dataGlobal.clear()
            dataGlobal.addAll(value)
        }

    var newsSubjectData: SnapshotStateList<NewsSubjectItem>
        get() = dataSubject
        set(value) {
            dataSubject.clear()
            dataSubject.addAll(value)
        }
}
