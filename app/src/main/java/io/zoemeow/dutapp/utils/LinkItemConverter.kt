package io.zoemeow.dutapp.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.zoemeow.dutapp.model.news.LinkItem
import kotlin.collections.ArrayList

class LinkItemConverter {
    @TypeConverter
    fun fromLinks(jsonData: String): ArrayList<LinkItem> {
        val gson = Gson()
        val myType = object : TypeToken<ArrayList<LinkItem>>() {}.type
        return gson.fromJson<ArrayList<LinkItem>>(jsonData, myType)
    }

    @TypeConverter
    fun linksToString(arr: ArrayList<LinkItem>): String {
        val gson = Gson()
        return gson.toJson(arr)
    }
}