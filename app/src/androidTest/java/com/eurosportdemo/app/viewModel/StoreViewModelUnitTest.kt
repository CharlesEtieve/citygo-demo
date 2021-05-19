package com.eurosportdemo.app.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eurosportdemo.app.data.repository.BookRepository
import com.eurosportdemo.app.domain.model.Book
import com.eurosportdemo.app.domain.useCase.GetBookUseCase
import com.eurosportdemo.app.domain.useCase.SetBasketUseCase
import com.eurosportdemo.app.presentation.viewModel.StoreViewModel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.awaitility.Awaitility
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class StoreViewModelUnitTest {

    private lateinit var storeViewModel: StoreViewModel

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        val bookRepository: BookRepository = mockk(relaxUnitFun = true)
        every { bookRepository.bookListInBasket } returns Flowable.empty()
        every { bookRepository.bookListAvailable } returns Flowable.just(buildListBook())
        every { bookRepository.error } returns PublishSubject.create()
        storeViewModel = StoreViewModel(getBookUseCase = GetBookUseCase(bookRepository),
            setBasketUseCase = SetBasketUseCase(bookRepository))
    }

    @After
    fun closeDb() {
        RxJavaPlugins.reset()
    }

    @Test
    fun test_view_state() {
        storeViewModel.load()
        val subscriber = storeViewModel.viewState.test()
        Awaitility.await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().isNotEmpty() && subscriber.values().last() is StoreViewModel.ViewState.ShowBookList }
        val state = subscriber.values().last()
        assert(state is StoreViewModel.ViewState.ShowBookList)
        state as StoreViewModel.ViewState.ShowBookList
        Assert.assertEquals(state.bookList, buildListBook())
    }

    @Throws(Exception::class)
    fun buildListBook(): List<Book> {
        val body = loadJsonFixture("mockBook.json")
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