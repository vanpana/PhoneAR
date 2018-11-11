package com.cyberschnitzel.phonear

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Network(context: Context) {

    //<editor-fold desc="Generic network stuff">
    private val url = "http://api.phonear.codespace.ro/search?name="
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    private fun get(url: String, queryparam: String, requestHandler: RequestHandler) {
        var networkResponse: NetworkResponse? = null
        val request = object : StringRequest(Request.Method.GET, url + queryparam,
                Response.Listener { handleSuccess(networkResponse, requestHandler) },
                Response.ErrorListener { handleError(it, requestHandler) }) {

            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                networkResponse = response
                return super.parseNetworkResponse(response)
            }
        }
        addRequest(request)
    }

    private fun addRequest(request: StringRequest) {
        // Make request only send once
        request.retryPolicy = DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add request to queue
        requestQueue.add(request)
    }

    private fun handleSuccess(response: NetworkResponse?, requestHandler: RequestHandler) {
        val responseDto: ResponseDto = emptyResponseDto
        if (response != null) {
            // Build response dto from data
            responseDto.data = String(response.data)
        }
        requestHandler.onSuccessful(responseDto)
    }

    private fun handleError(volleyError: VolleyError, requestHandler: RequestHandler) {
        val responseDto: ResponseDto = when (volleyError) {
            is TimeoutError -> timeoutResponseDto
            is NoConnectionError -> noConnectionResponseDto
            is ServerError -> serverErrorResponseDto
            is AuthFailureError -> authenticationFailureDto
            else -> ResponseDto(volleyError.message!!)
        }

        requestHandler.onFailed(responseDto)
    }
    //</editor-fold>

    fun getPhonesByQuery(phoneName: String, requestHandler: RequestHandler) {
        if (phoneName.length >= 3) get(url, phoneName, requestHandler)
    }
}