package com.thegoodyard.waiyanhein.thegoodyard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amazonaws.auth.AWSCognitoIdentityProvider
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.auth.core.IdentityManager
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.AWSStartupHandler
import com.amazonaws.mobile.client.AWSStartupResult
import kotlinx.android.synthetic.main.activity_main.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var application : MainApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        initialize()
        setUpVeiws()
    }

    private fun initialize()
    {
        application = getApplication() as MainApplication
    }


    fun setUpVeiws()
    {
        fab_post_item.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val userPool = CognitoUserPool(applicationContext, MainApplication.COGNITO_USER_POOL_ID, MainApplication.COGNITO_APP_CLIENT_ID, MainApplication.COGNITO_APP_CLIENT_SECRET)
                val user = userPool.getCurrentUser()
                if(userPool.currentUser.userId != null && !userPool.currentUser.userId.isEmpty())
                {
                    startItemFormActivity()
                }
                else
                {
                    startLoginActivity()
                }
            }
        })
    }

    fun isLollipopOrAbove(func: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            func()
        }
    }

    fun startItemFormActivity()
    {
        val intent = Intent(this, ItemFormActivity::class.java);
        startActivity(intent);
    }

    fun startLoginActivity()
    {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent)
    }

    class InfiniteScrollListener(val func: () -> Unit, val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener(){
    }
}