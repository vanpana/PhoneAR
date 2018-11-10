package com.cyberschnitzel.phonear

class Size(val w: Float, val h: Float, val d: Float)

class PhoneData(val phoneName: String, val size: Size, val os: String, val cpu: String,
                val camera: String) {
    companion object {
        private const val MAX_SCALE_COEFF = 3080
        private const val MIN_SCALE_COEFF = 10000
    }

    val maxScale: Float get() {
        return size.h / MAX_SCALE_COEFF
    }

    val minScale: Float get() {
        return size.h / MIN_SCALE_COEFF
    }
}