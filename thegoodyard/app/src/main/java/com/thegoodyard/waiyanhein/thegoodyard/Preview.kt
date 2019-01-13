package com.thegoodyard.waiyanhein.thegoodyard

import android.graphics.Bitmap

class Preview (path: String, bitmap: Bitmap, bucketKey: String) {
    var path: String? = null
    var bitmap: Bitmap? = null
    var bucketKey: String? = null

    init {
        this.path = path
        this.bitmap = bitmap
        this.bucketKey = null
    }
}