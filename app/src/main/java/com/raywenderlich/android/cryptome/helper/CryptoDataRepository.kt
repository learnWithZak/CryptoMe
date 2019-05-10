package com.raywenderlich.android.flyme.helper

import com.google.gson.internal.LinkedTreeMap
import io.reactivex.Observable
import timber.log.Timber

class CryptoDataRepository(val cryptoDataAPI: CryptoDataAPI) {

    fun getCryptoData(currencies: String): Observable<LinkedTreeMap<Object, Object>> {
        return cryptoDataAPI.getCryptoData(currencies)
            .doOnNext {
            Timber.d("Dispatching ${it.size} crypto data from API...")
        }
    }
}
