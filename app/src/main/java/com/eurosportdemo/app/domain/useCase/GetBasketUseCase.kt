package com.eurosportdemo.app.domain.useCase

import android.text.TextUtils
import com.eurosportdemo.app.data.api.Webservice
import com.eurosportdemo.app.domain.model.Basket
import com.eurosportdemo.app.domain.repository.BaseRepository
import com.eurosportdemo.app.domain.repository.BookRepository
import com.eurosportdemo.app.domain.model.Book
import com.eurosportdemo.app.domain.model.Offer
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class GetBasketUseCase @Inject constructor(
    bookRepository: BookRepository,
    webservice: Webservice
) {

    var basket: Observable<Basket> =
        bookRepository
            .bookListInBasket
            .toObservable()
            .map { bookList ->
                val isbnListPath = TextUtils.join(",", bookList.map { book -> book.isbn })
                Basket(bookList, isbnListPath, ArrayList(), 0.0, 0.0, "", "")
            }
            .flatMap { basket ->
                if(basket.bookList.isNotEmpty()) {
                    webservice
                        .getOffers(basket.isbnListPath)
                        .map { response ->
                            var offerList: List<Offer> = ArrayList()
                            if (response.isSuccessful) {
                                response.body()?.offerList?.let {
                                    offerList = it
                                }
                            } else {
                                error.onNext(BaseRepository.RepositoryErrorEvent.UNKNOWN)
                            }
                            basket.copy(offerList = offerList)
                        }
                } else {
                    Observable.just(basket)
                }
            }
            .map { basket ->
                //calculate original and best offer prices
                basket.copy(
                    originalPrice = getOriginalPrice(basket.bookList),
                    bestOfferPrice = getBestOfferPrice(basket.bookList, basket.offerList)
                )
            }
            .map { basket ->
                //format original and best offer prices
                basket.copy(
                    originalPriceFormatted = formatPrice(basket.originalPrice),
                    bestOfferPriceFormatted = formatPrice(basket.bestOfferPrice)
                )
            }
            .distinctUntilChanged()

    private fun getBestOfferPrice(bookList: List<Book>, offerList: List<Offer>): Double {
        val originalPrice = getOriginalPrice(bookList)
        return offerList.map {
            when (it.type) {
                Offer.OfferType.PERCENTAGE -> originalPrice - originalPrice * it.value!! / 100
                Offer.OfferType.MINUS -> originalPrice - it.value!!
                Offer.OfferType.SLICE -> originalPrice - (originalPrice / it.sliceValue!!).toInt() * it.value!!
            }
        }.minOrNull() ?: 0.0
    }

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.FRENCH)
        format.maximumFractionDigits = 2
        format.minimumFractionDigits = 0
        format.currency = Currency.getInstance("EUR")
        return format.format(price)
    }

    private fun getOriginalPrice(bookList: List<Book>): Double {
        return bookList.map { book -> book.price }.sum()
    }

    var error: PublishSubject<BaseRepository.RepositoryErrorEvent> = PublishSubject.create()

}