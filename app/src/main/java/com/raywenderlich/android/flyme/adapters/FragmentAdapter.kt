package com.raywenderlich.android.flyme.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.raywenderlich.android.flyme.fragments.HomeFragment
import com.raywenderlich.android.flyme.fragments.SecondFragment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxBus {

  private val publisher = PublishSubject.create<Any>()
  fun publish(event: Any) {
    publisher.onNext(event)
  }
  // Listen should return an Observable and not the publisher
  // Using ofType we filter only events that match that class type
  fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}


class FragmentAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

  override fun getItem(position: Int): Fragment? {
    when (position) {
      0 -> {
        return HomeFragment()
      }
      1 -> {
        return SecondFragment()
      }
      else -> return null
    }
  }

  override fun getCount(): Int {
    return totalTabs
  }
}