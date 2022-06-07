package io.zoemeow.dutapp.data

import io.zoemeow.dutapp.model.IsProcessingItem
import io.zoemeow.dutapp.model.enums.ProcessResult

class IsProcessingData {
    private val list: ArrayList<IsProcessingItem> = ArrayList()

    operator fun get(key: String): IsProcessingItem? {
        return list.firstOrNull {
            it.key.value == key
        }
    }

    operator fun set(key: String, value: ProcessResult) {
        if (list.firstOrNull { it.key.value == key } != null)
            list.firstOrNull { it.key.value == key }!!.valueProcess.value = value
        else
            list.add(IsProcessingItem(key = key, value = value))
    }
}