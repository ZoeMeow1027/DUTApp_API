package io.zoemeow.dutapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "newsGlobalCache")
data class NewsGlobalItem(
    @PrimaryKey
    @NotNull
    @Expose
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo
    var date: Long? = null,

    @ColumnInfo
    var title: String? = null,

    @ColumnInfo
    @SerializedName("contenttext")
    var contentText: String? = null,

    @ColumnInfo
    var links: ArrayList<LinkItem>? = null,
)
