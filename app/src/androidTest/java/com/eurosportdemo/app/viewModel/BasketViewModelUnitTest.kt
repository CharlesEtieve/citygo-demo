package com.eurosportdemo.app.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eurosportdemo.app.data.api.response.GetOffersResponse
import com.eurosportdemo.app.domain.repository.BookRepository
import com.eurosportdemo.app.domain.repository.OfferRepository
import com.eurosportdemo.app.domain.model.Book
import com.eurosportdemo.app.domain.model.Offer
import com.eurosportdemo.app.domain.useCase.GetBasketUseCase
import com.eurosportdemo.app.domain.useCase.RemoveBasketUseCase
import com.eurosportdemo.app.presentation.viewModel.BasketViewModel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
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
class BasketViewModelUnitTest {

    private lateinit var basketViewModel: BasketViewModel
    private var bookRepository: BookRepository = mockk(relaxUnitFun = true)
    private var offerRepository: OfferRepository = mockk(relaxUnitFun = true)

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        every { bookRepository.bookListAvailable } returns Flowable.empty()
        every { bookRepository.error } returns PublishSubject.create()
        every { offerRepository.error } returns PublishSubject.create()
    }

    @After
    fun closeDb() {
        RxJavaPlugins.reset()
    }

    @Test
    fun test_view_state_percentage() {
        every { offerRepository.offerList } returns BehaviorSubject.createDefault(buildListOffer("mockOfferPercentage.json"))
        every { bookRepository.bookListInBasket } returns Flowable.just(buildListBasketBook())
        basketViewModel = BasketViewModel(getBasketUseCase= GetBasketUseCase(bookRepository),
            removeBasketUseCase= RemoveBasketUseCase(bookRepository),
            getOfferUseCase= GetBasketUseCase(offerRepository)
        )
        basketViewModel.load()
        val subscriber = basketViewModel.viewState.test()
        Awaitility.await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().isNotEmpty() && subscriber.values().last() is BasketViewModel.ViewState.ShowBookList }
        val state = subscriber.values().last()
        assert(state is BasketViewModel.ViewState.ShowBookList)
        state as BasketViewModel.ViewState.ShowBookList
        Assert.assertEquals(state.bookList, buildListBasketBook())
        Assert.assertEquals(state.originalPrice, "124 €")
        Assert.assertEquals(state.offerPrice, "80,6 €")
    }

    @Test
    fun test_view_state_minus() {
        every { offerRepository.offerList } returns BehaviorSubject.createDefault(buildListOffer("mockOfferMinus.json"))
        every { bookRepository.bookListInBasket } returns Flowable.just(buildListBasketBook())
        basketViewModel = BasketViewModel(getBasketUseCase= GetBasketUseCase(bookRepository),
            removeBasketUseCase= RemoveBasketUseCase(bookRepository),
            getOfferUseCase= GetBasketUseCase(offerRepository)
        )
        basketViewModel.load()
        val subscriber = basketViewModel.viewState.test()
        Awaitility.await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().isNotEmpty() && subscriber.values().last() is BasketViewModel.ViewState.ShowBookList }
        val state = subscriber.values().last()
        assert(state is BasketViewModel.ViewState.ShowBookList)
        state as BasketViewModel.ViewState.ShowBookList
        Assert.assertEquals(state.bookList, buildListBasketBook())
        Assert.assertEquals(state.originalPrice, "124 €")
        Assert.assertEquals(state.offerPrice, "74 €")
    }

    @Test
    fun test_view_state_slice() {
        every { offerRepository.offerList } returns BehaviorSubject.createDefault(buildListOffer("mockOfferSlice.json"))
        every { bookRepository.bookListInBasket } returns Flowable.just(buildListBasketBook())
        basketViewModel = BasketViewModel(getBasketUseCase= GetBasketUseCase(bookRepository),
            removeBasketUseCase= RemoveBasketUseCase(bookRepository),
            getOfferUseCase= GetBasketUseCase(offerRepository)
        )
        basketViewModel.load()
        val subscriber = basketViewModel.viewState.test()
        Awaitility.await().timeout(30, TimeUnit.SECONDS).until { subscriber.values().isNotEmpty() && subscriber.values().last() is BasketViewModel.ViewState.ShowBookList }
        val state = subscriber.values().last()
        assert(state is BasketViewModel.ViewState.ShowBookList)
        state as BasketViewModel.ViewState.ShowBookList
        Assert.assertEquals(state.bookList, buildListBasketBook())
        Assert.assertEquals(state.originalPrice, "124 €")
        Assert.assertEquals(state.offerPrice, "84 €")
    }

    @Throws(Exception::class)
    fun buildListOffer(filename: String): List<Offer> {
        val body = loadJsonFixture(filename)
        return GetOffersResponse.fromJson(body).offerList!!
    }

    @Throws(Exception::class)
    fun buildListBasketBook(): List<Book> {
        val body = loadJsonFixture("mockBasketBook.json")
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