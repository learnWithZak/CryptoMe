package com.raywenderlich.android.flyme.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.os.health.SystemHealthManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.raywenderlich.android.flyme.App
import com.raywenderlich.android.flyme.DetailActivity
import com.raywenderlich.android.flyme.R
import com.raywenderlich.android.flyme.adapters.CryptoDataAdapter
import com.raywenderlich.android.flyme.models.CryptoData
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.flowable.FlowableReplay.observeOn
import io.reactivex.internal.util.HalfSerializer.onError
import io.reactivex.plugins.RxJavaPlugins.onError
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

open class BaseFragment : Fragment(), CryptoDataAdapter.Listener, SwipeRefreshLayout.OnRefreshListener  {

    private var cryptoDataAdapter: CryptoDataAdapter? = null

    private val viewModel = App.injectCryptoDataViewModel()
    private val disposables = CompositeDisposable()

    private lateinit var cryptocurrencyList: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private val INITIAL_DELAY_IN_MILLISECONDS: Long = 1000
    private var INTERVAL_IN_MILLISECONDS: Long = 10000

    private var currencies: String = ""

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            currencies = bundle.getString("currencies")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        readBundle(getArguments())

        initRecyclerView(view)

        return view
    }

    override fun onStart() {
        super.onStart()

        loadData()

        Timber.d("OnStart, LoadData")
    }

    override fun onResume() {
        super.onResume()

        loadData()

        Timber.d("onResume, LoadData")
    }

    override fun onRefresh() {
        loadData()

        Timber.d("onRefresh, LoadData")
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()

        Timber.d("onPause, Clear Disposables")
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()

        Timber.d("onStop, Clear Disposables")
    }

    private fun initRecyclerView(view: View) {
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        cryptocurrencyList = view.findViewById(R.id.cryptocurrency_list)
        cryptocurrencyList.layoutManager = layoutManager

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark)
    }

    private fun loadData() {

        Timber.d("Downloading Data ...")

        val disposable = Observable.interval(INITIAL_DELAY_IN_MILLISECONDS, INTERVAL_IN_MILLISECONDS,
            TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updateCryptoData, this::onError)

        Timber.d("Disposable added!")

        disposables.add(disposable)

    }

    private fun onError(throwable: Throwable) {
        Timber.d("OnError in Observable Time")
    }

    private fun updateCryptoData(aLong: Long) {

        mSwipeRefreshLayout.isRefreshing = true

        val observable: Observable<List<CryptoData>> = viewModel.getCryptoData(currencies)
        observable.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("Received UIModel $it users.")
                    handleResponse(it)
                }, {
                    handleError(it)
                })
    }

    private fun handleError(t: Throwable) {
        Timber.e(t)
    }

    private fun handleResponse(cryptoDataList: List<CryptoData>) {

        cryptoDataAdapter = CryptoDataAdapter(ArrayList(cryptoDataList), this)
        cryptocurrencyList.adapter = cryptoDataAdapter

        mSwipeRefreshLayout.isRefreshing = false

        Timber.d("We have ${disposables.size()} disposables")
    }

    override fun onItemClick(cryptoData: CryptoData) {
        val intent = Intent(activity, DetailActivity::class.java)
        startActivity(intent)
    }

}