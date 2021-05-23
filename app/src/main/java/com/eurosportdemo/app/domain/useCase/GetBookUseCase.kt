package com.eurosportdemo.app.domain.useCase

import com.eurosportdemo.app.domain.repository.BaseRepository
import com.eurosportdemo.app.domain.repository.BookRepository
import com.eurosportdemo.app.domain.model.Book
import io.reactivex.Observable
import javax.inject.Inject

class GetBookUseCase @Inject constructor(private val bookRepository: BookRepository) {

    val bookListAvailable: Observable<List<Book>> = Observable.merge(
        bookRepository
            .bookListAvailableLocal
            .toObservable(),
        bookRepository
            .bookListAvailableDistant
            .map { response ->
                var bookList: List<Book> = ArrayList()
                if (response.isSuccessful) {
                    response.body()?.let {
                        bookList = it
                        bookRepository.insertBookList(it)
                    }
                } else {
                    error.onNext(BaseRepository.RepositoryErrorEvent.UNKNOWN)
                }
                bookList
            }.map {
                it.filter { book -> !book.inBasket }
            }
    )
        .distinctUntilChanged()

    var error = bookRepository.error

}