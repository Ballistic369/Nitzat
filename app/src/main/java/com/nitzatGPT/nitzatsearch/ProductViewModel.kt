package com.nitzatGPT.nitzatsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nitzatGPT.nitzatsearch.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repo = ProductRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            try {
                val results = repo.searchProducts(query)
                // If we found no items but the query looks like a product page, try fetchProductDetails
                if (results.isEmpty() && (query.startsWith("http") || query.contains("productslist.aspx"))) {
                    val single = repo.fetchProductDetails(query)
                    single?.let { _products.value = listOf(it) } ?: run { _products.value = emptyList() }
                } else {
                    _products.value = results
                }
            } catch (e: Exception) {
                _products.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}
