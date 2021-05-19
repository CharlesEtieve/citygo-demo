package com.eurosportdemo.app.domain.useCase

import com.eurosportdemo.app.data.repository.OfferRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class GetOfferUseCase @Inject constructor(private val offerRepository: OfferRepository) {

    var offerList = offerRepository.offerList

    var error = offerRepository.error

    fun load(bag: CompositeDisposable) {
        offerRepository.load(bag)
    }

}