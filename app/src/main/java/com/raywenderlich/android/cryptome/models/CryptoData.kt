package com.raywenderlich.android.flyme.models

data class CryptoData(val name: String, val prices: List<Price>)
data class Price(val currency: String, val price: Float)