package com.eurosportdemo.app.domain.useCase

import com.eurosportdemo.app.domain.repository.BookRepository
import com.eurosportdemo.app.domain.model.Book
import javax.inject.Inject

class SetBookInBasketUseCase @Inject constructor(private val bookRepository: BookRepository) {

    var error = bookRepository.error

    fun setBookInBasket(book: Book) = bookRepository.setBookInBasket(book)

}