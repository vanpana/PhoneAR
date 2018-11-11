package com.cyberschnitzel.phonear

class Size(val w: Float, val h: Float, val d: Float)

class PhoneData(val phoneName: String, val size: Size, val weight: Float, var os: String? = "Android", var cpu: String? = "Snapdragon",
                val camera: String,
                var previewImage: String = "https://cdn2.gsmarena.com/vv/bigpic/lg-google-nexus-5-.jpg",
                var frontImage: String? = null, var backImage: String? = null, var sideImage: String? = null) {

    companion object {
        private const val MAX_SCALE_COEFF = 3080
        private const val MIN_SCALE_COEFF = 10000
    }

    init {


    }

    val maxScale: Float
        get() {
            return size.h / MAX_SCALE_COEFF
        }

    val minScale: Float
        get() {
            return size.h / MIN_SCALE_COEFF
        }

    val hasImages: Boolean
        get() {
        return frontImage != null && frontImage != ""
        }
}