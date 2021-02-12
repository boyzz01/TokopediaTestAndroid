package com.tokopedia.maps.view

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonParser
import com.tokopedia.maps.R
import com.tokopedia.maps.response.ApiResponse
import com.tokopedia.maps.service.ApiClient
import com.tokopedia.maps.service.ApiInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.*

@Suppress("DEPRECATION")
open class MapsActivity : AppCompatActivity() {

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null

    private lateinit var textCountryName: TextView
    private lateinit var textCountryCapital: TextView
    private lateinit var textCountryPopulation: TextView
    private lateinit var textCountryCallCode: TextView

    private var editText: EditText? = null
    private var buttonSubmit: View? = null
    private var loading: ProgressDialog? = null

    private val TAG = MapsActivity::class.java
    private var mCompositeDisposable: CompositeDisposable? = null

    private var found: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        bindViews()
        initListeners()
        loadMap()
    }

    private fun bindViews() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        editText = findViewById(R.id.editText)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        textCountryName = findViewById(R.id.txtCountryName)
        textCountryCapital = findViewById(R.id.txtCountryCapital)
        textCountryPopulation = findViewById(R.id.txtCountryPopulation)
        textCountryCallCode = findViewById(R.id.txtCountryCallCode)
    }

    private fun initListeners() {
        buttonSubmit!!.setOnClickListener {
            // TODO
            // search by the given country name, and
            // 1. pin point to the map
            // 2. set the country information to the textViews.


            //get location text from editText
            val location = editText?.text.toString()

            searchLocation(location)

        }

        editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                hideKeyboard(v)
            }
        }
    }

    private fun searchLocation(location: String) {
        //TODO("connect to RAPID API and search for the country data by country name")

        loading = ProgressDialog.show(this, null, "Mencari Data...", true, false)

        mCompositeDisposable = CompositeDisposable()

        val apiInterface : ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        mCompositeDisposable?.add(apiInterface.getByCountryName(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(response: List<ApiResponse>) {
        //TODO("Handle the successful response from the API")
        val countryName = editText?.text.toString()

        for(data in response) {
            //check if the country name that we search is same with the country name from the API data
            if (countryName.toLowerCase(Locale.getDefault()) == data.name.toLowerCase(Locale.getDefault())){
                found = true

                loading!!.dismiss()
                setInformation(data)
                pinPointToMap(data.nativeName, data.latlng[0], data.latlng[1])
                break
            } else{
                //if we cannot find it in the country name we check in the alternative spelling of the country name in the API data
                for (altSpelling in data.altSpellings){
                    if (countryName.toLowerCase(Locale.getDefault()) == altSpelling.toLowerCase(Locale.getDefault())){
                        found = true
                        loading!!.dismiss()
                        setInformation(data)
                        pinPointToMap(data.nativeName, data.latlng[0], data.latlng[1])
                        break
                    }
                    else{
                        found = false
                    }
                }
                if (found) break
            }
        }

        loading!!.dismiss()
        if (!found) showDialog("Country not found, Please use english or native country name")

    }

    private fun handleError(error: Throwable) {
        //TODO("Handle the error response from the API")

        var message = "Error"
        loading!!.dismiss()
        if (error is HttpException) {
            val data = error.response()?.errorBody()?.string()
            message = JsonParser().parse(data).asJsonObject["message"].asString

            if (message.toLowerCase().contains("not found")){
                showDialog("Country not found, Please use english or native country name")
            } else{
                showDialog("Error : " + message)
            }

        }
        else{
            message = error.localizedMessage
            showDialog(message)
        }
        Log.d("$TAG", error.localizedMessage)
    }



    private fun setInformation(country: ApiResponse) {
        //TODO("Set information that we got from the API to the textview ")
        textCountryName.text = "Nama negara: " + country.nativeName
        textCountryCapital.text = "Ibukota: " + country.capital
        textCountryPopulation.text ="Jumlah penduduk: " + country.population
        textCountryCallCode.text = "Kode telepon: " + country.callingCodes[0]
    }



    private fun pinPointToMap(location: String, lat: Double, lng: Double) {
        //TODO("pin point to the map")
        val latLng = LatLng(lat, lng)
        googleMap!!.clear()
        googleMap!!.addMarker(MarkerOptions()
                .position(latLng).title(location)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_position)))
        googleMap!!
                .animateCamera(CameraUpdateFactory
                        .newLatLngZoom(latLng, 3.0F))

    }

    fun loadMap() {
        mapFragment!!.getMapAsync { googleMap -> this@MapsActivity.googleMap = googleMap }
    }

    private fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setPositiveButton("OK", dialogClickListener)
                .setCancelable(false).show()
    }

    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                dialog.dismiss()
            }
        }
    }



    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}
