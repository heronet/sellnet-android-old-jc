package com.heronet.sellnetbeta.util

import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val API_URL = "https://sellnetx-ngmbdouzbq-uc.a.run.app/api/"
    const val PAGE_SIZE = 20

    val TOKEN by lazy { stringPreferencesKey("token") }
    val ID by lazy { stringPreferencesKey("id") }
    val NAME by lazy { stringPreferencesKey("name") }
    val ROLE by lazy { stringPreferencesKey("role") }
}