package com.eurosportdemo.app.data.repository

import com.eurosportdemo.app.data.api.Webservice
import com.eurosportdemo.app.data.database.dao.BookDao
import com.eurosportdemo.app.domain.model.Book
import io.reactivex.Flowable
import javax.inject.Inject

class BookRepository @Inject constructor(
    webservice: Webservice,
    private val bookDao: BookDao
) : BaseRepository() {

    val bookListAvailableLocal = bookDao.getBookListAvailable()
    val bookListAvailableDistant = webservice.getAllBooks()

    val bookListInBasket: Flowable<List<Book>> = bookDao.getBookListInBasket()

    fun insertBookList(bookList: List<Book>) {
        bookDao.insertBookList(bookList)
    }

    fun setBookInBasket(book: Book) {
        bookDao.updateBook(book.copy(inBasket = true))
    }

    fun removeBookInBasket(book: Book) {
        bookDao.updateBook(book.copy(inBasket = false))
    }

}