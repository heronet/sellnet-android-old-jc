package com.heronet.sellnetbeta.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heronet.sellnetbeta.data.ProductsRepository
import com.heronet.sellnetbeta.model.Product
import com.heronet.sellnetbeta.util.Constants.PAGE_SIZE
import com.heronet.sellnetbeta.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    var products = mutableStateOf<List<Product>>(listOf())
    var productsCount = mutableStateOf(0)
    var isLoading = mutableStateOf(false)
    var uploadFinished = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    private var currentPage = 1

    init {
        getProducts()
    }

    fun getProducts(
        name: String? = null,
        city: String? = null,
        division: String? = null,
        category: String? = null,
        sortParam: String? = null,
        sellerId: String? = null,
        isFiltering: Boolean? = false
    ) {
        viewModelScope.launch {
            isLoading.value = true

            // Check if filtering.
            if (isFiltering == true) currentPage = 1

            when (val response = repository.getProducts(
                currentPage,
                PAGE_SIZE,
                name,
                city,
                division,
                category,
                sortParam,
                sellerId
            )) {
                is Resource.Error -> {
                    errorMessage.value = response.message!!
                    isLoading.value = false
                }
                is Resource.Success -> {
                    productsCount.value = response.data!!.size
                    errorMessage.value = ""
                    if (isFiltering == true) // If request is filter query, set products to response
                        products.value = response.data.data
                    else // Used for pagination. Request may or may not be filtered.
                        products.value += response.data.data
                    ++currentPage
                    isLoading.value = false
                }
            }
        }
    }

    suspend fun getProduct(id: String): Resource<Product> {
        return repository.getProduct(id)
    }

    fun addProduct(
        name: String,
        price: String,
        description: String,
        category: String,
        uris: List<Uri?>,
        token: String
    ) {
        isLoading.value = true
        viewModelScope.launch {
            val images = mutableListOf<MultipartBody.Part>()
            for (uri in uris) {
                withContext(Dispatchers.IO) {
                    val photo =
                        context.contentResolver.openInputStream(uri!!)?.buffered()?.readBytes()
                    val photoFile = RequestBody.create(MediaType.parse("image/*"), photo!!)
                    val formData = MultipartBody.Part.createFormData("photos", "photos", photoFile)
                    images.add(formData)
                }
            }
            val productName = RequestBody.create(MultipartBody.FORM, name)
            val productPrice = RequestBody.create(MultipartBody.FORM, price)
            val productDescription = RequestBody.create(MultipartBody.FORM, description)
            val productCategory = RequestBody.create(MultipartBody.FORM, category)

            when (val response = repository.addProduct(
                productName,
                productPrice,
                productDescription,
                productCategory,
                images,
                "Bearer $token"
            )) {
                is Resource.Error -> {
                    errorMessage.value = response.message!!
                    isLoading.value = false
                    uploadFinished.value = false
                }
                is Resource.Success -> {
                    errorMessage.value = ""
                    isLoading.value = false
                    uploadFinished.value = true // True will redirect away from AddProduct Screen
                }
            }
        }
    }

    // Reset upload status for using with subsequent uploads.
    fun resetUploadStatus() {
        uploadFinished.value = false
    }

    // Reset products for query
    fun resetProducts() {
        products.value = listOf()
    }
}