package com.eurosportdemo.app.data.database.dao

import androidx.room.*
import com.eurosportdemo.app.domain.model.Book
import io.reactivex.Flowable
import org.jetbrains.annotations.TestOnly

@Dao
interface BookDao {

    @TestOnly
    @Query("SELECT * FROM Book")
    fun getAllBooks(): Flowable<List<Book>>

    @Query("SELECT * FROM Book WHERE inBasket = 0")
    fun getBookListAvailable(): Flowable<List<Book>>

    @Query("SELECT * FROM Book WHERE inBasket = 1")
    fun getBookListInBasket(): Flowable<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookList(bookList: List<Book>)

    @Update
    fun updateBook(book: Book)
}