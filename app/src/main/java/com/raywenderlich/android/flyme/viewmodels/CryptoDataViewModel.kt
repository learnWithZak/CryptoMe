package com.raywenderlich.android.flyme.viewmodels

import com.google.gson.internal.LinkedTreeMap
import com.raywenderlich.android.flyme.adapters.CryptoDataAdapter
import com.raywenderlich.android.flyme.helper.*
import com.raywenderlich.android.flyme.models.CryptoData
import com.raywenderlich.android.flyme.models.Price
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import timber.log.Timber

class CryptoDataViewModel(val cryptoDataRepository: CryptoDataRepository) {

    fun getCryptoData(currencies: String): Observable<List<CryptoData>> {
        return cryptoDataRepository.getCryptoData(currencies)
            .map {
                Timber.d("Mapping crypto data to UIData...")
                handleResult(it)
            }
            .onErrorReturn {
                Timber.d("An error occurred")
                arrayListOf<CryptoData>().toList()
            }
    }

    private fun handleResult(result: LinkedTreeMap<Object, Object>): List<CryptoData> {
        var cryptoData = ArrayList<CryptoData>()
        for (entry in result.entries) {
            val cryptoTitle = entry.key as String
            val priceMap = entry.value as LinkedTreeMap<String, Float>
            var prices = ArrayList<Price>()
            for (price in priceMap.entries) {
                val newPrice = Price(price.key, price.value)
                prices.add(newPrice)
            }

            val newCryptoData = CryptoData(cryptoTitle, prices.toList())
            cryptoData.add(newCryptoData)
        }

        return cryptoData
    }

    private fun handleResponse(cryptoDataList: List<CryptoData>) {

        System.out.println(cryptoDataList.toString())

    }
}