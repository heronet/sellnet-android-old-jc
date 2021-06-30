package com.heronet.sellnetbeta.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heronet.sellnetbeta.data.ProductsRepository
import com.heronet.sellnetbeta.model.Product
import com.heronet.sellnetbeta.util.Constants.PAGE_SIZE
import com.heronet.sellnetbeta.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(private val repository: ProductsRepository): ViewModel() {
    var products = mutableStateOf<List<Product>>(listOf())
    var productsCount = mutableStateOf(0)
    var isLoading = mutableStateOf(false)
    var loadError = mutableStateOf("")
    private var currentPage = 1

    init {
        getProducts()
    }
    fun getProducts() {
        viewModelScope.launch {
            isLoading.value = true
            when(val response = repository.getProducts(currentPage, PAGE_SIZE)) {
                is Resource.Error -> {
                    loadError.value = response.message!!
                    isLoading.value = false
                }
                is Resource.Success -> {
                    productsCount.value = response.data!!.size
                    loadError.value = ""
                    isLoading.value = false
                    products.value += response.data.data
                    ++currentPage
                }
            }
        }
    }
    suspend fun getProduct(id: String): Resource<Product> {
        return repository.getProduct(id)
    }
}