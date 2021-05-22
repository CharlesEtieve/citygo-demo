package com.eurosportdemo.app.domain.useCase

import com.eurosportdemo.app.data.repository.BookRepository
import com.eurosportdemo.app.domain.model.Book
import io.reactivex.Flowable
import javax.inject.Inject

class GetBasketUseCase @Inject constructor(bookRepository: BookRepository) {

    val bookListInBasket: Flowable<List<Book>> = bookRepository.bookListInBasket

    var error = bookRepository.error

}