package com.cyberschnitzel.phonear

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class AutoCompletePhoneAdapter(var items: List<PhoneData>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    var autoCompletePhoneSelected: AutoCompletePhoneSelected? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.phone_autocomplete_item, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, pos: Int) {
        val phoneDataItem = items[pos]

        loadImage(context, phoneDataItem.previewImage, viewHolder.imageView)
        viewHolder.name.text = phoneDataItem.phoneName

        viewHolder.itemView.setOnClickListener {
            autoCompletePhoneSelected?.onPhoneSelected(phoneDataItem)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(new_items: List<PhoneData>) {
        items = new_items
        notifyDataSetChanged()
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val imageView = view.findViewById(R.id.image_view_flag) as ImageView
    val name = view.findViewById(R.id.text_view_name) as TextView
}