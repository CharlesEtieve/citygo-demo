package com.eurosportdemo.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

@Entity
data class Book(
    @PrimaryKey
    @SerializedName("isbn")
    val isbn: String,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "price")
    @SerializedName("price")
    val price: Double,

    @ColumnInfo(name = "cover")
    @SerializedName("cover")
    val cover: String,

    @ColumnInfo(name = "synopsis")
    @SerializedName("synopsis")
    val synopsis: List<String>,

    @ColumnInfo(name = "inBasket")
    val inBasket: Boolean
) {
    companion object {
        fun fromJson(json: String?): List<Book> {
            val gson = GsonBuilder()
            val listType = object : TypeToken<List<Book>>() {}.type
            return gson.create().fromJson(json, listType)
        }
    }
}

