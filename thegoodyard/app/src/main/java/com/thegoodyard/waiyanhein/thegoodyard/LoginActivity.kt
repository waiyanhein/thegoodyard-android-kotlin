package com.thegoodyard.waiyanhein.thegoodyard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.amazonaws.mobile.auth.ui.SignInUI
import com.amazonaws.mobile.client.AWSMobileClient



class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        AWSMobileClient.getInstance().initialize(this) {
            val signin = AWSMobileClient.getInstance().getClient(
                    this@LoginActivity,
                    SignInUI::class.java) as SignInUI
            signin.login(
                    this@LoginActivity,
                    MainActivity::class.java).execute()
        }.execute()
    }
}