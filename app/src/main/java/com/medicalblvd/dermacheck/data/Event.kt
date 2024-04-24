package com.medicalblvd.dermacheck.data


open class Event<out T>(private val content: T) {

    @Volatile
    var hasBeenHandled = false
        private set

    @Synchronized
    fun getContentOrNull(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun handleIfNotHandled(action: (T) -> Unit) {
        getContentOrNull()?.let(action)
    }
}
