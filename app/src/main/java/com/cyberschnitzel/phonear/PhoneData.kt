package com.cyberschnitzel.phonear

import com.google.ar.sceneform.math.Vector3

class Size(val w: Float, val h: Float, val d: Float) {
    fun vec3(): Vector3 = Vector3(w, h, d)
}

class PhoneData(val phoneName: String, val size: Size, val weight: Float, var os: String? = "Android", var cpu: String? = "Snapdragon",
                val camera: String,
                var previewImage: String = "https://cdn2.gsmarena.com/vv/bigpic/lg-google-nexus-5-.jpg",
                var frontImage: String? = null, var backImage: String? = null, var sideImage: String? = null) {


    fun hasImages(): Boolean {
        if (frontImage == null) {
            frontImage = "http://api.phonear.codespace.ro/storage/oneplus5t_front.png"
            backImage = "http://api.phonear.codespace.ro/storage/oneplus5t_back.png"
            sideImage = "http://api.phonear.codespace.ro/storage/oneplus5t_side.png"
        }
        return true
    }
}