package io.zoemeow.dutapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.zoemeow.dutapp.LinkItemConverter
import io.zoemeow.dutapp.model.NewsGlobalItem

@Database(
    entities = [NewsGlobalItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LinkItemConverter::class)
abstract class NewsGlobalCacheDatabase: RoomDatabase() {
    abstract fun getDao(): NewsGlobalCacheDatabaseDao
}