package com.cyberschnitzel.phonear

data class PhoneDataDTO(val DeviceName: String, val dimensions: String, val weight: String, val os: String, val cpu: String,
                        var previewImage: String, var frontImage: String?, var backImage: String?, var sideImage: String?,
                        var primary_: String?, var single: String? = null) {
    fun toPhoneData(): PhoneData {
        // Create size
        var w: Float
        var h: Float
        var d: Float
        var weightFloat: Float
        var camera: String?
        try {
            val dimensionsSplit = dimensions.split(" x ")
            h = dimensionsSplit[0].toFloat()
            w = dimensionsSplit[1].toFloat()
            d = dimensionsSplit[2].split(" mm ")[0].toFloat()


            // Create weight
            weightFloat = weight.split(" g")[0].toFloat()

            // Create camera
            camera = primary_
            if (camera == null) camera = single
            if (camera == null) camera = ""

        } catch (e: Exception) {
            w = 70f
            h = 143f
            d = 7.7f
            weightFloat = 169f
            camera = "12 MP"
        }
        return PhoneData(DeviceName, Size(w, h, d), weightFloat, os, cpu, camera!!, previewImage, frontImage, backImage, sideImage)
    }
}