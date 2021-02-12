package com.tokopedia.filter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tokopedia.filter.R
import com.tokopedia.filter.model.LocationModel
import com.tokopedia.filter.view.ProductActivity
import kotlinx.android.synthetic.main.single_location_layout.view.*
import java.util.*


class LocationAdapter(private val context: Context, val locationList: MutableList<LocationModel>) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    private var row_index : Int? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_location_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(locationList[position], position)

    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(location: LocationModel, position: Int) {


            itemView.locationTxt.text = location.name
            itemView.checkBox.isChecked = location.checked

            itemView.setOnClickListener {
                itemView.checkBox.isChecked = true
                row_index = position
                location.checked = true
                clearCheck()
            }

            itemView.checkBox.setOnClickListener {
                itemView.checkBox.isChecked = true
                row_index = position
                location.checked = true
                clearCheck()
            }


            if (row_index == position) {
                itemView.checkBox.isChecked = true
                location.checked = true
            } else {
                itemView.checkBox.isChecked = false
                location.checked = false
            }

        }
    }


    private fun clearCheck() {
        if (context is ProductActivity) {
            context.clearCheck()
        }
    }



}