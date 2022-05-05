package com.example.rfid_scanner.data.model.repository

class MResponse(
    val state: Int,
    val response: ResponseData?
) {

    companion object {
        const val FINISHED_SUCCESS = 0
        const val FINISHED_FAILURE = 1
        const val LOADING = 2
    }

    data class ResponseData(val code: String,
                            val message: String,
                            var data: Any?)
}