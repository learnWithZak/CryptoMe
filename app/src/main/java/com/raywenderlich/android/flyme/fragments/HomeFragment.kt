package com.raywenderlich.android.flyme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import com.raywenderlich.android.flyme.*
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class HomeFragment: Fragment(), CryptoDataAdapter.Listener, SwipeRefreshLayout.OnRefreshListener {

  private var cryptoDataAdapter: CryptoDataAdapter? = null
  private var myCompositeDisposable: CompositeDisposable? = null
  private val BASE_URL = "https://min-api.cryptocompare.com/"

  private lateinit var cryptocurrency_list: RecyclerView
  private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_home, container, false)

    myCompositeDisposable = CompositeDisposable()
    initRecyclerView(view)

    return view
  }

  override fun onResume() {
    super.onResume()

    loadData()
  }

  private fun initRecyclerView(view: View) {
    val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
    cryptocurrency_list = view.findViewById(R.id.cryptocurrency_list)
    cryptocurrency_list.layoutManager = layoutManager

    mSwipeRefreshLayout = view.findViewById(R.id.swipe_container)
    mSwipeRefreshLayout.setOnRefreshListener(this)
    mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
        android.R.color.holo_green_dark,
        android.R.color.holo_orange_dark,
        android.R.color.holo_blue_dark)

    mSwipeRefreshLayout.post {
      mSwipeRefreshLayout.isRefreshing = true

      // Fetching data from server
      loadData()
    }
  }

  override fun onRefresh() {
    loadData()
  }

  private fun loadData() {

    mSwipeRefreshLayout.isRefreshing = true

    val requestInterface = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(APIRequesting::class.java)

    myCompositeDisposable?.add(requestInterface.getData("USD,CAD,EUR,BRL")
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

    mSwipeRefreshLayout.isRefreshing = false
  }

  override fun onItemClick(cryptoData: CryptoData) {
    Toast.makeText(activity, "You clicked: ${cryptoData.name}", Toast.LENGTH_LONG).show()
  }

  override fun onDestroy() {
    super.onDestroy()

    myCompositeDisposable?.clear()
  }

}