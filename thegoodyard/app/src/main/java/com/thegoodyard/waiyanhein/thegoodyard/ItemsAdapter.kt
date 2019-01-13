package com.thegoodyard.waiyanhein.thegoodyard

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.widget_item.view.*

class ItemsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private lateinit var context: Context;
    private lateinit var items : List<Any>;
    private lateinit var listener: ItemsAdapterListener
    public var lastLoadingPosition : Int = -1
    public var isLoading : Boolean = false
    companion object {
        val VIEW_TYPE_ITEM = 1
        val VIEW_TYPE_LOADING = 2
        val LOADING_VIEW_IDENTIFIER = "loading-view"
    }

    constructor(pContext: Context, pItems : List<Any>){
        this.context = pContext;
        this.items = pItems;
    }

    class ItemViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val image: ImageView = view.item_image_thumbnail
        val name: TextView = view.item_tv_name
        val description: TextView = view.item_tv_description
        val container: LinearLayout = view.item_widget_container
    }

    class LoadingViewHolder (view: View) : RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if(p1 == VIEW_TYPE_LOADING)
        {
            return LoadingViewHolder(LayoutInflater.from(this.context).inflate(R.layout.widget_loading, p0, false));
        }
        else
        {
            return ItemViewHolder(LayoutInflater.from(this.context).inflate(R.layout.widget_item, p0, false));
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if(p0.itemViewType == VIEW_TYPE_ITEM)
        {
            val item: ItemDO = items.get(p1) as ItemDO
            val itemViewHolder: ItemViewHolder = p0 as ItemViewHolder
            itemViewHolder.name.setText(item.name)
            itemViewHolder.description.setText(item.description)
            itemViewHolder.container.setOnClickListener{ listener.onItemSelected(p1, item ) }
            val previewUrl = if (item.images!=null && item.images?.size as Int > 0) item.images?.get(0) else ""
//            Log.i("PREVIEW_COUNT", "C:" + item.images?.size.toString())
//            if(previewUrl.toString().isEmpty())
//            {
//                itemViewHolder.image.setImageResource(R.drawable.ic_launcher_background)
//            }
//            else
//            {
//                Picasso.get().load(previewUrl).error(R.drawable.ic_launcher_background).into(itemViewHolder.image);
//            }
            Picasso.get().load(MainApplication.getBucketFileUrl(item.thumbnail.toString())).error(R.drawable.ic_launcher_background).into(itemViewHolder.image);
        }
    }

    override fun getItemCount(): Int {
        return this.items.size;
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_ITEM
    }

    interface ItemsAdapterListener {
        public fun onItemSelected(position: Int, item: ItemDO)
    }

    public fun setItemsAdapterListener (adapterListener: ItemsAdapterListener) {
        this.listener = adapterListener
    }
}