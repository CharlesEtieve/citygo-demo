package com.eurosportdemo.app.presentation.viewModel

import androidx.annotation.StringRes
import com.eurosportdemo.app.domain.model.Book
import com.eurosportdemo.app.domain.useCase.GetBasketUseCase
import com.eurosportdemo.app.domain.useCase.RemoveBasketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(
    removeBasketUseCase: RemoveBasketUseCase,
    getBasketUseCase: GetBasketUseCase
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

        getBasketUseCase
            .basket
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { basket ->
                if (basket.bookList.isEmpty()) {
                    viewState.onNext(ViewState.ShowNoData)
                } else {
                    viewState.onNext(
                        ViewState.ShowBookList(
                            basket.bookList,
                            basket.originalPriceFormatted,
                            basket.bestOfferPriceFormatted
                        )
                    )
                }
            }.addTo(disposable)

        itemClicked
            .withLatestFrom(getBasketUseCase
                .basket.subscribeOn(Schedulers.io())
                .filter { it.bookList.isNotEmpty() }
                , { itemClicked, basket ->
                    basket.bookList[itemClicked]
            })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { book ->
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

}