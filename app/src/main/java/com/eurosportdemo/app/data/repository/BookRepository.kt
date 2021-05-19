package com.eurosportdemo.app.data.repository

import com.eurosportdemo.app.data.api.Webservice
import com.eurosportdemo.app.data.database.dao.BookDao
import com.eurosportdemo.app.domain.model.Book
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val webservice: Webservice,
    private val bookDao: BookDao
) : BaseRepository() {

    val bookListAvailable = bookDao.getBookListAvailable()

    val bookListInBasket = bookDao.getBookListInBasket()

    fun load(disposable: CompositeDisposable) {
        webservice
            .getAllBooks()
            .subscribeOn(Schedulers.io())
            .subscribe({ response ->
                if (response.isSuccessful) {
                    response.body()?.let {
                        bookDao.insertBookList(it)
                    }
                } else {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        error.onNext(RepositoryErrorEvent.UNKNOWN)
                    }
                }
            }, { throwable ->
                AndroidSchedulers.mainThread().scheduleDirect {
                    when (throwable) {
                        is UnknownHostException -> error.onNext(RepositoryErrorEvent.NETWORK)
                        else -> error.onNext(RepositoryErrorEvent.UNKNOWN)
                    }
                }
            }).addTo(disposable)
    }

    fun setBookInBasket(book: Book) {
        bookDao.updateBook(book.copy(inBasket = true))
    }

    fun removeBookInBasket(book: Book) {
        bookDao.updateBook(book.copy(inBasket = false))
    }

}