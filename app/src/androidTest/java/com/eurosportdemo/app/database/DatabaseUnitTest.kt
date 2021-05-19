package com.eurosportdemo.app.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eurosportdemo.app.data.database.MyDatabase
import com.eurosportdemo.app.data.database.dao.BookDao
import com.eurosportdemo.app.domain.model.Book
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.awaitility.Awaitility.await
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@RunWith(AndroidJUnit4::class)
class DatabaseUnitTest {

    private lateinit var db: MyDatabase
    private lateinit var bookDao: BookDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MyDatabase::class.java
        )
            .build()
        bookDao = db.bookDao()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        RxJavaPlugins.reset()
    }

    @Test
    fun test_insert_book() {
        val subscriber = bookDao.getAllBooks().test()
        await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().size == 1 }
        Assert.assertEquals(subscriber.values().first(), ArrayList<Book>())
        bookDao.insertBookList(buildListBookDatabase())
        await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().size == 2 }
        Assert.assertEquals(subscriber.values()[1], buildListBookDatabase())
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