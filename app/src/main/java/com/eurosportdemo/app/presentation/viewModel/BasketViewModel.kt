package com.eurosportdemo.app.presentation.viewModel

import androidx.annotation.StringRes
import com.eurosportdemo.app.domain.model.Book
import com.eurosportdemo.app.domain.model.Offer
import com.eurosportdemo.app.domain.useCase.GetBasketUseCase
import com.eurosportdemo.app.domain.useCase.GetOfferUseCase
import com.eurosportdemo.app.domain.useCase.RemoveBasketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*
import com.eurosportdemo.app.domain.model.Offer.OfferType
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(
    private val getBasketUseCase: GetBasketUseCase,
    private val removeBasketUseCase: RemoveBasketUseCase,
    private val getOfferUseCase: GetOfferUseCase
) : BaseViewModel() {
    sealed class ViewState {
        class ShowBookList(
            val bookList: List<Book>,
            val originalPrice: String,
            val offerPrice: String
        ) : ViewState()

        class ShowErrorMessage(@StringRes val message: Int) : ViewState()
        object ShowNoData : ViewState()
    }

    val viewState: BehaviorSubject<ViewState> = BehaviorSubject.create()

    var itemClicked: PublishSubject<Int> = PublishSubject.create()

    init {
        Observable
            .combineLatest(
                getOfferUseCase.offerList.subscribeOn(Schedulers.io()),
                getBasketUseCase.bookListInBasket.toObservable().subscribeOn(Schedulers.io()),
                { offerList, bookInBasketList ->
                    Pair(bookInBasketList, offerList)
                })
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                //calculate price
                Triple(
                    it.first,
                    getOriginalPrice(it.first),
                    getBestOfferPrice(it.first, it.second)
                )
            }
            .map {
                //format price
                Triple(
                    it.first,
                    formatPrice(it.second),
                    formatPrice(it.third)
                )
            }
            .subscribe {
                if (it.first.isEmpty()) {
                    viewState.onNext(ViewState.ShowNoData)
                } else {
                    viewState.onNext(
                        ViewState.ShowBookList(
                            it.first,
                            it.second,
                            it.third
                        )
                    )
                }
            }.addTo(disposable)

        itemClicked
            .withLatestFrom(getBasketUseCase.bookListInBasket.toObservable(), { itemClicked, bookListInBasket ->
                Pair(itemClicked, bookListInBasket)
            })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe {
                val book = it.second[it.first]
                removeBasketUseCase.removeBookInBasket(book)
            }.addTo(disposable)

        getBasketUseCase
            .error
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewState.onNext(ViewState.ShowErrorMessage(it.getErrorResource()))
            }.addTo(disposable)
        viewState.onNext(ViewState.ShowNoData)
    }

    fun load() {
        Schedulers.io().scheduleDirect {
            getBasketUseCase.load(disposable)
            removeBasketUseCase.load(disposable)
            getOfferUseCase.load(disposable)
        }
    }

    private fun getBestOfferPrice(bookList: List<Book>, offerList: List<Offer>): Double {
        val originalPrice = getOriginalPrice(bookList)
        return offerList.map {
            when (it.type) {
                OfferType.PERCENTAGE -> originalPrice - originalPrice * it.value!! / 100
                OfferType.MINUS -> originalPrice - it.value!!
                OfferType.SLICE -> originalPrice - (originalPrice / it.sliceValue!!).toInt() * it.value!!
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
}