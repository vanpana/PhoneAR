package com.cyberschnitzel.phonear

class ResponseDto(var data: String?)

var emptyResponseDto = ResponseDto("")
val timeoutResponseDto = ResponseDto("Timeout!")
val noConnectionResponseDto = ResponseDto("No connection")
val serverErrorResponseDto = ResponseDto("Server error!")
val authenticationFailureDto = ResponseDto("Authentication failed!")