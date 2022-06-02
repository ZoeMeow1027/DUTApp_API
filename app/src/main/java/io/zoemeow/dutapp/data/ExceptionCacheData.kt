package io.zoemeow.dutapp.data

class ExceptionCacheData {
    private var ex: ArrayList<Exception> = ArrayList()

    fun addException(ex: Exception) {
        this.ex.add(ex)
        ex.printStackTrace()
    }
}