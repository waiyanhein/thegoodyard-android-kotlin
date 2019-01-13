package com.thegoodyard.waiyanhein.thegoodyard

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import kotlinx.android.synthetic.main.activity_vr_view.*
import android.R.attr.src
import android.graphics.Bitmap
import org.jetbrains.anko.doAsync
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class VrViewActivity : AppCompatActivity() {

    private val imageUrl = "https://s2.wp.com/wp-content/themes/vip/fbspherical/images/static/fb360-mpk-clean-rotated-sample.jpg"
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vr_view)
        downloadImage()
    }

    private fun loadPhotoSphere() {
        val options = VrPanoramaView.Options()

        try {
            val inputStream = assets.open("panorama.jpeg")
            options.inputType = VrPanoramaView.Options.TYPE_MONO
//            vrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeStream(inputStream), options)
//            inputStream.close()
            vrPanoramaView.loadImageFromBitmap(bitmap, options);
        } catch (e: Exception) {
            Log.i("ERROR", "ERROR IN LOADING IMAGE")
        }
    }

    private fun downloadImage() {
        doAsync {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.connect()
                val input = connection.getInputStream()
                bitmap = BitmapFactory.decodeStream(input)
                Log.i("DOWNLOAD_OK", "Download successful")
                loadPhotoSphere()
            } catch (e: IOException) {
                // Log exception
                Log.i("DOWNLOAD_ERROR", "Error in loading image from the URL")
            }
        }

    }

    override fun onPause() {
        super.onPause()
        vrPanoramaView.pauseRendering()
    }

    override fun onResume() {
        super.onResume()
        vrPanoramaView.resumeRendering()
    }

    override fun onDestroy() {
        vrPanoramaView.shutdown()
        super.onDestroy()
    }
}