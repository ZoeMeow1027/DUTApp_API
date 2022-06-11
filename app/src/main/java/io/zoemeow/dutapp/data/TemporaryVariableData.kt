package io.zoemeow.dutapp.data

import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.VariableItem

class TemporaryVariableData {
    private val varList: ArrayList<VariableItem> = ArrayList()
    internal val changedCount = mutableStateOf(0)

    operator fun get(key: String): VariableItem {
        return try {
            val temp = varList.firstOrNull {
                it.key == key
            } ?: throw Exception("temp is null!")

            val d = VariableItem(temp.key, temp.value)
            d
        } catch (ex: Exception) {
            ex.printStackTrace()
            VariableItem(key = key, value = null)
        }
    }

    operator fun set(key: String, value: String) {
        try {
            if (varList.firstOrNull { it.key == key } != null)
                varList.firstOrNull { it.key == key }?.value = value
            else varList.add(VariableItem(key = key, value = value))
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
        changedCount.value += 1
    }
}