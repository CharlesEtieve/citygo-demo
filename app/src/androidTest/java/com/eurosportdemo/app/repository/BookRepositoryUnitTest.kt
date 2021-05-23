package com.eurosportdemo.app.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eurosportdemo.app.data.api.Webservice
import com.eurosportdemo.app.data.database.MyDatabase
import com.eurosportdemo.app.data.database.dao.BookDao
import com.eurosportdemo.app.domain.repository.BookRepository
import com.eurosportdemo.app.domain.model.Book
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.runner.RunWith
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.awaitility.Awaitility
import org.junit.*
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class BookRepositoryUnitTest {

    private lateinit var bookDao: BookDao
    private val webservice: Webservice = mockk(relaxUnitFun = true)
    private lateinit var bookRepository: BookRepository
    private var webserviceResponse: Response<List<Book>> = mockk(relaxUnitFun = true)
    private lateinit var db: MyDatabase
    private lateinit var disposable: CompositeDisposable

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MyDatabase::class.java).build()
        bookDao = db.bookDao()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        every { webserviceResponse.body() } returns buildListBookWebservice()
        every { webserviceResponse.isSuccessful } returns true
        every { webservice.getAllBooks() } returns Observable.just(webserviceResponse)
        bookRepository = BookRepository(bookDao = bookDao, webservice = webservice)
        disposable = CompositeDisposable()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        disposable.clear()
        RxJavaPlugins.reset()
    }

    @Test
    @Throws(InterruptedException::class)
    fun check_all_books_with_no_cache() {
        val subscriber = bookRepository.bookListAvailable.test()
        Awaitility.await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().size == 1 }
        assertEquals(subscriber.values().first(), ArrayList<Book>())
        bookRepository.load(disposable)
        Awaitility.await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().size == 2 }
        assertEquals(subscriber.values()[1], buildListBookWebservice())
    }

    @Test
    fun check_all_books_with_cache() {
        bookDao.insertBookList(buildListBookDatabase())
        val subscriber = bookRepository.bookListAvailable.test()
        Awaitility.await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().size == 1 }
        assertEquals(subscriber.values().first(), buildListBookDatabase())
        bookRepository.load(disposable)
        Awaitility.await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().size == 2 }
        assertEquals(subscriber.values()[1], buildListBookWebservice())
    }

    @Throws(Exception::class)
    fun buildListBookWebservice(): List<Book> {
        val body = loadJsonFixture("mockWebservice.json")
        return Book.fromJson(body)
    }

    @Throws(Exception::class)
    fun buildListBookDatabase(): List<Book> {
        val body = loadJsonFixture("mockDatabase.json")
        return Book.fromJson(body)
    }

    @Throws(IOException::class)
    fun loadJsonFixture(filename: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream(filename)
        return inputStream?.let {
            val scanner = Scanner(it, "UTF-8").useDelimiter("\\A")
            if (scanner.hasNext()) scanner.next() else ""
        } ?: ""
    }
}