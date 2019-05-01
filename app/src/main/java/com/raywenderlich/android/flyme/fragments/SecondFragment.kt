package com.raywenderlich.android.flyme.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import com.raywenderlich.android.flyme.R
import com.raywenderlich.android.flyme.adapters.CryptoDataAdapter
import com.raywenderlich.android.flyme.helper.APIRequesting
import com.raywenderlich.android.flyme.models.CryptoData
import com.raywenderlich.android.flyme.models.Price
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SecondFragment: Fragment(), CryptoDataAdapter.Listener {

    private var cryptoDataAdapter: CryptoDataAdapter? = null
    private var myCompositeDisposable: CompositeDisposable? = null
    private val BASE_URL = "https://min-api.cryptocompare.com/"

    private lateinit var cryptocurrency_list: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_sport, container, false)

        myCompositeDisposable = CompositeDisposable()
        initRecyclerView(view)
        loadData()

        return view
    }

    private fun initRecyclerView(view: View) {
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        cryptocurrency_list = view!!.findViewById(R.id.cryptocurrency_list)
        cryptocurrency_list.layoutManager = layoutManager
    }


    private fun loadData() {

        val requestInterface = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(APIRequesting::class.java)

        myCompositeDisposable?.add(requestInterface.getData("SGD,CNY,JPY,KRW")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError { error -> System.err.println("Error: ${error.message}") }
            .subscribe(this::handleResult)
        )
    }

    private fun handleResult(result: LinkedTreeMap<Object, Object>) {
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

        handleResponse(cryptoData)
    }

    private fun handleResponse(cryptoDataList: List<CryptoData>) {

        cryptoDataAdapter = CryptoDataAdapter(ArrayList(cryptoDataList), this)
        cryptocurrency_list.adapter = cryptoDataAdapter

    }

    override fun onItemClick(cryptoData: CryptoData) {
        Toast.makeText(activity, "You clicked: ${cryptoData.name}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        myCompositeDisposable?.clear()
    }

}