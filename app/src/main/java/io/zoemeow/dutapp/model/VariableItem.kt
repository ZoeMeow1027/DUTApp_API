package io.zoemeow.dutapp.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class VariableItem<T> (
    val key: MutableState<String> = mutableStateOf(String()),
    val value: MutableState<T?> = mutableStateOf(null),
) {
    constructor(key: String, value: T) : this() {
        this.key.value = key
        this.value.value = value
    }
}