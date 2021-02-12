package com.tokopedia.filter.view

import DataModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.chip.Chip
import com.google.android.material.slider.RangeSlider
import com.google.gson.Gson
import com.tokopedia.filter.R
import com.tokopedia.filter.adapter.LocationAdapter
import com.tokopedia.filter.adapter.ProductAdapter
import com.tokopedia.filter.model.LocationModel
import com.tokopedia.filter.model.Products
import com.tokopedia.filter.utils.getJsonData
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.filter_sheet_view.*
import kotlinx.android.synthetic.main.location_sheet_view.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ProductActivity : AppCompatActivity(), View.OnClickListener, RangeSlider.OnChangeListener {


    lateinit var productAdapter  : ProductAdapter
    private lateinit var productList: MutableList<Products>
    private var productCopy = mutableListOf<Products>()

    private var cityMap: HashMap<String, Int> = HashMap()
    private var prices = mutableListOf<Int>()
    private var city: MutableList<LocationModel> = ArrayList<LocationModel>()
    private var cityCopy = mutableListOf<LocationModel>()

    private var priceFilter = mutableListOf<Float>()
    var cityFilter = mutableListOf<String>()


    private lateinit var filterSheetBehavior: BottomSheetBehavior<RelativeLayout>
    lateinit var locationSheetBehavior: BottomSheetBehavior<RelativeLayout>

    private val df = DecimalFormat("###,###,###")

    private lateinit var adapterLocation : LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        init()
        getProduct()
        getFrequentCityAndPrice()

    }


    private fun getFrequentCityAndPrice() {

        if (!productList.isNullOrEmpty()){
            for (product in productList){
                val key : String = product.shop.city
                if (cityMap.containsKey(key)){
                    val count : Int = cityMap[key]!! +1
                    cityMap[key] = count
                } else{
                    cityMap[key] = 1
                }

                if (!prices.contains(product.priceInt)){
                    prices.add(product.priceInt)
                }
            }
            val resultMap = cityMap.toList()
                    .sortedByDescending { (_, value) -> value }
                    .toMap()



            for (entry in resultMap) {
                val model = LocationModel(entry.key, false)
                city.add(model)
            }

            cityCopy.addAll(city)
            adapterLocation = LocationAdapter(this, city)
            val max = Collections.max(prices)
            val min = Collections.min(prices)
            showFrequentCity(city)
            setPriceRange(min, max)
        }
    }


    private fun showFrequentCity(city: MutableList<LocationModel>) {

        //change the number to show more frequent city
        val n = 2

        for (i in 0 until n){
            if (!city[i].checked){
                val chipCity = this.layoutInflater.inflate(R.layout.single_chip_layout, chipGroup, false) as Chip
                chipCity.text= city[i].name
                chipGroup.addView(chipCity)
                chipCity.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked){
                        cityFilter.add(chipCity.text.toString())
                        adapterLocation.locationList[i].checked = true
                    }
                    else{
                        cityFilter.remove(chipCity.text.toString())
                        adapterLocation.locationList[i].checked = false
                    }
                }
            }
        }


        val chipOther = this.layoutInflater.inflate(R.layout.single_chip_layout, chipGroup, false) as Chip
        chipOther.text= "Other"
        chipOther.isCheckable = false
        chipOther.isClickable = true
        chipGroup.addView(chipOther)

        chipOther.setOnClickListener { showLocationList() }
    }



    private fun setPriceRange(min: Int, max: Int) {
        priceSlider.values = arrayListOf(min.toFloat(), max.toFloat())
        priceSlider.valueFrom = min.toFloat()
        priceSlider.valueTo = max.toFloat()
    }

    private fun getProduct() {

        val jsonString = getJsonData(applicationContext, "products")
        if (jsonString.isNullOrEmpty()){
            Toast.makeText(this, "Failed To Load Data", Toast.LENGTH_SHORT).show()
        } else{
            val dataObject = JSONObject(jsonString).getJSONObject("data")
            val data: DataModel = Gson().fromJson(dataObject.toString(), DataModel::class.java)

            productList = data.products
            productCopy.addAll(productList)
            productAdapter  = ProductAdapter(this, productList)
            rv_product.adapter = productAdapter

        }

    }

    private fun init() {

        rv_product.layoutManager = StaggeredGridLayoutManager(2, 1)
        rv_location.layoutManager = LinearLayoutManager(this)
        rv_location.addItemDecoration(
                DividerItemDecoration(
                        this,
                        LinearLayoutManager.VERTICAL
                )
        )

        filterSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        locationSheetBehavior = BottomSheetBehavior.from(locationSheetView)


        darkerBg.setOnClickListener(this)
        darkerBg2.setOnClickListener(this)
        priceSlider.addOnChangeListener(this)
        fab.setOnClickListener(this)
        filterBtn.setOnClickListener(this)
        resetTxt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)




        filterSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    darkerBg.visibility = View.GONE
                    fab.visibility = View.VISIBLE
                } else {
                    fab.visibility = View.GONE
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                darkerBg.visibility = View.VISIBLE
                darkerBg.alpha = slideOffset
            }
        })
        locationSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    darkerBg2.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                darkerBg2.visibility = View.VISIBLE
                darkerBg2.alpha = slideOffset
            }
        })



    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.darkerBg -> {
                filterSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
            R.id.darkerBg2 -> {
                locationSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
            R.id.fab -> {

                if (filterSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    filterSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    initFilter()
                } else {
                    filterSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            R.id.filterBtn -> {
                priceFilter = priceSlider.values
                filterSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                productAdapter.filter(productCopy, cityFilter, priceFilter)
            }
            R.id.resetTxt -> {
                chipGroup.clearCheck()
                cityFilter.clear()
                priceFilter.clear()
                initFilter()
            }
            R.id.saveBtn -> {
                locationSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                showSavedCity()
            }
        }
    }

    private fun showSavedCity() {

        chipGroup.removeAllViews()

        for (i in 0 until adapterLocation.locationList.size){
            if (adapterLocation.locationList[i].checked){
                println(adapterLocation.locationList[i].name)
                val savedCity = this.layoutInflater.inflate(R.layout.single_chip_layout, chipGroup, false) as Chip
                savedCity.text= adapterLocation.locationList[i].name
                cityFilter.clear()
                cityFilter.add(savedCity.text.toString())
                chipGroup.addView(savedCity)
                chipGroup.check(savedCity.id)
                savedCity.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked){
                        cityFilter.add(savedCity.text.toString())
                        adapterLocation.locationList[i].checked = true
                    }
                    else{
                        cityFilter.remove(savedCity.text.toString())
                        adapterLocation.locationList[i].checked = false
                    }

                }
            }

        }
        showFrequentCity(city)
    }

    private fun initFilter() {
        val max = Collections.max(prices)
        val min = Collections.min(prices)
        if(priceFilter.isEmpty())
            priceSlider.values = arrayListOf(min.toFloat(), max.toFloat())
        else{
            priceSlider.values = arrayListOf(priceFilter[0], priceFilter[1])

        }

    }

    private fun showLocationList() {
        locationSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        rv_location.adapter = adapterLocation
    }

    override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
        val min : Int = slider.values[0].toInt()
        val max : Int = slider.values[1].toInt()
        lowPrice.setText(df.format(min).replace(',', '.'))
        highPrice.setText(df.format(max).replace(',', '.'))
        priceFilter = priceSlider.values
    }

    fun clearCheck(){
        rv_location.post(Runnable { adapterLocation.notifyDataSetChanged() })
    }



}



