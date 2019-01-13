package com.thegoodyard.waiyanhein.thegoodyard

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.algolia.search.saas.CompletionHandler
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import kotlin.concurrent.thread
import com.algolia.search.saas.AlgoliaException
import com.algolia.search.saas.Query
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject




class ItemListFragment : Fragment() {

    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var contentView : View
    private lateinit var listAdapter: ItemsAdapter
    private lateinit var listItems : ArrayList<Any>
    private lateinit var application : MainApplication
    private lateinit var listLayoutManager: GridLayoutManager
    private var lastEvaluatedKey: Map<String?, AttributeValue?>? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(R.layout.fragment_item_list, container, false)
        initialize()
        setUpLocationHandler()
        setUpItemsView()
        searchItemsOnAlgolia()
        return contentView
    }

    private fun initialize()
    {
        listItems = ArrayList<Any>()
        application = activity?.getApplication() as MainApplication
        FetchItems().execute()
    }

    private fun setUpLocationHandler() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
        if (ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if(location != null) {
                    this.currentLatitude = location.latitude
                    this.currentLongitude = location.longitude
                }
            }
        }
    }


    private fun setUpItemsView() {
        listLayoutManager = GridLayoutManager(activity, 2)
        listAdapter = ItemsAdapter(application, listItems)
        contentView.rc_view_items.layoutManager = listLayoutManager
        contentView.rc_view_items.adapter = listAdapter
        listAdapter.setItemsAdapterListener(object : ItemsAdapter.ItemsAdapterListener{
            override fun onItemSelected(position: Int, item: ItemDO) {

            }
        });
        contentView.rc_view_items.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!listAdapter.isLoading)
                {
                    if(listLayoutManager.findLastCompletelyVisibleItemPosition() >= (listLayoutManager.itemCount - 1)
                        &&
                        listAdapter.lastLoadingPosition != listLayoutManager.findLastCompletelyVisibleItemPosition())
                    {
                        listAdapter.isLoading = true

                        listAdapter.isLoading = false
                        listAdapter.lastLoadingPosition = listLayoutManager.findLastCompletelyVisibleItemPosition()
                    }
                }
            }
        })
    }


    private fun searchItemsOnAlgolia() {
        val algoliaClient = MainApplication.getAlgoliaClient()
        val index = algoliaClient.getIndex(MainApplication.ALGOLIA_ITEM_INDEX_NAME)

        val completionHandler = CompletionHandler { content, error ->
            if(content == null)
            {
                Log.i("RESPONSE_NULL", "TRUE")
            }
            else
            {
                val hits = content.optJSONArray("hits")
                if(hits == null)
                {
                    Log.i("HIT_NULL", "TRUE")
                }
                else
                {
                    Log.i("HIT_LENGTH", hits.length().toString())
                    for (i in 0..hits.length()-1)
                    {
                        val hit = hits.optJSONObject(i);
                        Log.i("NAME", hit.optString("Name"))
                    }
                }
            }
            Log.i("SEARCH_COMPLETED", "COMPLETED")
        }

        index.searchAsync(Query(), completionHandler)
    }


    inner class FetchItems() : AsyncTask<Void, ArrayList<Any>, ArrayList<Any>>() {
        override fun doInBackground(vararg params: Void?): ArrayList<Any> {
            val dynamoDBClient = AmazonDynamoDBClient(AWSMobileClient.getInstance().credentialsProvider)
            val fetchedItems: ArrayList<Any> = ArrayList();

            val scanRequest = ScanRequest().withTableName(MainApplication.DB_TABLE_ITEMS);
            scanRequest.exclusiveStartKey = lastEvaluatedKey


            //val gson = Gson()
//        gson.fromJson("", Array<String>::class.java).asList()
            val scanResult = dynamoDBClient.scan(scanRequest)
            scanResult.items.forEach { item ->
                Log.i("ITEM_NAME", item.get("Name")?.s)
                val viewItem = ItemDO()
                viewItem.id = item.get("Id")?.s
                viewItem.description = item.get("Description")?.s
                viewItem.name = item.get("Name")?.s
                viewItem.userId = item.get("UserId")?.s
                viewItem.images = item.get("Images")?.ns
                viewItem.thumbnail = item.get("Thumbnail")?.s
                fetchedItems.add(viewItem)
                Log.i("IMAGES_COUNT", item.get("Images")?.ns?.size.toString())
            }
            Log.i("TOTAL_ITEMS", scanResult.items.size.toString())
            lastEvaluatedKey = scanResult.lastEvaluatedKey


//            val nameKeyCondition = Condition().withComparisonOperator(ComparisonOperator.CONTAINS.toString())
//                    .withAttributeValueList(AttributeValue().withS("M"));
//            val idKeyCondition = Condition().withComparisonOperator(ComparisonOperator.CONTAINS.toString())
//                    .withAttributeValueList(AttributeValue().withS("a"))
//            val keyConditions = HashMap<String, Condition>()
//            keyConditions["Name"] = nameKeyCondition
//            keyConditions["Id"] = idKeyCondition
//            val lastEvaluatedKey = null
//            val queryRequest = QueryRequest().withTableName(MainApplication.DB_TABLE_ITEMS).withKeyConditions(keyConditions)
//                    .withExclusiveStartKey(lastEvaluatedKey)
//            val queryResult = dynamoDBClient.query(queryRequest)
//            Log.i("QUERY_RESULT", queryResult.items.size.toString())

            return fetchedItems
        }

        override fun onPostExecute(result: ArrayList<Any>) {
            result.forEach { element ->
                val itemDO = element as ItemDO
                listItems.add(itemDO)
            }
            listAdapter.notifyDataSetChanged()
            super.onPostExecute(result)
        }
    }
}