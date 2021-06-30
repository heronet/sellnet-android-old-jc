package com.heronet.sellnetbeta.data

import com.heronet.sellnetbeta.util.Constants.API_URL
import com.heronet.sellnetbeta.web.SellnetApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SellnetModule {
    @Singleton
    @Provides
    fun provideSellnetApi() = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(API_URL)
        .build()
        .create(SellnetApi::class.java)
}