package com.eurosportdemo.app.domain.model

data class Basket(
    val bookList: List<Book>,
    val isbnListPath: String,
    val offerList: List<Offer>,
    val originalPrice: Double,
    val bestOfferPrice: Double,
    val originalPriceFormatted: String,
    val bestOfferPriceFormatted: String
)