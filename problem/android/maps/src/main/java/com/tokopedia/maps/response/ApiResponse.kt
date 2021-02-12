package com.tokopedia.maps.response


import com.google.gson.annotations.SerializedName

data class ApiResponse (

        @SerializedName("name") val name : String,
        @SerializedName("nativeName") val nativeName : String,
        @SerializedName("population") val population : Int,
        @SerializedName("capital") val capital : String,
        @SerializedName("callingCodes") val callingCodes : List<String>,
        @SerializedName("altSpellings") val altSpellings : List<String>,
        @SerializedName("latlng") val latlng : List<Double>

)

