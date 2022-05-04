package com.example.rfid_scanner.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.rfid_scanner.data.repository.helper.ResponseCode
import com.example.rfid_scanner.utils.helper.ToastHelper
import com.example.rfid_scanner.utils.service.StorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class VolleyRepository(private val context: Context) {

    data class ResponseLiveData(
        val data : MutableLiveData<JSONObject>,
        val error : MutableLiveData<VolleyError>
    )

    companion object {
        private const val IS_DEBUG = true

        @SuppressLint("StaticFieldLeak")
        var mInstance: VolleyRepository? = null
            private set

        fun getInstance() = mInstance!!

        fun init(context: Context) { mInstance = VolleyRepository(context) }
    }

    private val _sfStatus = MutableLiveData<Boolean>()
    val sfStatus : LiveData<Boolean> = _sfStatus

    private var requestQueue: RequestQueue? = null

    init {
        requestQueue = getRequestQueue()
    }

    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(context)
        return requestQueue
    }

    // API CALL
    fun requestAPI(endpoint: String, obj: JSONObject? = null): ResponseLiveData {
        val data = MutableLiveData<JSONObject>()
        val error = MutableLiveData<VolleyError>()

        if (obj != null && IS_DEBUG) Log.d("12345", "$endpoint $obj")
        val addressUrl = "http://${StorageService.getInstance().ipAddress}:${StorageService.getInstance().port}/app/"
        val url = "$addressUrl$endpoint.php"
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            obj,
            { response ->
                if (IS_DEBUG) Log.d("12345", response.toString())
                if (isServerAvailable(response)) {
                    updateStatus(true)

                    data.postValue(response)
                } else {
                    updateStatus(false)
                    ToastHelper.showToast(
                        context,
                        "Server Error: pastikan server online dan koneksi wifi benar"
                    )
                }
            }
        ) { errorResponse ->
            if (IS_DEBUG) Log.d("12345", errorResponse.toString())
            updateStatus(false)
            error.postValue(errorResponse)
            ToastHelper.showToast(
                context,
                "Server Error: pastikan server online dan koneksi wifi benar"
            )
        }
        requestQueue?.add(request)
        return ResponseLiveData(data, error)
    }

    private fun updateStatus(isSuccess: Boolean) {
        _sfStatus.postValue(isSuccess)
    }

    private fun isServerAvailable(response: JSONObject): Boolean {
        val responseCode = response.getString("response")
        return (responseCode != ResponseCode.SERVER_ERROR)
    }

}