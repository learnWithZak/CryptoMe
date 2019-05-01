package com.raywenderlich.android.flyme.helper

import com.google.gson.internal.LinkedTreeMap
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

const val APIKEY = "acf76c5e2691c604c81bc7f5cdcd9bc8f0d5ee356010b15fb9226209ce6307ea"

interface APIRequesting {
    @Headers("Authorization: $APIKEY")
    @GET("data/pricemulti?fsyms=BTC,ETH,LTC")
    fun getData(@Query("tsyms") currencies: String): Observable<LinkedTreeMap<Object, Object>>
}