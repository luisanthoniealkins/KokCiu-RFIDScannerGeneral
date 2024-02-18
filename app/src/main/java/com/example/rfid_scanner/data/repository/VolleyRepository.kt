package com.example.rfid_scanner.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.rfid_scanner.data.model.repository.MResponse
import com.example.rfid_scanner.data.model.repository.MResponse.Companion.FINISHED_FAILURE
import com.example.rfid_scanner.data.model.repository.MResponse.Companion.FINISHED_SUCCESS
import com.example.rfid_scanner.data.model.repository.MResponse.Companion.LOADING
import com.example.rfid_scanner.data.model.repository.MResponse.ResponseData
import com.example.rfid_scanner.data.repository.component.RequestResult.getGeneralResponse
import com.example.rfid_scanner.data.repository.component.ResponseCode
import com.example.rfid_scanner.service.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject


class VolleyRepository(private val context: Context) {

    companion object {
        private const val IS_DEBUG = true

        @SuppressLint("StaticFieldLeak")
        var mInstance: VolleyRepository? = null
            private set

        fun getI() = mInstance!!

        fun init(context: Context) { mInstance = VolleyRepository(context) }
    }

    private val _sfStatus = MutableStateFlow(null as Boolean?)
    val sfStatus = _sfStatus.asStateFlow()

    private var requestQueue: RequestQueue? = null

    init {
        requestQueue = getRequestQueue()
    }

    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(context)
        return requestQueue
    }

    // API CALL
    fun requestAPI(endpoint: String, obj: JSONObject? = null, function: ((JSONObject) -> ResponseData)? = null, useRetryPolicy: Boolean = true): StateFlow<MResponse> {
        if (obj != null && IS_DEBUG) Log.d("12345", "$endpoint $obj")

        val sfResponse = MutableStateFlow(MResponse(LOADING, null))
        val baseUrl = "http://${StorageService.getI().ipAddress}:${StorageService.getI().port}/app/"
        val url = "$baseUrl$endpoint.php"
        val request =
            JsonObjectRequest(
                Request.Method.POST,
                url,
                obj,
                { response ->
                    if (IS_DEBUG) Log.d("12345", response.toString())

                    if (isServerAvailable(response)) {
                        _sfStatus.value = true
                        if (isResponseOK(response))
                            sfResponse.value = MResponse(FINISHED_SUCCESS, function?.let { it(response) })
                        else
                            sfResponse.value = MResponse(FINISHED_FAILURE, getGeneralResponse(response))

                    } else {
                        _sfStatus.value = false
                        sfResponse.value = MResponse(FINISHED_FAILURE, getGeneralResponse(response))
                    }
                }
            ) { errorResponse ->
                if (IS_DEBUG) Log.d("12345", errorResponse.toString())

                _sfStatus.value = false
                sfResponse.value = MResponse(FINISHED_FAILURE, null)
            }

        if (useRetryPolicy) {
            request.retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

        requestQueue?.add(request)

        return sfResponse
    }

    private fun isServerAvailable(response: JSONObject): Boolean {
        return (response.getString("response") != ResponseCode.SERVER_ERROR)
    }

    private fun isResponseOK(response: JSONObject): Boolean {
        return (response.getString("response") == ResponseCode.OK)
    }

}