package com.heronet.sellnetbeta.web

import com.heronet.sellnetbeta.model.Product
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SellnetApi {
    @GET("products/all")
    suspend fun getProducts(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<List<Product>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: String): Product
}