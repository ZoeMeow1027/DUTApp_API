package io.zoemeow.dutapp.data

class ExceptionWithCache() {
    private var ex: ArrayList<Exception> = ArrayList()

    fun addException(ex: Exception) {
        this.ex.add(ex)
    }
}