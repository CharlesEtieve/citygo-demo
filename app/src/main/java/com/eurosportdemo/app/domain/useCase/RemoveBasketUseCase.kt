package com.eurosportdemo.app.domain.useCase

import com.eurosportdemo.app.data.repository.BookRepository
import com.eurosportdemo.app.domain.model.Book
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RemoveBasketUseCase @Inject constructor(private val bookRepository: BookRepository) {

    var error = bookRepository.error

    fun removeBookInBasket(book: Book) = bookRepository.removeBookInBasket(book)

    fun load(bag: CompositeDisposable) {
        bookRepository.load(bag)
    }
}