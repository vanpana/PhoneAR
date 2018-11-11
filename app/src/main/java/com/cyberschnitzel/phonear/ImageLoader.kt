package com.cyberschnitzel.phonear

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

fun loadImage(context: Context, url: String, imageView: ImageView) {
    Glide.with(context).load(url).into(imageView)
}