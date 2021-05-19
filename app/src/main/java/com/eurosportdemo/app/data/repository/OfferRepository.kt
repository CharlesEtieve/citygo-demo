package com.eurosportdemo.app.data.repository

import android.text.TextUtils
import com.eurosportdemo.app.data.api.Webservice
import com.eurosportdemo.app.data.database.dao.BookDao
import com.eurosportdemo.app.domain.model.Offer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class OfferRepository @Inject constructor(
    private val webservice: Webservice,
    private val bookDao: BookDao
) : BaseRepository() {

    var offerList: BehaviorSubject<List<Offer>> = BehaviorSubject.create()

    fun load(disposable: CompositeDisposable) {
        bookDao
            .getBookListInBasket()
            .toObservable()
            .map { it.map { book -> book.isbn } }
            .map { TextUtils.join(",", it) }
            .filter { it.isNotEmpty() }
            .flatMap { isbnListPath ->
                webservice
                    .getOffers(isbnListPath)
                    .subscribeOn(Schedulers.io())
            }
            .subscribe { response ->
                if (response.isSuccessful) {
                    response.body()?.offerList?.let {
                        offerList.onNext(it)
                    }
                } else {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        error.onNext(RepositoryErrorEvent.UNKNOWN)
                    }
                }
            }.addTo(disposable)
    }

}