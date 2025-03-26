package com.eraysirdas.turkeymapskotlin.model.response

import com.google.gson.annotations.SerializedName

class SearchDataByTypeResponse {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("type")
    var type: String? = null
}