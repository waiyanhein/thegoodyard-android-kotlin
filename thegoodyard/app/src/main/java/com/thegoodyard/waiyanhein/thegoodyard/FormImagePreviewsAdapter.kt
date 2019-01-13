package com.thegoodyard.waiyanhein.thegoodyard

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.widget_form_image_preview.view.*

class FormImagePreviewsAdapter (context: Context, listItems: ArrayList<Preview>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var listItems: ArrayList<Preview>
    private lateinit var context: Context

    init {
        this.listItems = listItems
        this.context = context
    }

    public class PreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagePreview : ImageView = view.item_form_image_preview
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val previewHolder = p0 as PreviewViewHolder
        val preview = listItems.get(p1)
        previewHolder.imagePreview.setImageBitmap(preview.bitmap)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return PreviewViewHolder(LayoutInflater.from(context).inflate(R.layout.widget_form_image_preview, p0, false))
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}