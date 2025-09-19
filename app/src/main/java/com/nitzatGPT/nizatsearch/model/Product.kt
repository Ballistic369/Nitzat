package com.nitzatGPT.nitzatsearch.model

data class Product(
    val name: String,
    val imageUrl: String?,
    val price: String,
    val discountPrice: String?,
    val description: String?,
    val link: String
)
