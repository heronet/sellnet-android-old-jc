package com.heronet.sellnetbeta.data

import android.util.Log
import com.heronet.sellnetbeta.model.Product
import com.heronet.sellnetbeta.util.Resource
import com.heronet.sellnetbeta.web.ApiResponse
import com.heronet.sellnetbeta.web.SellnetApi
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val api: SellnetApi) {
    suspend fun getProducts(pageNumber: Int, pageSize: Int): Resource<ApiResponse<List<Product>>> {
        val productsResponse = try {
            api.getProducts(pageNumber, pageSize)
        } catch (e: HttpException) {
            return Resource.Error("An error occurred", null)
        } catch (e: Exception) {
            return Resource.Error("No Internet Connection", null)
        }
        return Resource.Success(productsResponse, null)
    }
}