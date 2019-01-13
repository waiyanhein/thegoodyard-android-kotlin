package com.thegoodyard.waiyanhein.thegoodyard

import android.Manifest
import android.content.Intent
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*
import kotlin.concurrent.schedule
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat



class InitialActivity : AppCompatActivity() {

    var REQUEST_CODE_LOCATION = 111
    lateinit var application : MainApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        application = getApplication() as MainApplication
        Timer().schedule(2000){
            if(!isLocationPermitted()) {
                requestLocation()
            }
            else {
                startMainActivity()
            }
        }
    }

    fun startMainActivity() {
        val intent = Intent(this, VrViewActivity::class.java);
        startActivity(intent);
    }

    fun requestLocation() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
    }

    fun isLocationPermitted() : Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_LOCATION) {
            if(!(permissions.size >0 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                finish()
            }
        }
        startMainActivity()
    }
}
