package com.example.rfid_scanner.utils.generic

class HandleEvent<T>(content: T?) {
    private val mContent: T
    private var hasBeenHandled = false
    val contentIfNotHandled: T?
        get() = if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            mContent
        }

    fun hasBeenHandled(): Boolean {
        return hasBeenHandled
    }

    init {
        requireNotNull(content) { "null values in Event are not allowed." }
        mContent = content
    }
}