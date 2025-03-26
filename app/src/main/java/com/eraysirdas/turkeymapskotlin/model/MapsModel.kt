package com.eraysirdas.turkeymapskotlin.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MapsModel : Serializable {
    @SerializedName("name")
    var name: String? = null
        get() = field

    @SerializedName("cityMapImage")
    var cityMapImage: String? = null

    @SerializedName("districtCount")
    var districtCount: Int = 0

    @SerializedName("hiveCount")
    var hiveCount: Int = 0

    @SerializedName("produceCount")
    var produceCount: Int = 0

    @SerializedName("plateCode")
    var plateCode: String? = null

    @SerializedName("areCode")
    var areCode: String? = null

    @SerializedName("area")
    var area: String? = null

    @SerializedName("mapPath")
    var mapPath: List<String>? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("createdDate")
    var createdDate: String? = null

    @SerializedName("updatedDate")
    var updatedDate: String? = null

    @SerializedName("cityPlants")
    var cityPlants: List<CityPlantModel>? = null
        get()=field

    @SerializedName("cityHoney")
    var cityHoney: List<CityHoneyModel>? = null
        get()=field




}
