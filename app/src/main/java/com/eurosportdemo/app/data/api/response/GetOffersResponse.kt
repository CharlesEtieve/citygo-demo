package com.eurosportdemo.app.data.api.response

import com.eurosportdemo.app.domain.model.Offer
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class GetOffersResponse(
    @SerializedName("offers") var offerList: List<Offer>? = null
) {
    companion object {
        fun fromJson(json: String?): GetOffersResponse {
            val gson = GsonBuilder()
            val listType = object : TypeToken<GetOffersResponse>() {}.type
            return gson.create().fromJson(json, listType)
        }
    }
}
