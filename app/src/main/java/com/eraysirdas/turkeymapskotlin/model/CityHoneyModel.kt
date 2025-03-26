package com.eraysirdas.turkeymapskotlin.model

import com.google.gson.annotations.SerializedName

class CityHoneyModel {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("cityName")
    var cityName: String? = null

    @SerializedName("honeyVerietyName")
    var honeyVerietyName: String? = null

    @SerializedName("area")
    var area: String? = null

    @SerializedName("createdDate")
    var createdDate: String? = null

    @SerializedName("updatedDate")
    var updatedDate: String? = null
}
