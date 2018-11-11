package com.cyberschnitzel.phonear

interface RequestHandler {
    fun onSuccessful(responseDto: ResponseDto)
    fun onFailed(responseDto: ResponseDto)
}