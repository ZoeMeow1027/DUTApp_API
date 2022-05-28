package io.zoemeow.dutapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.zoemeow.dutapp.LinkItemConverter
import io.zoemeow.dutapp.model.NewsGlobalItem
import io.zoemeow.dutapp.model.NewsSubjectItem

@Database(
    entities = [NewsGlobalItem::class, NewsSubjectItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LinkItemConverter::class)
abstract class NewsCacheDatabase: RoomDatabase() {
    abstract fun getNewsGlobalDbDao(): NewsGlobalCacheDatabaseDao
    abstract fun getNewsSubjectDbDao(): NewsSubjectCacheDatabaseDao
}