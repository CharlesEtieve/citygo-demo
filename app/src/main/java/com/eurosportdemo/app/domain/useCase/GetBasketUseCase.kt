package com.eurosportdemo.app.domain.useCase

import com.eurosportdemo.app.data.repository.BookRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class GetBasketUseCase @Inject constructor(private val bookRepository: BookRepository) {

    var bookListInBasket = bookRepository.bookListInBasket

    var error = bookRepository.error

    fun load(disposable: CompositeDisposable) {
        bookRepository.load(disposable)
    }

}