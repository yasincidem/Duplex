package com.yasincidem.duplex.common.constant

import androidx.annotation.DrawableRes
import com.yasincidem.duplex.R

val DefaultCountry = Country(R.drawable.flag_turkey, 90)

val Countries = listOf(
    DefaultCountry,
    Country(R.drawable.flag_germany, 49)
)

data class Country(
    @DrawableRes val flagDrawableRes: Int,
    val countryCode: Int,
) {
    fun getCountryCodeString(): String = "+ $countryCode"
}
