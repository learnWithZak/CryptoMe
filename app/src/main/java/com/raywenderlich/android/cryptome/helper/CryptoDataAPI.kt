package com.raywenderlich.android.cryptome.helper

import com.google.gson.internal.LinkedTreeMap
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val APIKEY = "f1969177fff8de8f784ce67d18979e34b27d5c21f966b3600a20f236953a5e24"
const val BASEURL = "https://min-api.cryptocompare.com/"

interface CryptoDataAPI {
  @Headers("Authorization: $APIKEY")
  @GET("data/pricemulti?fsyms=BTC,ETH,LTC")
  fun getCryptoData(@Query("tsyms") currencies: String): Observable<LinkedTreeMap<Object, Object>>
}