package com.eurosportdemo.app.model

import com.eurosportdemo.app.data.api.response.GetOffersResponse
import com.eurosportdemo.app.domain.model.Offer
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.util.*

class OfferUnitTest {

    @Test
    @Throws(Exception::class)
    fun test_parsing() {
        val body = loadJsonFixture("mockOffer.json")
        val offerList = GetOffersResponse.fromJson(body).offerList!!
        assertEquals(offerList.size, 3)
        val percentageOffer = offerList[0]
        assertEquals(percentageOffer.type, Offer.OfferType.PERCENTAGE)
        assertEquals(percentageOffer.value, 4.0)
        val minusOffer = offerList[1]
        assertEquals(minusOffer.type, Offer.OfferType.MINUS)
        assertEquals(minusOffer.value, 15.0)
        val sliceOffer = offerList[2]
        assertEquals(sliceOffer.type, Offer.OfferType.SLICE)
        assertEquals(sliceOffer.sliceValue, 100.0)
        assertEquals(sliceOffer.value, 12.0)
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