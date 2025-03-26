package com.eraysirdas.turkeymapskotlin.model

import com.google.gson.annotations.SerializedName

class CityPlantModel {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("cityName")
    var cityName: String? = null

    @SerializedName("plantName")
    var plantName: String? = null

    @SerializedName("plantDescription")
    var plantDescription: String? = null

    @SerializedName("plantImageUrl")
    var plantImageUrl: String? = null

    @SerializedName("area")
    var area: String? = null

    @SerializedName("createdDate")
    var createdDate: String? = null

    @SerializedName("updatedDate")
    var updatedDate: String? = null
}
