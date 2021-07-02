package com.heronet.sellnetbeta.web

import com.heronet.sellnetbeta.model.AuthData
import com.heronet.sellnetbeta.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @POST("account/register")
    suspend fun register(@Body registerDto: RegisterDto): AuthData

    @POST("account/refresh")
    suspend fun refreshToken(@Header("Authorization") authorization: String, @Body authData: AuthData): AuthData

    @GET("utilities/locations")
    suspend fun getLocations(): Location

    @POST("products")
    @Multipart
    suspend fun addProduct(
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part photos: List<MultipartBody.Part>,
        @Header("Authorization") authorization: String
    )
}