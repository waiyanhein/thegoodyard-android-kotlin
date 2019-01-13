package com.thegoodyard.waiyanhein.thegoodyard

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.algolia.search.saas.Client
import com.amazonaws.regions.Regions
import java.io.File
import java.util.*

class MainApplication : Application() {

    private lateinit var localStorage: SharedPreferences


    companion object {
        val AWS_REGION = Regions.US_EAST_1
        //arn - arn:aws:cognito-idp:eu-west-2:206813362306:userpool/eu-west-2_W54GrUfU6
        val COGNITO_IDENTITY_POOL_ID = "us-east-1:f2d4353d-c684-4a6e-bc6a-c1c3e1cdbb06";//"eu-west-2_W54GrUfU6";
        val COGNITO_USER_POOL_ID = "us-east-1_yEfqiVMWJ"
        val COGNITO_APP_CLIENT_ID = "7l1ql4qt0pr56g7nmtqtf55l6o"
        val COGNITO_APP_CLIENT_SECRET = "1d1b7sdb46ipu43ld021qpe7fd0t7rpdrm63bgm1l1uuemcu0otc"
        val PF_NAME_LOCAL_STORAGE = "local_storage"
        val PF_KEY_IS_LOGGED_IN = "is_logged_in"
        val DB_TABLE_ITEMS  = "thegoodyardandroid-mobilehub-330286608-item"
        val S3_BUCKET_NAME = "thegoodyardandroid-userfiles-mobilehub-330286608";
        val AWS_ACCESS_KEY = "AKIAJOTQSEJ3XCA2NYYA"
        val AWS_ACCESS_SECRET = "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"
        val ACTION_SAVE_ITEM_RECEIVER = "com.thegoodyard.waiyanhein.thegoodyard.save_item"
        val BUCKET_BASE_URL = "https://s3.amazonaws.com/thegoodyardandroid-userfiles-mobilehub-330286608/"
        val ALGOLIA_APPLICATION_ID = "AFRKS8Y408"
        val ALGOLIA_SEARCH_ONLY_API_KEY = "ff04effbf733c8dc79c1b9e68fdd4011"
        val ALGOLIA_ITEM_INDEX_NAME = "items"


        fun getBucketFileUrl(path: String) : String
        {
            return BUCKET_BASE_URL + path
        }

        fun generateFileKey(path: String) : String
        {
            return "public/" + System.currentTimeMillis().toString() + UUID.randomUUID() + UUID.randomUUID() + "." + File(path).extension
        }

        fun getAlgoliaClient() : Client
        {
            return Client(ALGOLIA_APPLICATION_ID, ALGOLIA_SEARCH_ONLY_API_KEY)
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun getLocalStorage() : SharedPreferences
    {
        if(localStorage == null)
        {
            localStorage = getSharedPreferences(PF_NAME_LOCAL_STORAGE, Context.MODE_PRIVATE)
        }
        return localStorage
    }

    fun isLoggedIn() : Boolean
    {
        return getLocalStorage().getBoolean(PF_KEY_IS_LOGGED_IN, false)
    }
}