package io.zoemeow.dutapp.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.zoemeow.dutapp.model.enums.ProcessResult

data class IsProcessingItem(
    val key: MutableState<String> = mutableStateOf(String()),
    val valueProcess: MutableState<ProcessResult> = mutableStateOf(ProcessResult.Unknown),
) {
    constructor(key: String, value: ProcessResult) : this() {
        this.key.value = key
        this.valueProcess.value = value
    }
}