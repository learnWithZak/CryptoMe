package com.raywenderlich.android.flyme.fragments

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Bundle
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
import com.raywenderlich.android.flyme.R
import com.raywenderlich.android.flyme.adapters.CryptoDataAdapter
import com.raywenderlich.android.flyme.models.CryptoData
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.flowable.FlowableReplay.observeOn
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

open class BaseFragment : Fragment(), CryptoDataAdapter.Listener, SwipeRefreshLayout.OnRefreshListener  {

    private var cryptoDataAdapter: CryptoDataAdapter? = null

    private val viewModel = App.injectCryptoDataViewModel()
    private val disposables = CompositeDisposable()

    private lateinit var cryptocurrency_list: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var autoRefreshButton: ImageButton

    private var currencies: String = ""

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            currencies = bundle.getString("currencies")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        readBundle(getArguments())

        initFAB()
        initRecyclerView(view)

        return view
    }

    override fun onStart() {
        super.onStart()

        loadData()
    }

    override fun onRefresh() {
        loadData()
    }

    fun subscribe(disposable: Disposable): Disposable {
        disposables.add(disposable)
        return disposable
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun initFAB() {
        autoRefreshButton = activity!!.findViewById(R.id.autoRefreshButton)
        autoRefreshButton.isSelected = false
        updateButtonStyle()

        autoRefreshButton.setOnClickListener {
            toggleButton()
            updateButtonStyle()
        }
    }

    private fun initRecyclerView(view: View) {
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        cryptocurrency_list = view.findViewById(R.id.cryptocurrency_list)
        cryptocurrency_list.layoutManager = layoutManager

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark)
    }

    private fun loadData() {

        mSwipeRefreshLayout.isRefreshing = true

        var scheduler = Observable.interval(0, 5, TimeUnit.SECONDS)

        val disposable = scheduler.timeInterval()
            .subscribe(viewModel.getCryptoData(currencies)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Received View Model Data: $it ")
                handleResponse(it)
            }, {
                Timber.w(it)
            }))

        scheduler
    }

    private fun handleResponse(cryptoDataList: List<CryptoData>) {

        cryptoDataAdapter = CryptoDataAdapter(ArrayList(cryptoDataList), this)
        cryptocurrency_list.adapter = cryptoDataAdapter

        mSwipeRefreshLayout.isRefreshing = false
    }

    override fun onItemClick(cryptoData: CryptoData) {
        Toast.makeText(activity, "You clicked: ${cryptoData.name}", Toast.LENGTH_LONG).show()
    }

    private fun toggleButton() {
        autoRefreshButton.isSelected = !autoRefreshButton.isSelected
    }

    private fun updateButtonStyle() {
        autoRefreshButton.alpha = if (autoRefreshButton.isSelected) 1.0F else 0.2F
    }
}