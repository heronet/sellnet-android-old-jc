package com.heronet.sellnetbeta.ui.navigation

import androidx.annotation.StringRes
import com.heronet.sellnetbeta.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Products: Screen("products", R.string.products)
}
