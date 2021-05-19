package com.eurosportdemo.app.domain.useCase

import com.eurosportdemo.app.data.repository.BookRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class GetBookUseCase @Inject constructor(private val bookRepository: BookRepository) {

    var bookListAvailable = bookRepository.bookListAvailable

    var error = bookRepository.error

    fun load(bag: CompositeDisposable) {
        bookRepository.load(bag)
    }

}