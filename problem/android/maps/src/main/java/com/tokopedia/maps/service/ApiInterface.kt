package com.tokopedia.maps.service

import com.tokopedia.maps.response.ApiResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiInterface {

    @GET("{name}")
    fun getByCountryName(@Path("name") name: String): Observable<List<ApiResponse>>



}