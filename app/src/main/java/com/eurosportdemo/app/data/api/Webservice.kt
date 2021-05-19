package com.eurosportdemo.app.data.api

import com.eurosportdemo.app.data.api.response.GetOffersResponse
import com.eurosportdemo.app.domain.model.Book
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Webservice {

    @GET("books")
    fun getAllBooks(): Observable<Response<List<Book>>>

    @GET("books/{bookIsbnList}/commercialOffers")
    fun getOffers(@Path("bookIsbnList") bookIsbnList: String): Observable<Response<GetOffersResponse>>

}