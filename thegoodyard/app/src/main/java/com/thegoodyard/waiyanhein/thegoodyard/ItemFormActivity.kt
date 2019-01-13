package com.thegoodyard.waiyanhein.thegoodyard

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.Permission
import com.esafirm.imagepicker.features.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_item_form.*
import org.jetbrains.anko.doAsync
import java.io.File
import java.lang.Exception
import java.util.*

class ItemFormActivity : AppCompatActivity() {
    private var pickFromCamera : Boolean = false
    private var previewListItems : ArrayList<Preview> = ArrayList<Preview>()
    private lateinit var previewsAdapter: FormImagePreviewsAdapter
    private lateinit var previewsLayoutManager: LinearLayoutManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_form)
        setUpLocationHandler()
        setUpViews()
        registerReceiver(SaveItemReceiver(), IntentFilter(MainApplication.ACTION_SAVE_ITEM_RECEIVER))
    }

    private fun setUpLocationHandler() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                       this.location = location
                    }
        }
    }

    private fun setUpViews() {
        item_form_btn_take_pictures.setOnClickListener(View.OnClickListener {
            pickFromCamera = true
            ImagePicker.cameraOnly().start(this)
        })

        item_form_btn_pick_images.setOnClickListener(View.OnClickListener {
            sendBroadcast(Intent(MainApplication.ACTION_SAVE_ITEM_RECEIVER))
            //pickFromCamera = false
            //ImagePicker.create(this).multi().start()
        })

        item_form_btn_save.setOnClickListener({ uploadImages() })

        setUpPreviewsView()
    }

    private fun setUpPreviewsView(){
        previewsAdapter = FormImagePreviewsAdapter(this, previewListItems)
        previewsLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        item_form_rc_view_previews.layoutManager = previewsLayoutManager
        item_form_rc_view_previews.adapter = previewsAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(ImagePicker.shouldHandle(requestCode, resultCode, data)){
            if(pickFromCamera){
                val image = ImagePicker.getFirstImageOrNull(data)
                previewListItems.add(Preview(image.path, BitmapFactory.decodeFile(image.path), ""))
                previewsAdapter.notifyDataSetChanged()

                uploadPhotoToS3(image.path)

            } else {
                val images = ImagePicker.getImages(data);
                Toast.makeText(applicationContext, images.size.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadPhotoToS3(path : String){
        doAsync {
            //AWSMobileClient.getInstance().initialize(applicationContext).execute()
            //AWSMobileClient.getInstance().credentialsProvider
            val s3Client = AmazonS3Client(BasicAWSCredentials(MainApplication.AWS_ACCESS_KEY,MainApplication.AWS_ACCESS_SECRET))
            val transferUtility = TransferUtility.builder()
                    .context(applicationContext)
                    .awsConfiguration(AWSMobileClient.getInstance().configuration)
                    .s3Client(s3Client)
                    .build()

            val fileKey = MainApplication.generateFileKey(path)
            val uploadObserver = transferUtility.upload(fileKey, File(path), CannedAccessControlList.PublicRead)

            uploadObserver.setTransferListener(object : TransferListener{
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if(TransferState.COMPLETED == state){
                        Log.i("UPLOAD_STATE", "COMPLETED")
                    } else {
                        Log.i("UPLOAD_STATE", "CHANGED")
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

                }

                override fun onError(id: Int, ex: Exception?) {
                    Log.i("UPLOAD_ERROR", "Unable to upload file")
                }
            })
        }
    }

    private fun uploadImages()
    {
        doAsync {
            for (i in 0..(previewListItems.size - 1))
            {
                val preview = previewListItems.get(i)
                val s3Client = AmazonS3Client(BasicAWSCredentials(MainApplication.AWS_ACCESS_KEY,MainApplication.AWS_ACCESS_SECRET))
                val transferUtility = TransferUtility.builder()
                        .context(applicationContext)
                        .awsConfiguration(AWSMobileClient.getInstance().configuration)
                        .s3Client(s3Client)
                        .build()

                val fileKey = MainApplication.generateFileKey(preview.path.toString())
                preview.bucketKey = fileKey
                previewListItems.set(i, preview)
                val uploadObserver = transferUtility.upload(fileKey, File(preview.path.toString()), CannedAccessControlList.PublicRead)

                uploadObserver.setTransferListener(object : TransferListener{
                    override fun onStateChanged(id: Int, state: TransferState?) {
                        if(TransferState.COMPLETED == state){
                            if(previewListItems.size - 1 == i)
                            {
                                saveItem()
                            }
                        } else {
                            Log.i("UPLOAD_STATE", "CHANGED")
                        }
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

                    }

                    override fun onError(id: Int, ex: Exception?) {
                        Log.i("UPLOAD_ERROR", "Unable to upload file")
                    }
                })
            }
            previewsAdapter.notifyDataSetChanged()
        }
    }

    private fun saveItem(){
        doAsync {
            val images = ArrayList<String>()
            for(preview in previewListItems)
            {
                images.add(preview.bucketKey.toString())
            }

            val cognitoCredentialsProvider = CognitoCachingCredentialsProvider(applicationContext, MainApplication.COGNITO_IDENTITY_POOL_ID, MainApplication.AWS_REGION)
            val dynamoDbClient = AmazonDynamoDBClient(AWSMobileClient.getInstance().credentialsProvider)
            var dbMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDbClient)
                    .awsConfiguration(AWSMobileClient.getInstance().configuration)
                    .build()

            val item = ItemDO()
            item.id = UUID.randomUUID().toString()
            item.name = item_form_tf_name.text.toString()
            item.description = item_form_tf_description.text.toString()
            item.userId = cognitoCredentialsProvider.cachedIdentityId
            item.images = images
            item.thumbnail = images.get(0)
            if(location!=null) {
                item.latitude = location?.latitude
                item.longitude = location?.longitude
            }
            dbMapper.save(item)
            Log.i("ITEM_SAVED", "ITEM_SAVED")
            finish()
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES  .O) {
            val name = "notification_name"
            val descriptionText = "description text"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notification_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun popNotification(){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES  .O) {
            var mBuilder = NotificationCompat.Builder(this, "notification_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Notification title")
                    .setContentText("This is the content of the notification")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(123, mBuilder.build())
            }
        } else {

        }
    }
}