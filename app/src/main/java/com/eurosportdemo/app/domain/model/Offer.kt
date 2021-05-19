package com.eurosportdemo.app.domain.model

import androidx.room.Entity
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

@Entity
data class Offer(
    @SerializedName("type")
    val type: OfferType,

    @SerializedName("value")
    val value: Double?,

    @SerializedName("sliceValue")
    val sliceValue: Double?,
) {

    enum class OfferType {
        @SerializedName("percentage")
        PERCENTAGE,

        @SerializedName("minus")
        MINUS,

        @SerializedName("slice")
        SLICE
    }

    companion object {
        fun fromJson(json: String?): List<Offer> {
            val gson = GsonBuilder()
            val listType = object : TypeToken<List<Offer>>() {}.type
            return gson.create().fromJson(json, listType)
        }
    }
}

