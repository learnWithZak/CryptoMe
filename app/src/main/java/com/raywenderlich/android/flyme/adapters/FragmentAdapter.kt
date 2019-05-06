package com.raywenderlich.android.flyme.adapters

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.raywenderlich.android.flyme.fragments.BaseFragment
import com.raywenderlich.android.flyme.fragments.HomeFragment
import com.raywenderlich.android.flyme.fragments.SecondFragment

class FragmentAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

  override fun getItem(position: Int): BaseFragment? {
    when (position) {
      0 -> {
        return HomeFragment.newInstance("USD,CAD,EUR,BRL")
      }
      1 -> {
        return SecondFragment.newInstance("USD,CAD,EUR,BRL")
      }
      else -> return null
    }
  }

  override fun getCount(): Int {
    return totalTabs
  }
}