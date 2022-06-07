package io.zoemeow.dutapp.model.enums

enum class ProcessResult(val result: Int) {
    Unknown(-1),
    NotRun(0),
    Running(1),
    Successful(2),
    Failed(3)
}