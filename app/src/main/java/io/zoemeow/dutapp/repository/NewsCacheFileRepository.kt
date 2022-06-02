package io.zoemeow.dutapp.repository

import com.google.gson.Gson
import io.zoemeow.dutapp.model.NewsCacheItem
import io.zoemeow.dutapp.model.NewsGlobalItem
import io.zoemeow.dutapp.model.NewsSubjectItem
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject

class NewsCacheFileRepository @Inject constructor(
    private val file: File
) {
    private var newsCache: NewsCacheItem = NewsCacheItem()

    var newsGlobalUpdateTime: Long
        get() = newsCache.newsGlobalUpdateTime
        set(value) {
            newsCache.newsGlobalUpdateTime = value
            exportSettings()
        }

    var newsSubjectUpdateTime: Long
        get() = newsCache.newsSubjectUpdateTime
        set(value) {
            newsCache.newsSubjectUpdateTime = value
            exportSettings()
        }

    fun setNewsGlobal(newsList: ArrayList<NewsGlobalItem>, append: Boolean = false) {
        if (!append) {
            newsCache.newsGlobalList.clear()
            newsCache.newsGlobalList.addAll(newsList)
        }
        else {
            // TODO: Append news global here.
        }
        exportSettings()
    }

    fun getNewsGlobal(): ArrayList<NewsGlobalItem> {
        return newsCache.newsGlobalList
    }

    fun getNewsGlobal(id: String): NewsGlobalItem? {
        return newsCache.newsGlobalList.firstOrNull { it.id == id }
    }

    fun deleteNewsGlobal(item: NewsGlobalItem) {
        newsCache.newsGlobalList.remove(item)
        exportSettings()
    }

    fun deleteAllNewsGlobal() {
        newsCache.newsGlobalList.clear()
        exportSettings()
    }

    fun setNewsSubject(newsList: ArrayList<NewsSubjectItem>, append: Boolean = false) {
        if (!append) {
            newsCache.newsSubjectList.clear()
            newsCache.newsSubjectList.addAll(newsList)
        }
        else {
            // TODO: Append news subject here.
        }
        exportSettings()
    }

    fun getNewsSubject(): ArrayList<NewsSubjectItem> {
        return newsCache.newsSubjectList
    }

    fun getNewsSubject(id: String): NewsSubjectItem? {
        return newsCache.newsSubjectList.firstOrNull { it.id == id }
    }

    fun deleteNewsSubject(item: NewsSubjectItem) {
        newsCache.newsSubjectList.remove(item)
        exportSettings()
    }

    fun deleteAllNewsSubject() {
        newsCache.newsSubjectList.clear()
        exportSettings()
    }

    private fun importSettings() {
        try {
            val buffer: BufferedReader = file.bufferedReader()
            val inputStr = buffer.use { it.readText() }
            buffer.close()
            newsCache = Gson().fromJson(inputStr, NewsCacheItem::class.java)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun exportSettings() {
        try {
            val str = Gson().toJson(newsCache)
            file.writeText(str)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    init {
        importSettings()
    }
}