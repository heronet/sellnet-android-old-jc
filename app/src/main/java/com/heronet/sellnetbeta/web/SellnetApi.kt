package com.heronet.sellnetbeta.web

import com.heronet.sellnetbeta.model.AuthData
import com.heronet.sellnetbeta.model.Product
import retrofit2.http.*

interface SellnetApi {
    @GET("products/all")
    suspend fun getProducts(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<List<Product>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: String): Product

    @POST("account/login")
    suspend fun login(@Body loginDto: LoginDto): AuthData

    @POST("account/refresh")
    suspend fun refreshToken(@Header("Authorization") authorization: String, @Body authData: AuthData): AuthData
}