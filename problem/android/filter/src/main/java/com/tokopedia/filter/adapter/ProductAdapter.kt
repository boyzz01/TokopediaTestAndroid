package com.tokopedia.filter.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tokopedia.filter.R
import com.tokopedia.filter.model.Products
import com.tokopedia.filter.view.ProductActivity
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.product_item.view.*
import java.text.DecimalFormat

class ProductAdapter(private val context: Context, private val productList: MutableList<Products>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.bind(productList[position], position)

    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(product: Products, position: Int) {

            val df = DecimalFormat("###,###,###")
            itemView.title.text = product.name
            itemView.price.text = "Rp"+df.format(product.priceInt).replace(',','.')
            itemView.city.text = product.shop.city
            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.ic_baseline_broken_image_24)
            Glide.with(context).load(product.imageUrl).apply(options).into(itemView.productImage)


        }
    }

    fun filter(productCopy: List<Products>, cityFilter: MutableList<String>, priceFilter: MutableList<Float>) {

        productList.clear()
        val minPrice = priceFilter[0]
        val maxPrice = priceFilter[1]
        for (data in productCopy){
            if (cityFilter.isNotEmpty()){
                for (city in cityFilter){
                    if (data.shop.city==city && data.priceInt >=minPrice && data.priceInt <= maxPrice){
                        productList.add(data)
                    }
                }
            }else{
                if (data.priceInt in minPrice..maxPrice){
                    productList.add(data)
                }
            }
        }
        if (productList.isEmpty()){
            if (context is ProductActivity) {
                context.notFoundTxt.visibility = View.VISIBLE
            }
        }else{
            if (context is ProductActivity) {
                context.notFoundTxt.visibility = View.GONE
            }
        }
        notifyDataSetChanged()
    }


}