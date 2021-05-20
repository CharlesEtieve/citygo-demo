package com.eurosportdemo.app.presentation.viewModel

import androidx.annotation.StringRes
import com.eurosportdemo.app.domain.model.Book
import com.eurosportdemo.app.domain.useCase.GetBookUseCase
import com.eurosportdemo.app.domain.useCase.SetBasketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val getBookUseCase: GetBookUseCase,
    private val setBasketUseCase: SetBasketUseCase
) : BaseViewModel() {

    sealed class ViewState {
        class ShowBookList(val bookList: List<Book>) : ViewState()
        object ShowNoData : ViewState()
        class ShowErrorMessage(@StringRes val message: Int) : ViewState()
    }

    val viewState: BehaviorSubject<ViewState> = BehaviorSubject.create()

    init {
        getBookUseCase
            .bookListAvailable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                bookList = it
                viewState.onNext(ViewState.ShowBookList(it))
            }.addTo(disposable)
        getBookUseCase
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
            getBookUseCase.load(disposable)
            setBasketUseCase.load(disposable)
        }
    }

    //TODO refactor using lastElement()
    private var bookList: List<Book>? = null
    fun itemClicked(position: Int) {
        bookList?.let {
            val book = it[position]
            Schedulers.io().scheduleDirect {
                setBasketUseCase.setBookInBasket(book)
            }
        }
    }
}