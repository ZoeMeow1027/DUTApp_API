package io.zoemeow.dutapp.data

import io.zoemeow.dutapp.model.VariableItem

class VariableData {
    private val list: ArrayList<VariableItem<Any>> = ArrayList()

    operator fun <T: Any> get(key: String): VariableItem<T>? {
        try {
            val d = VariableItem<T>()
            val temp = list.firstOrNull {
                it.key.value == key
            }
            if (temp == null)
                throw Exception("temp is null!")

            d.key.value = temp.key.value
            d.value.value = temp.value.value as T?
            return d
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    operator fun <T: Any> set(key: String, value: T) {
        try {
            if (list.firstOrNull { it.key.value == key } != null)
                list.firstOrNull { it.key.value == key }?.value!!.value = value
            else list.add(VariableItem(key = key, value = value))
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}