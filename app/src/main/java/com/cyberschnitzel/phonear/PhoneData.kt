package com.cyberschnitzel.phonear

import com.google.ar.sceneform.math.Vector3

class Size(val w: Float, val h: Float, val d: Float) {
    fun vec3(): Vector3 = Vector3(w, h, d)
}

class PhoneData(val phoneName: String, val size: Size, val weight: Float, var os: String? = "Android", var cpu: String? = "Snapdragon",
                val camera: String,
                var previewImage: String = "https://cdn2.gsmarena.com/vv/bigpic/lg-google-nexus-5-.jpg",
                var frontImage: String? = null, var backImage: String? = null, var sideImage: String? = null) {

    val hasImages: Boolean
        get() {
        return frontImage != null && frontImage != ""
        }
}