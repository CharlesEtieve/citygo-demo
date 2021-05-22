package com.eurosportdemo.app.domain.useCase

import com.eurosportdemo.app.data.repository.BookRepository
import com.eurosportdemo.app.domain.model.Book
import javax.inject.Inject

class SetBasketUseCase @Inject constructor(private val bookRepository: BookRepository) {

    var error = bookRepository.error

    fun setBookInBasket(book: Book) = bookRepository.setBookInBasket(book)

}